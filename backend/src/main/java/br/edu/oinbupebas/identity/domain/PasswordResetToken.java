package br.edu.oinbupebas.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="identity_password_reset")
public class PasswordResetToken {
  @Id private UUID id;
  @Column(nullable=false) private UUID userId;
  @Column(nullable=false,unique=true) private String tokenHash;
  @Column(nullable=false) private Instant expiresAt;
  private Instant usedAt;
  protected PasswordResetToken() {}
  public PasswordResetToken(UUID userId,String tokenHash,Instant expiresAt){this.id=UUID.randomUUID();this.userId=userId;this.tokenHash=tokenHash;this.expiresAt=expiresAt;}
  public UUID getUserId(){return userId;}
  public boolean canUseAt(Instant now){return usedAt==null&&now.isBefore(expiresAt);}
  public void consume(Instant now){if(!canUseAt(now))throw new IllegalArgumentException("Token de recuperação inválido ou expirado.");usedAt=now;}
}
