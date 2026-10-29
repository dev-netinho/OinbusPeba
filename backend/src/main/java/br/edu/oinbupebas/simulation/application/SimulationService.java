package br.edu.oinbupebas.simulation.application;

import br.edu.oinbupebas.OinbuPebasApplication;
import br.edu.oinbupebas.simulation.api.VehiclePosition;
import br.edu.oinbupebas.transit.api.TransitCatalog;
import br.edu.oinbupebas.transit.api.TransitGeometry;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {
  private final Clock clock; private final TransitCatalog catalog;
  SimulationService(Clock clock,TransitCatalog catalog){this.clock=clock;this.catalog=catalog;}

  public java.util.List<VehiclePosition> positions(){
    var now=clock.instant();var local=LocalDateTime.ofInstant(now,OinbuPebasApplication.ZONE);
    var timetable=catalog.timetable(local.toLocalDate());
    Map<String,TransitCatalog.Stop> stops=timetable.stops().stream().collect(Collectors.toMap(TransitCatalog.Stop::id,s->s));
    int second=local.toLocalTime().toSecondOfDay();var result=new ArrayList<VehiclePosition>();
    timetable.trips().stream().filter(trip->!trip.calls().isEmpty()&&trip.calls().getFirst().departureSeconds()<=second&&trip.calls().getLast().arrivalSeconds()>=second)
      .sorted(Comparator.comparing(TransitCatalog.Trip::id)).limit(12).forEach(trip->result.add(position(trip,stops,second,now)));
    return result;
  }

  private static VehiclePosition position(TransitCatalog.Trip trip,Map<String,TransitCatalog.Stop> stops,int nowSecond,java.time.Instant calculatedAt){
    var calls=trip.calls();var first=stops.get(calls.getFirst().stopId());
    double latitude=first.latitude(),longitude=first.longitude();String nextStop=calls.getFirst().stopName();int eta=0;
    for(int index=0;index<calls.size();index++){
      var call=calls.get(index);var current=stops.get(call.stopId());
      if(nowSecond<=call.departureSeconds()){
        latitude=current.latitude();longitude=current.longitude();nextStop=call.stopName();eta=Math.max(0,(call.arrivalSeconds()-nowSecond+59)/60);break;
      }
      if(index+1<calls.size()){
        var following=calls.get(index+1);var target=stops.get(following.stopId());
        if(nowSecond<following.arrivalSeconds()){
          double progress=(nowSecond-call.departureSeconds())/(double)(following.arrivalSeconds()-call.departureSeconds());
          progress=Math.max(0,Math.min(1,progress));
          var geometry=TransitGeometry.segment(trip,index,index+1,stops);
          if (geometry.size() >= 2) {
            var point=pointAlong(geometry, progress);
            latitude=point.latitude(); longitude=point.longitude();
          } else {
            latitude=current.latitude()+(target.latitude()-current.latitude())*progress;
            longitude=current.longitude()+(target.longitude()-current.longitude())*progress;
          }
          nextStop=following.stopName();eta=Math.max(1,(following.arrivalSeconds()-nowSecond+59)/60);break;
        }
      }
    }
    return new VehiclePosition("SIM-"+trip.id().substring(0,Math.min(8,trip.id().length())).toUpperCase(),trip.lineShortName(),trip.direction(),latitude,longitude,nextStop,eta,calculatedAt,true);
  }

  private static TransitCatalog.Coordinate pointAlong(java.util.List<TransitCatalog.Coordinate> points,double progress) {
    double total=0; for(int i=1;i<points.size();i++) total += distance(points.get(i-1),points.get(i));
    double remaining=total*progress;
    for(int i=1;i<points.size();i++) { var a=points.get(i-1); var b=points.get(i); double segment=distance(a,b);
      if(remaining<=segment || i==points.size()-1) { double f=segment==0?0:remaining/segment; return new TransitCatalog.Coordinate(a.latitude()+(b.latitude()-a.latitude())*f,a.longitude()+(b.longitude()-a.longitude())*f); }
      remaining-=segment;
    } return points.getLast();
  }
  private static double distance(TransitCatalog.Coordinate a,TransitCatalog.Coordinate b) { double dy=b.latitude()-a.latitude(),dx=(b.longitude()-a.longitude())*Math.cos(Math.toRadians((a.latitude()+b.latitude())/2)); return Math.sqrt(dx*dx+dy*dy); }
}
