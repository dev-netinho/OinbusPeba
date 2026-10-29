package br.edu.oinbupebas;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OinbuPebasApplication {
  public static final ZoneId ZONE = ZoneId.of("America/Belem");
  public static void main(String[] args) { SpringApplication.run(OinbuPebasApplication.class, args); }
  @Bean Clock applicationClock() { return Clock.system(ZONE); }
}
