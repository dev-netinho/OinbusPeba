package br.edu.oinbupebas.transit.web;

import br.edu.oinbupebas.transit.api.TransitCatalog;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/public/transit")
public class TransitController {
  private final TransitCatalog catalog;
  public TransitController(TransitCatalog catalog) { this.catalog = catalog; }
  @GetMapping("/catalog") Object catalog() { return Map.of("stops", catalog.stops(), "lines", catalog.lines(), "notice", "Horários, geometrias e veículos são demonstrativos."); }
}
