package br.edu.oinbupebas.identity.web;

import br.edu.oinbupebas.identity.application.IdentityService;
import br.edu.oinbupebas.identity.application.PasswordRecoveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api")
public class AuthController {
  private final IdentityService identity; private final PasswordRecoveryService recovery;
  AuthController(IdentityService identity,PasswordRecoveryService recovery){this.identity=identity;this.recovery=recovery;}
  record Registration(@Email @NotBlank String email,@Size(min=2,max=80) String name,@Size(min=10,max=128) String password){}
  record RecoveryRequest(@Email @NotBlank String email){}
  record ResetRequest(@NotBlank String token,@Size(min=10,max=128) String password){}
  @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED) Object register(@Valid @RequestBody Registration r){var u=identity.register(r.email,r.name,r.password);return Map.of("id",u.getId(),"name",u.getDisplayName());}
  @PostMapping("/auth/recovery") @ResponseStatus(HttpStatus.ACCEPTED) Object recover(@Valid @RequestBody RecoveryRequest request){recovery.request(request.email());return Map.of("message","Se a conta existir, você receberá instruções.");}
  @PostMapping("/auth/reset") @ResponseStatus(HttpStatus.NO_CONTENT) void reset(@Valid @RequestBody ResetRequest request){recovery.reset(request.token(),request.password());}
  @GetMapping("/auth/csrf") Object csrf(CsrfToken token){return Map.of("headerName",token.getHeaderName(),"parameterName",token.getParameterName(),"token",token.getToken());}
  @GetMapping("/me") Object me(Principal p){var u=identity.byEmail(p.getName());return Map.of("id",u.getId(),"email",u.getEmail(),"name",u.getDisplayName(),"role",u.getRole());}
}
