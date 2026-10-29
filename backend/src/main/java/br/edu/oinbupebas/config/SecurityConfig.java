package br.edu.oinbupebas.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
  @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
    var csrf = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrf.setCookieCustomizer(c -> c.sameSite("Lax").path("/"));
    return http
      .csrf(c -> c.csrfTokenRepository(csrf).ignoringRequestMatchers("/api/public/**", "/api/simulation/**"))
      .authorizeHttpRequests(a -> a
        .requestMatchers("/api/public/**", "/api/simulation/**").permitAll()
        .requestMatchers("/api/auth/**", "/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
      .formLogin(f -> f.loginProcessingUrl("/api/auth/login").successHandler((q,s,a) -> s.setStatus(204)).failureHandler((q,s,e) -> s.sendError(401,"Credenciais inválidas")))
      .logout(l -> l.logoutUrl("/api/auth/logout").logoutSuccessHandler((q,s,a) -> s.setStatus(204)))
      .exceptionHandling(e -> e.authenticationEntryPoint((q,s,x) -> s.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Sessão necessária")))
      .headers(h -> h.contentSecurityPolicy(c -> c.policyDirectives("default-src 'self'; img-src 'self' data: https://*.tile.openstreetmap.org; connect-src 'self'; style-src 'self' 'unsafe-inline'; script-src 'self'")))
      .build();
  }
}
