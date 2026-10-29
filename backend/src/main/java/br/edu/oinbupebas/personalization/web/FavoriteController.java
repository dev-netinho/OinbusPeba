package br.edu.oinbupebas.personalization.web;
import br.edu.oinbupebas.personalization.domain.*; import jakarta.validation.constraints.NotBlank; import java.security.Principal; import java.util.UUID; import org.springframework.http.HttpStatus; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/favorites") public class FavoriteController {
 private final FavoriteJourneyRepository favorites; FavoriteController(FavoriteJourneyRepository f){favorites=f;}
 record Request(@NotBlank String originId,@NotBlank String destinationId,@NotBlank String label){}
 @GetMapping Object list(Principal p){return favorites.findAllByUserEmailOrderByCreatedAtDesc(p.getName());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) FavoriteJourney add(@RequestBody Request r,Principal p){return favorites.save(new FavoriteJourney(p.getName(),r.originId,r.destinationId,r.label));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional void delete(@PathVariable UUID id,Principal p){favorites.findByIdAndUserEmail(id,p.getName()).ifPresent(favorites::delete);}
}
