package br.edu.oinbupebas.identity.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthRateLimitFilter extends OncePerRequestFilter {
  private static final int LIMIT = 10;
  private static final long WINDOW_SECONDS = 60;
  private final Clock clock;
  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
  AuthRateLimitFilter(Clock clock) { this.clock = clock; }

  @Override protected boolean shouldNotFilter(HttpServletRequest request) { return !request.getRequestURI().startsWith("/api/auth/") || request.getMethod().equals("GET"); }
  @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
    long now=clock.instant().getEpochSecond(); String key=request.getRemoteAddr();
    var window=windows.compute(key,(ignored,current)->current==null||now-current.startedAt>=WINDOW_SECONDS?new Window(now,1):new Window(current.startedAt,current.count+1));
    if(window.count>LIMIT){response.setStatus(429);response.setContentType("application/problem+json");response.getWriter().write("{\"title\":\"Muitas tentativas\",\"detail\":\"Aguarde um minuto antes de tentar novamente.\"}");return;}
    chain.doFilter(request,response);
  }
  private record Window(long startedAt,int count) {}
}
