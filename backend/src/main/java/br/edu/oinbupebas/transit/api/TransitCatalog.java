package br.edu.oinbupebas.transit.api;

import java.util.List;
import java.time.LocalDate;

public interface TransitCatalog {
  List<Stop> stops();
  List<Line> lines();
  Timetable timetable(LocalDate serviceDate);
  record Stop(String id, String name, double latitude, double longitude, String provenance) {}
  record Line(String id, String shortName, String name, String color, String provenance, List<String> stopIds) {}
  record StopCall(String stopId, String stopName, int sequence, int arrivalSeconds, int departureSeconds) {}
  record Coordinate(double latitude, double longitude) {}
  record Trip(String id, String lineId, String lineShortName, String direction, String provenance,
              List<StopCall> calls, List<Coordinate> geometry) {
    public Trip(String id, String lineId, String lineShortName, String direction, String provenance, List<StopCall> calls) {
      this(id,lineId,lineShortName,direction,provenance,calls,List.of());
    }
  }
  record Timetable(LocalDate serviceDate, List<Stop> stops, List<Trip> trips) {}
}
