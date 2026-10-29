package br.edu.oinbupebas.transit.web;

import br.edu.oinbupebas.transit.application.TransitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/admin/transit")
public class TransitAdminController {
  private final TransitAdminService service; TransitAdminController(TransitAdminService service){this.service=service;}
  record UpdateLine(@NotBlank String name,@Pattern(regexp="#[0-9A-Fa-f]{6}") String color,@PositiveOrZero long version){}
  @GetMapping("/lines") Object lines(){return service.lines();}
  @PutMapping("/lines/{id}") Object update(@PathVariable String id,@Valid @RequestBody UpdateLine request,Principal principal){return service.updateLine(id,request.name(),request.color(),request.version(),principal.getName());}
}
