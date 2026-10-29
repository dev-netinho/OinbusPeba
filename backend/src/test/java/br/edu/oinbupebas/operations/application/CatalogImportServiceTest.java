package br.edu.oinbupebas.operations.application;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CatalogImportServiceTest {
  private static final String HEADER="type,id,name,latitude,longitude,short_name,color,provenance\n";
  @Test void validatesStopAndLineRows(){
    var csv=HEADER+"STOP,teste,Parada Teste,-6.1,-49.9,,,sintetico\nLINE,l99,Linha Teste,,,L99,#123ABC,sintetico\n";
    var result=CatalogImportService.validate(csv.getBytes(StandardCharsets.UTF_8));
    assertThat(result.errors()).isEmpty();assertThat(result.rows()).hasSize(2);
  }
  @Test void reportsLineNumberAndDoesNotStageInvalidCoordinates(){
    var csv=HEADER+"STOP,teste,Parada Teste,999,-49.9,,,sintetico\n";
    var result=CatalogImportService.validate(csv.getBytes(StandardCharsets.UTF_8));
    assertThat(result.rows()).isEmpty();assertThat(result.errors()).singleElement().satisfies(error->{assertThat(error.row()).isEqualTo(2);assertThat(error.message()).contains("coordenadas");});
  }
  @Test void rejectsIncompleteHeader(){
    var result=CatalogImportService.validate("type,id\nSTOP,x\n".getBytes(StandardCharsets.UTF_8));
    assertThat(result.errors()).singleElement().satisfies(error->assertThat(error.message()).contains("Cabeçalho"));
  }
}
