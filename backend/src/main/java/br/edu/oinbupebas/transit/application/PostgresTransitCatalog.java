package br.edu.oinbupebas.transit.application;

import br.edu.oinbupebas.transit.api.TransitCatalog;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostgresTransitCatalog implements TransitCatalog {
  private final JdbcClient jdbc;

  public PostgresTransitCatalog(JdbcClient jdbc) { this.jdbc = jdbc; }

  @Override @Transactional(readOnly = true)
  public List<Stop> stops() {
    return jdbc.sql("""
        select id,name,ST_Y(location::geometry) latitude,ST_X(location::geometry) longitude,
               provenance::text provenance from transit_stop order by name
        """).query(PostgresTransitCatalog::stop).list();
  }

  @Override @Transactional(readOnly = true)
  public List<Line> lines() {
    var rows = jdbc.sql("""
        select l.id,l.short_name,l.name,l.color,l.provenance::text provenance,ps.stop_id
        from transit_line l
        left join transit_pattern p on p.line_id=l.id and p.direction_id=0
        left join transit_pattern_stop ps on ps.pattern_id=p.id
        order by l.short_name,ps.stop_sequence
        """).query((rs,n) -> new LineRow(rs.getString("id"),rs.getString("short_name"),
          rs.getString("name"),rs.getString("color"),rs.getString("provenance"),rs.getString("stop_id"))).list();
    Map<String,LineBuilder> grouped = new LinkedHashMap<>();
    for (var row : rows) {
      var line = grouped.computeIfAbsent(row.id(), ignored -> new LineBuilder(row));
      if (row.stopId() != null) line.stopIds.add(row.stopId());
    }
    return grouped.values().stream().map(LineBuilder::build).toList();
  }

  @Override @Transactional(readOnly = true)
  public Timetable timetable(LocalDate serviceDate) {
    var rows = jdbc.sql("""
        select t.id::text trip_id,l.id line_id,l.short_name,p.headsign,p.provenance::text provenance,
               ST_AsText(p.geometry) geometry_wkt,
               ps.stop_id,s.name stop_name,ps.stop_sequence,
               t.service_start_seconds+st.arrival_offset_seconds arrival_seconds,
               t.service_start_seconds+st.departure_offset_seconds departure_seconds,
               c.monday,c.tuesday,c.wednesday,c.thursday,c.friday,c.saturday,c.sunday
        from transit_trip t
        join transit_calendar c on c.id=t.calendar_id
        join transit_pattern p on p.id=t.pattern_id
        join transit_line l on l.id=p.line_id
        join transit_stop_time st on st.trip_id=t.id
        join transit_pattern_stop ps on ps.pattern_id=p.id and ps.stop_sequence=st.stop_sequence
        join transit_stop s on s.id=ps.stop_id
        where :serviceDate between c.start_date and c.end_date
        order by t.service_start_seconds,t.id,ps.stop_sequence
        """).param("serviceDate",serviceDate).query((rs,n) -> tripRow(rs)).list();
    Map<String,TripBuilder> trips = new LinkedHashMap<>();
    for (var row : rows) {
      if (!row.operates(serviceDate.getDayOfWeek())) continue;
      trips.computeIfAbsent(row.tripId(), ignored -> new TripBuilder(row)).calls.add(
        new StopCall(row.stopId(),row.stopName(),row.sequence(),row.arrivalSeconds(),row.departureSeconds()));
    }
    return new Timetable(serviceDate,stops(),trips.values().stream().map(TripBuilder::build).toList());
  }

  private static Stop stop(ResultSet rs,int row) throws SQLException {
    return new Stop(rs.getString("id"),rs.getString("name"),rs.getDouble("latitude"),
      rs.getDouble("longitude"),rs.getString("provenance"));
  }

  private static TripRow tripRow(ResultSet rs) throws SQLException {
    return new TripRow(rs.getString("trip_id"),rs.getString("line_id"),rs.getString("short_name"),
      rs.getString("headsign"),rs.getString("provenance"),rs.getString("geometry_wkt"),rs.getString("stop_id"),
      rs.getString("stop_name"),rs.getInt("stop_sequence"),rs.getInt("arrival_seconds"),
      rs.getInt("departure_seconds"),rs.getBoolean("monday"),rs.getBoolean("tuesday"),
      rs.getBoolean("wednesday"),rs.getBoolean("thursday"),rs.getBoolean("friday"),
      rs.getBoolean("saturday"),rs.getBoolean("sunday"));
  }

  private record LineRow(String id,String shortName,String name,String color,String provenance,String stopId) {}
  private static final class LineBuilder {
    private final LineRow row; private final List<String> stopIds = new ArrayList<>();
    private LineBuilder(LineRow row) { this.row=row; }
    private Line build() { return new Line(row.id,row.shortName,row.name,row.color,row.provenance,List.copyOf(stopIds)); }
  }
  private record TripRow(String tripId,String lineId,String lineShortName,String direction,String provenance,String geometryWkt,
      String stopId,String stopName,int sequence,int arrivalSeconds,int departureSeconds,
      boolean monday,boolean tuesday,boolean wednesday,boolean thursday,boolean friday,boolean saturday,boolean sunday) {
    boolean operates(DayOfWeek day) { return switch(day) {
      case MONDAY -> monday; case TUESDAY -> tuesday; case WEDNESDAY -> wednesday;
      case THURSDAY -> thursday; case FRIDAY -> friday; case SATURDAY -> saturday; case SUNDAY -> sunday;
    }; }
  }
  private static final class TripBuilder {
    private final TripRow row; private final List<StopCall> calls = new ArrayList<>();
    private TripBuilder(TripRow row) { this.row=row; }
    private Trip build() { return new Trip(row.tripId,row.lineId,row.lineShortName,row.direction,row.provenance,List.copyOf(calls),parseGeometry(row.geometryWkt)); }
  }

  private static List<TransitCatalog.Coordinate> parseGeometry(String wkt) {
    if (wkt == null || !wkt.startsWith("LINESTRING")) return List.of();
    int start=wkt.indexOf('('), end=wkt.lastIndexOf(')');
    if (start<0 || end<0) return List.of();
    var result=new ArrayList<TransitCatalog.Coordinate>();
    for (String pair : wkt.substring(start+1,end).split(",")) {
      String[] values=pair.trim().split("\\s+");
      if (values.length>=2) result.add(new TransitCatalog.Coordinate(Double.parseDouble(values[1]),Double.parseDouble(values[0])));
    }
    return List.copyOf(result);
  }
}
