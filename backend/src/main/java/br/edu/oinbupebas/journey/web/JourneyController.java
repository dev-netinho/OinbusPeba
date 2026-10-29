package br.edu.oinbupebas.journey.web;

import br.edu.oinbupebas.journey.api.JourneyPlanner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/public/journeys")
public class JourneyController {
  private final JourneyPlanner planner;
  public JourneyController(JourneyPlanner planner) { this.planner = planner; }
  record Request(@NotBlank String originId,@NotBlank String destinationId,@NotNull LocalDate serviceDate,@NotNull LocalTime departure,@Min(100) @Max(3000) int maxWalkMeters,@Min(0) @Max(2) int maxTransfers,String preference) {}
  @PostMapping("/plan") Object plan(@Valid @RequestBody Request r) {
    var journeys=planner.plan(new JourneyPlanner.Query(r.originId,r.destinationId,r.serviceDate,r.departure,r.maxWalkMeters,r.maxTransfers,r.preference));
    return Map.of("journeys",journeys,"serviceDate",r.serviceDate,"timezone","America/Belem","dataNature","SYNTHETIC","message",journeys.isEmpty()?"Nenhum serviço demonstrativo foi encontrado. Ajuste o horário ou a caminhada.":"Alternativas sobre horários demonstrativos.");
  }
}
