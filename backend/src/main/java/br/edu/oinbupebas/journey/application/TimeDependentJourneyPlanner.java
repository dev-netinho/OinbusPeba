package br.edu.oinbupebas.journey.application;

import br.edu.oinbupebas.journey.api.JourneyPlanner;
import br.edu.oinbupebas.transit.api.TransitCatalog;
import br.edu.oinbupebas.transit.api.TransitCatalog.Stop;
import br.edu.oinbupebas.transit.api.TransitCatalog.StopCall;
import br.edu.oinbupebas.transit.api.TransitCatalog.Trip;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.springframework.stereotype.Service;

/** Busca temporal exata para o pequeno catálogo demonstrativo.
 * Complexidade O(L × T × S²), onde L são rótulos, T viagens e S paradas por viagem.
 */
@Service
public class TimeDependentJourneyPlanner implements JourneyPlanner {
  static final int MIN_CONNECTION_SECONDS = 180;
  static final double WALK_METERS_PER_SECOND = 1.2;
  static final double WALK_DISCOVERY_FACTOR = 1.25;
  private static final String WALK_NOTE = "Aproximação geodésica conservadora; não é navegação pedestre validada";
  private final TransitCatalog catalog;

  public TimeDependentJourneyPlanner(TransitCatalog catalog) { this.catalog = catalog; }

  @Override public List<Journey> plan(Query query) {
    validate(query);
    var timetable = catalog.timetable(query.serviceDate());
    var stops = timetable.stops().stream().collect(java.util.stream.Collectors.toMap(Stop::id,s -> s));
    var origin = requiredStop(stops,query.originId(),"Origem");
    var destination = requiredStop(stops,query.destinationId(),"Destino");
    if (origin.id().equals(destination.id())) return List.of();

    Map<String,List<Boarding>> departures = index(timetable.trips());
    var queue = new PriorityQueue<State>(Comparator.comparingInt(State::atSeconds));
    var best = new HashMap<String,Integer>();
    int requested = query.departure().toSecondOfDay();
    for (var stop : timetable.stops()) {
      int meters = walkingDistance(origin,stop);
      if (meters <= query.maxWalkMeters()) {
        int seconds = walkSeconds(meters);
        var legs = new ArrayList<TimedLeg>();
        if (meters > 0) legs.add(TimedLeg.walk(origin.name(),stop.name(),requested,requested+seconds,meters));
        var state = new State(stop.id(),requested+seconds,0,seconds,0,0,List.copyOf(legs),null);
        queue.add(state); best.put(key(state),state.atSeconds());
      }
    }

    var candidates = new ArrayList<Candidate>();
    Set<String> signatures = new HashSet<>();
    while (!queue.isEmpty() && candidates.size() < 12) {
      var state = queue.poll();
      if (state.atSeconds() > best.getOrDefault(key(state),Integer.MAX_VALUE)) continue;
      if (state.boardings() > 0) {
        int finalMeters = walkingDistance(stops.get(state.stopId()),destination);
        if (finalMeters <= query.maxWalkMeters()) {
          int finalSeconds = walkSeconds(finalMeters);
          var legs = new ArrayList<>(state.legs());
          if (finalMeters > 0) legs.add(TimedLeg.walk(stops.get(state.stopId()).name(),destination.name(),state.atSeconds(),state.atSeconds()+finalSeconds,finalMeters));
          var candidate = new Candidate(requested,state.atSeconds()+finalSeconds,state.boardings()-1,
            state.walkSeconds()+finalSeconds,state.waitSeconds(),state.rideSeconds(),List.copyOf(legs));
          if (signatures.add(candidate.signature())) candidates.add(candidate);
        }
      }
      if (state.boardings() >= query.maxTransfers()+1) continue;
      for (var boarding : departures.getOrDefault(state.stopId(),List.of())) {
        if (boarding.trip().id().equals(state.lastTripId())) continue;
        int ready = state.atSeconds() + (state.boardings() == 0 ? 0 : MIN_CONNECTION_SECONDS);
        if (boarding.call().departureSeconds() < ready) continue;
        int wait = boarding.call().departureSeconds()-state.atSeconds();
        for (int next=boarding.index()+1;next<boarding.trip().calls().size();next++) {
          var arrival = boarding.trip().calls().get(next);
          var legs = new ArrayList<>(state.legs());
          if (state.boardings()>0) legs.add(TimedLeg.transfer(boarding.call().stopName(),state.atSeconds(),boarding.call().departureSeconds()));
          legs.add(TimedLeg.transit(boarding.call().stopName(),arrival.stopName(),boarding.call().departureSeconds(),
            arrival.arrivalSeconds(),boarding.trip().lineShortName(),boarding.trip().direction(),boarding.trip().provenance(),boarding.trip().geometry()));
          var advanced = new State(arrival.stopId(),arrival.arrivalSeconds(),state.boardings()+1,state.walkSeconds(),
            state.waitSeconds()+wait,state.rideSeconds()+arrival.arrivalSeconds()-boarding.call().departureSeconds(),
            List.copyOf(legs),boarding.trip().id());
          var stateKey=key(advanced);
          if (advanced.atSeconds() < best.getOrDefault(stateKey,Integer.MAX_VALUE)) {
            best.put(stateKey,advanced.atSeconds()); queue.add(advanced);
          }
        }
      }
    }
    return selectAlternatives(candidates,query.preference());
  }

  private static List<Journey> selectAlternatives(List<Candidate> candidates,String preference) {
    if (candidates.isEmpty()) return List.of();
    var selected = new LinkedHashMap<String,Candidate>();
    var fastest = candidates.stream().min(Comparator.comparingInt(Candidate::duration)).orElseThrow();
    var leastTransfers = candidates.stream().min(Comparator.comparingInt(Candidate::transfers).thenComparingInt(Candidate::duration)).orElseThrow();
    var leastWalking = candidates.stream().min(Comparator.comparingInt(Candidate::walkSeconds).thenComparingInt(Candidate::duration)).orElseThrow();
    if ("FEWER_TRANSFERS".equalsIgnoreCase(preference)) selected.put(leastTransfers.signature(),leastTransfers);
    else if ("LESS_WALKING".equalsIgnoreCase(preference)) selected.put(leastWalking.signature(),leastWalking);
    else selected.put(fastest.signature(),fastest);
    selected.put(fastest.signature(),fastest);
    selected.put(leastTransfers.signature(),leastTransfers);
    selected.put(leastWalking.signature(),leastWalking);
    candidates.stream().sorted(Comparator.comparingInt(Candidate::duration)).forEach(c -> { if(selected.size()<3) selected.putIfAbsent(c.signature(),c); });
    var result = new ArrayList<Journey>(); int position=0;
    for (var candidate : selected.values()) {
      String label = candidate==fastest ? "Melhor horário" : candidate==leastTransfers ? "Menos baldeações" : candidate==leastWalking ? "Menos caminhada" : "Alternativa programada";
      result.add(candidate.toJourney(label,position++));
      if (result.size()==3) break;
    }
    return result;
  }

  private static Map<String,List<Boarding>> index(List<Trip> trips) {
    Map<String,List<Boarding>> result = new HashMap<>();
    for (var trip : trips) for (int index=0;index<trip.calls().size()-1;index++) {
      var call=trip.calls().get(index); result.computeIfAbsent(call.stopId(),ignored -> new ArrayList<>()).add(new Boarding(trip,index,call));
    }
    result.values().forEach(list -> list.sort(Comparator.comparingInt(b -> b.call().departureSeconds())));
    return result;
  }

  private static int walkingDistance(Stop a,Stop b) {
    double lat1=Math.toRadians(a.latitude()),lat2=Math.toRadians(b.latitude());
    double dLat=lat2-lat1,dLon=Math.toRadians(b.longitude()-a.longitude());
    double h=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(lat1)*Math.cos(lat2)*Math.sin(dLon/2)*Math.sin(dLon/2);
    return (int)Math.round(6_371_000*2*Math.atan2(Math.sqrt(h),Math.sqrt(1-h))*WALK_DISCOVERY_FACTOR);
  }
  private static int walkSeconds(int meters) { return (int)Math.ceil(meters/WALK_METERS_PER_SECOND); }
  private static String key(State state) { return state.stopId()+"|"+state.boardings(); }
  private static Stop requiredStop(Map<String,Stop> stops,String id,String label) {
    var stop=stops.get(id); if(stop==null) throw new IllegalArgumentException(label+" não encontrada no catálogo ativo."); return stop;
  }
  private static void validate(Query q) {
    if(q.maxWalkMeters()<100||q.maxWalkMeters()>3000) throw new IllegalArgumentException("Caminhada máxima deve ficar entre 100 e 3.000 m.");
    if(q.maxTransfers()<0||q.maxTransfers()>2) throw new IllegalArgumentException("São permitidas até duas baldeações.");
  }
  private static LocalTime time(int seconds) { return LocalTime.ofSecondOfDay(Math.floorMod(seconds,86_400)); }

  private record Boarding(Trip trip,int index,StopCall call) {}
  private record State(String stopId,int atSeconds,int boardings,int walkSeconds,int waitSeconds,int rideSeconds,List<TimedLeg> legs,String lastTripId) {}
  private record TimedLeg(String mode,String from,String to,int departure,int arrival,String line,String direction,int distance,String provenance,List<TransitCatalog.Coordinate> geometry) {
    static TimedLeg walk(String from,String to,int dep,int arr,int meters){return new TimedLeg("WALK",from,to,dep,arr,null,null,meters,WALK_NOTE,List.of());}
    static TimedLeg transfer(String stop,int dep,int arr){return new TimedLeg("TRANSFER",stop,stop,dep,arr,null,null,0,"Tempo mínimo de conexão e espera programada",List.of());}
    static TimedLeg transit(String from,String to,int dep,int arr,String line,String direction,String provenance,List<TransitCatalog.Coordinate> geometry){return new TimedLeg("TRANSIT",from,to,dep,arr,line,direction,0,provenance,geometry);}
    Leg publicLeg(){return new Leg(mode,from,to,time(departure),time(arrival),line,direction,distance,provenance,geometry);}
  }
  private record Candidate(int start,int arrival,int transfers,int walkSeconds,int waitSeconds,int rideSeconds,List<TimedLeg> legs) {
    int duration(){return arrival-start;}
    String signature(){return legs.stream().filter(l -> l.line()!=null).map(TimedLeg::line).reduce((a,b)->a+">"+b).orElse("walk");}
    Journey toJourney(String label,int index){return new Journey("journey-"+Integer.toHexString(signature().hashCode())+"-"+index,time(start),time(arrival),
      (int)Math.ceil(duration()/60.0),(int)Math.ceil(walkSeconds/60.0),(int)Math.ceil(waitSeconds/60.0),
      (int)Math.ceil(rideSeconds/60.0),transfers,label,legs.stream().map(TimedLeg::publicLeg).toList());}
  }
}
