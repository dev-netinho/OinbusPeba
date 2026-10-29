package br.edu.oinbupebas.transit.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TransitGeometryTest {
  @Test void repeatedStopUsesTheNextOrderedOccurrence() {
    var stopA=new TransitCatalog.Stop("a","A",0,0,"test");
    var stopB=new TransitCatalog.Stop("b","B",0,2,"test");
    var trip=new TransitCatalog.Trip("loop","L","L","B","test",
      List.of(new TransitCatalog.StopCall("a","A",1,0,0),new TransitCatalog.StopCall("a","A",2,60,60),new TransitCatalog.StopCall("b","B",3,120,120)),
      List.of(new TransitCatalog.Coordinate(0,0),new TransitCatalog.Coordinate(0,1),new TransitCatalog.Coordinate(0,0),new TransitCatalog.Coordinate(0,2)));
    var stops=Map.of("a",stopA,"b",stopB);
    assertThat(TransitGeometry.occurrences(trip,stops)).containsExactly(0,2,3);
    var segment=TransitGeometry.segment(trip,1,2,stops);
    assertThat(segment).extracting(TransitCatalog.Coordinate::longitude).containsExactly(0.0,2.0);
  }

  @Test void segmentEndpointsAreExactStopsAndInteriorVerticesArePreserved() {
    var a=new TransitCatalog.Stop("a","A",-6,-50,"test");
    var b=new TransitCatalog.Stop("b","B",-6,-49,"test");
    var trip=new TransitCatalog.Trip("segment","L","L","B","test",
      List.of(new TransitCatalog.StopCall("a","A",1,0,0),new TransitCatalog.StopCall("b","B",2,60,60)),
      List.of(new TransitCatalog.Coordinate(-6.0002,-50.0002),new TransitCatalog.Coordinate(-6.1,-49.5),new TransitCatalog.Coordinate(-5.9998,-48.9998)));
    var segment=TransitGeometry.segment(trip,0,1,Map.of("a",a,"b",b));
    assertThat(segment).containsExactly(new TransitCatalog.Coordinate(-6,-50),new TransitCatalog.Coordinate(-6.1,-49.5),new TransitCatalog.Coordinate(-6,-49));
  }
}
