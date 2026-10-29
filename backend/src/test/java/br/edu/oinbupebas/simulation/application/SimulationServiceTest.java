package br.edu.oinbupebas.simulation.application;

import static org.assertj.core.api.Assertions.assertThat;
import br.edu.oinbupebas.OinbuPebasApplication;
import br.edu.oinbupebas.transit.api.TransitCatalog;
import java.time.Clock;import java.time.Instant;import java.time.LocalDate;import java.time.ZoneOffset;import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationServiceTest {
 @Test void interpolatesDeterministicallyBetweenScheduledStops(){
  var instant=Instant.parse("2026-10-28T11:05:00Z"); // 08:05 em Belém
  var stops=List.of(new TransitCatalog.Stop("a","A",-6,-50,"test"),new TransitCatalog.Stop("b","B",-6,-49,"test"));
  var trip=new TransitCatalog.Trip("12345678-trip","l1","L1","B","synthetic",List.of(new TransitCatalog.StopCall("a","A",1,28800,28800),new TransitCatalog.StopCall("b","B",2,29400,29400)));
  TransitCatalog catalog=new TransitCatalog(){public List<Stop> stops(){return stops;}public List<Line> lines(){return List.of();}public Timetable timetable(LocalDate date){return new Timetable(date,stops,List.of(trip));}};
  var position=new SimulationService(Clock.fixed(instant,ZoneOffset.UTC),catalog).positions().getFirst();
  assertThat(position.longitude()).isEqualTo(-49.5);assertThat(position.nextStop()).isEqualTo("B");assertThat(position.etaMinutes()).isEqualTo(5);assertThat(position.simulated()).isTrue();
 }

 @Test void followsOnlyTheCurrentRoadSegment(){
  var instant=Instant.parse("2026-10-28T11:05:00Z");
  var stops=List.of(new TransitCatalog.Stop("a","A",-6,-50,"test"),new TransitCatalog.Stop("b","B",-6,-49,"test"),new TransitCatalog.Stop("c","C",-6,-48,"test"));
  var trip=new TransitCatalog.Trip("road-segment","l1","L1","B","synthetic",List.of(
    new TransitCatalog.StopCall("a","A",1,28800,28800),new TransitCatalog.StopCall("b","B",2,29400,29400),new TransitCatalog.StopCall("c","C",3,30000,30000)),
    List.of(new TransitCatalog.Coordinate(-6,-50),new TransitCatalog.Coordinate(-6,-49.9),new TransitCatalog.Coordinate(-6,-49),new TransitCatalog.Coordinate(-6,-48)));
  TransitCatalog catalog=new TransitCatalog(){public List<Stop> stops(){return stops;}public List<Line> lines(){return List.of();}public Timetable timetable(LocalDate date){return new Timetable(date,stops,List.of(trip));}};
  var position=new SimulationService(Clock.fixed(instant,ZoneOffset.UTC),catalog).positions().getFirst();
  assertThat(position.longitude()).isBetween(-49.6,-49.4);assertThat(position.nextStop()).isEqualTo("B");
 }
}
