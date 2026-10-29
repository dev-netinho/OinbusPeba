package br.edu.oinbupebas.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PasswordResetTokenTest {
  private static final Instant NOW=Instant.parse("2026-10-28T12:00:00Z");
  @Test void acceptsExactlyOneUseBeforeExpiry(){
    var token=new PasswordResetToken(UUID.randomUUID(),"hash",NOW.plusSeconds(60));
    assertThat(token.canUseAt(NOW)).isTrue(); token.consume(NOW);
    assertThat(token.canUseAt(NOW.plusSeconds(1))).isFalse();
    assertThatThrownBy(()->token.consume(NOW.plusSeconds(1))).hasMessageContaining("inválido ou expirado");
  }
  @Test void rejectsExpiredToken(){
    var token=new PasswordResetToken(UUID.randomUUID(),"hash",NOW);
    assertThat(token.canUseAt(NOW)).isFalse();
    assertThatThrownBy(()->token.consume(NOW)).isInstanceOf(IllegalArgumentException.class);
  }
}
