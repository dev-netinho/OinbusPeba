package br.edu.oinbupebas.identity.application;

import br.edu.oinbupebas.identity.domain.UserAccount;
import br.edu.oinbupebas.identity.domain.UserAccountRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service public class IdentityService {
  private final UserAccountRepository users; private final PasswordEncoder encoder;
  IdentityService(UserAccountRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
  @Transactional public UserAccount register(String email,String name,String password){
    var normalized=email.trim().toLowerCase(Locale.ROOT); if(users.findByEmailIgnoreCase(normalized).isPresent()) throw new IllegalArgumentException("Não foi possível concluir o cadastro com os dados informados.");
    return users.save(new UserAccount(normalized,name.trim(),encoder.encode(password),"USER"));
  }
  public UserAccount byEmail(String email){return users.findByEmailIgnoreCase(email).orElseThrow();}
  @Transactional public void bootstrapAdmin(String email,String password){
    var normalized=email.trim().toLowerCase(Locale.ROOT);
    var existing=users.findByEmailIgnoreCase(normalized);
    if(existing.isPresent()){
      if(!existing.get().isAdmin())throw new IllegalStateException("ADMIN_EMAIL já pertence a uma conta sem papel administrativo.");
      return;
    }
    users.save(new UserAccount(normalized,"Administrador",encoder.encode(password),"ADMIN"));
  }
}
