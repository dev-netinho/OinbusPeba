package br.edu.oinbupebas.identity.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="identity_user_account")
public class UserAccount {
  @Id private UUID id;
  @Column(nullable=false,unique=true) private String email;
  @Column(nullable=false) private String displayName;
  @Column(nullable=false) private String passwordHash;
  @Column(nullable=false) private String role;
  @Column(nullable=false) private Instant createdAt;
  @Version private long version;
  protected UserAccount() {}
  public UserAccount(String email,String displayName,String passwordHash,String role) { this.id=UUID.randomUUID(); this.email=email.toLowerCase(); this.displayName=displayName; this.passwordHash=passwordHash; this.role=role; this.createdAt=Instant.now(); }
  public UUID getId(){return id;} public String getEmail(){return email;} public String getDisplayName(){return displayName;} public String getPasswordHash(){return passwordHash;} public String getRole(){return role;}
  public void rename(String value){displayName=value;}
  public void changePassword(String value){passwordHash=value;}
  public boolean isAdmin(){return "ADMIN".equals(role);}
}
