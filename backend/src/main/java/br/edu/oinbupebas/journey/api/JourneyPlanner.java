package br.edu.oinbupebas.journey.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface JourneyPlanner {
  List<Journey> plan(Query query);
  record Query(String originId, String destinationId, LocalDate serviceDate, LocalTime departure, int maxWalkMeters, int maxTransfers, String preference) {}
  record Leg(String mode, String from, String to, LocalTime departure, LocalTime arrival, String line, String direction, int distanceMeters, String provenance) {}
  record Journey(String id, LocalTime departure, LocalTime arrival, int durationMinutes, int walkingMinutes, int waitingMinutes, int ridingMinutes, int transfers, String label, List<Leg> legs) {}
}
