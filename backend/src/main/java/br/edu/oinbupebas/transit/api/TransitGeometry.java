package br.edu.oinbupebas.transit.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Recorte determinístico de uma geometria roteada por ocorrência ordenada de parada. */
public final class TransitGeometry {
  private TransitGeometry() {}

  public static List<TransitCatalog.Coordinate> segment(TransitCatalog.Trip trip, int fromCall,
      int toCall, Map<String, TransitCatalog.Stop> stops) {
    if (trip.geometry().size() < 2 || fromCall >= toCall) return trip.geometry();
    int[] occurrences = occurrences(trip, stops);
    int start = occurrences[fromCall], end = occurrences[toCall];
    if (end < start) return List.of();
    var result = new ArrayList<TransitCatalog.Coordinate>();
    var from = stops.get(trip.calls().get(fromCall).stopId());
    var to = stops.get(trip.calls().get(toCall).stopId());
    result.add(new TransitCatalog.Coordinate(from.latitude(), from.longitude()));
    if (end > start + 1) result.addAll(trip.geometry().subList(start + 1, end));
    result.add(new TransitCatalog.Coordinate(to.latitude(), to.longitude()));
    return List.copyOf(result);
  }

  /** Cada ocorrência só pode avançar na polilinha; paradas repetidas usam a próxima ocorrência. */
  public static int[] occurrences(TransitCatalog.Trip trip, Map<String, TransitCatalog.Stop> stops) {
    int[] result = new int[trip.calls().size()];
    int previous = -1;
    for (int call = 0; call < trip.calls().size(); call++) {
      int minimum = previous + 1;
      int maximum = trip.geometry().size() - (trip.calls().size() - call);
      if (maximum < minimum) maximum = trip.geometry().size() - 1;
      int best = minimum, bestDistance = Integer.MAX_VALUE;
      var stop = stops.get(trip.calls().get(call).stopId());
      for (int point = minimum; point <= maximum; point++) {
        int distance = squaredDistance(trip.geometry().get(point), stop);
        if (distance < bestDistance) { best = point; bestDistance = distance; }
      }
      result[call] = best;
      previous = best;
    }
    return result;
  }

  private static int squaredDistance(TransitCatalog.Coordinate point, TransitCatalog.Stop stop) {
    double lat = point.latitude() - stop.latitude();
    double lon = point.longitude() - stop.longitude();
    return (int) Math.min(Integer.MAX_VALUE, Math.round((lat * lat + lon * lon) * 1_000_000_000));
  }
}
