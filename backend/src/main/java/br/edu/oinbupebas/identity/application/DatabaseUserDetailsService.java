package br.edu.oinbupebas.identity.application;
import br.edu.oinbupebas.identity.domain.UserAccountRepository; import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service;
@Service class DatabaseUserDetailsService implements UserDetailsService {
  private final UserAccountRepository users; DatabaseUserDetailsService(UserAccountRepository users){this.users=users;}
  public UserDetails loadUserByUsername(String email){var u=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("Conta não encontrada")); return User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole()).build();}
}
