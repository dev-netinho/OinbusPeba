package br.edu.oinbupebas.identity.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class AdminBootstrap implements ApplicationRunner {
  private final IdentityService identity; private final String email; private final String password;
  AdminBootstrap(IdentityService identity,@Value("${app.bootstrap.admin-email:}") String email,
      @Value("${app.bootstrap.admin-password:}") String password){this.identity=identity;this.email=email;this.password=password;}
  @Override public void run(ApplicationArguments args){
    boolean hasEmail=!email.isBlank(),hasPassword=!password.isBlank();
    if(hasEmail!=hasPassword)throw new IllegalStateException("ADMIN_EMAIL e ADMIN_PASSWORD devem ser definidos juntos.");
    if(!hasEmail)return;
    if(password.length()<14)throw new IllegalStateException("ADMIN_PASSWORD deve ter ao menos 14 caracteres.");
    identity.bootstrapAdmin(email,password);
  }
}
