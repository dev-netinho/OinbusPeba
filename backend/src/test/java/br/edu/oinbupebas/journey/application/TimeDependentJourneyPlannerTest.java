package br.edu.oinbupebas.journey.application;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.oinbupebas.journey.api.JourneyPlanner;
import br.edu.oinbupebas.transit.api.TransitCatalog;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeDependentJourneyPlannerTest {
  private static final LocalDate DATE=LocalDate.of(2026,10,28);
  private static final List<TransitCatalog.Stop> STOPS=List.of(
    stop("a",0,0),stop("b",0,0.01),stop("c",0,0.02),stop("d",0,0.03),stop("near-a",0,0.0003),stop("isolated",1,1));

  @Test void plansDirectJourneyForArbitraryStopIds(){
    var result=planner(trip("direct","L1","Destino",call("a",8,0),call("d",8,30))).plan(query("a","d",7,55,0));
    assertThat(result).hasSize(1); assertThat(result.getFirst().transfers()).isZero();
    assertThat(result.getFirst().legs()).extracting(JourneyPlanner.Leg::line).contains("L1");
  }

  @Test void plansOneTransferOnlyWhenConnectionIsFeasible(){
    var result=planner(trip("ab","L1","B",call("a",8,0),call("b",8,10)),trip("bd","L2","D",call("b",8,14),call("d",8,30)))
      .plan(query("a","d",7,55,1));
    assertThat(result.getFirst().transfers()).isEqualTo(1);
    assertThat(result.getFirst().legs()).extracting(JourneyPlanner.Leg::line).contains("L1","L2");
  }

  @Test void plansTwoTransfers(){
    var result=planner(trip("ab","L1","B",call("a",8,0),call("b",8,10)),
      trip("bc","L2","C",call("b",8,14),call("c",8,20)),trip("cd","L3","D",call("c",8,24),call("d",8,35)))
      .plan(query("a","d",7,55,2));
    assertThat(result.getFirst().transfers()).isEqualTo(2);
  }

  @Test void skipsMissedDeparture(){
    var result=planner(trip("early","L1","D",call("a",8,0),call("d",8,30)),trip("next","L1","D",call("a",9,0),call("d",9,30)))
      .plan(query("a","d",8,1,0));
    assertThat(result.getFirst().legs().stream().filter(l->l.line()!=null).findFirst().orElseThrow().departure()).isEqualTo(LocalTime.of(9,0));
  }

  @Test void rejectsConnectionBelowMinimumThreeMinutes(){
    var result=planner(trip("ab","L1","B",call("a",8,0),call("b",8,10)),trip("lost","L2","D",call("b",8,12),call("d",8,30)))
      .plan(query("a","d",7,55,1));
    assertThat(result).isEmpty();
  }

  @Test void reportsNoServiceAndDisconnectedStops(){
    var planner=planner(trip("day","L1","D",call("a",8,0),call("d",8,30)));
    assertThat(planner.plan(query("a","d",21,0,0))).isEmpty();
    assertThat(planner.plan(query("isolated","d",7,0,2))).isEmpty();
  }

  @Test void appliesMaximumWalkingLimitToAccessStops(){
    var planner=planner(trip("day","L1","D",call("a",8,0),call("d",8,30)));
    assertThat(planner.plan(new JourneyPlanner.Query("near-a","d",DATE,LocalTime.of(7,50),100,0,"FASTEST"))).isNotEmpty();
    assertThat(planner.plan(new JourneyPlanner.Query("b","d",DATE,LocalTime.of(7,50),100,0,"FASTEST"))).isEmpty();
  }

  @Test void neverInvertsDirection(){
    assertThat(planner(trip("reverse","L1","A",call("d",8,0),call("a",8,30))).plan(query("a","d",7,50,0))).isEmpty();
  }

  @Test void supportsOffsetsAfterMidnight(){
    var result=planner(trip("night","N1","D",callSeconds("a",25*3600),callSeconds("d",25*3600+1800)))
      .plan(query("a","d",23,50,0));
    assertThat(result).isNotEmpty(); assertThat(result.getFirst().arrival()).isEqualTo(LocalTime.of(1,30));
  }

  private static TimeDependentJourneyPlanner planner(TransitCatalog.Trip... trips){return new TimeDependentJourneyPlanner(new FakeCatalog(List.of(trips)));}
  private static JourneyPlanner.Query query(String from,String to,int hour,int minute,int transfers){return new JourneyPlanner.Query(from,to,DATE,LocalTime.of(hour,minute),100,transfers,"FASTEST");}
  private static TransitCatalog.Stop stop(String id,double lat,double lon){return new TransitCatalog.Stop(id,id.toUpperCase(),lat,lon,"test");}
  private static TransitCatalog.StopCall call(String id,int hour,int minute){return callSeconds(id,hour*3600+minute*60);}
  private static TransitCatalog.StopCall callSeconds(String id,int seconds){return new TransitCatalog.StopCall(id,id.toUpperCase(),0,seconds,seconds);}
  private static TransitCatalog.Trip trip(String id,String line,String direction,TransitCatalog.StopCall... raw){
    var calls=new ArrayList<TransitCatalog.StopCall>(); for(int i=0;i<raw.length;i++){var c=raw[i];calls.add(new TransitCatalog.StopCall(c.stopId(),c.stopName(),i+1,c.arrivalSeconds(),c.departureSeconds()));}
    return new TransitCatalog.Trip(id,line,line,direction,"synthetic-test",List.copyOf(calls));
  }
  private record FakeCatalog(List<TransitCatalog.Trip> trips) implements TransitCatalog {
    public List<Stop> stops(){return STOPS;} public List<Line> lines(){return List.of();}
    public Timetable timetable(LocalDate date){return new Timetable(date,STOPS,trips);}
  }
}
