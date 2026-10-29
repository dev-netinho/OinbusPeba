package br.edu.oinbupebas.identity.application;

import br.edu.oinbupebas.identity.domain.PasswordResetToken;
import br.edu.oinbupebas.identity.domain.PasswordResetTokenRepository;
import br.edu.oinbupebas.identity.domain.UserAccountRepository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class PasswordRecoveryService {
  private final UserAccountRepository users; private final PasswordResetTokenRepository tokens;
  private final PasswordEncoder encoder; private final RecoveryMailDelivery mail; private final Clock clock;
  private final TransactionTemplate transactions; private final SecureRandom random=new SecureRandom();
  PasswordRecoveryService(UserAccountRepository users,PasswordResetTokenRepository tokens,PasswordEncoder encoder,
      RecoveryMailDelivery mail,Clock clock,TransactionTemplate transactions){this.users=users;this.tokens=tokens;this.encoder=encoder;this.mail=mail;this.clock=clock;this.transactions=transactions;}

  public void request(String email){
    if(!mail.available())throw new RecoveryUnavailableException();
    var normalized=email.trim().toLowerCase(Locale.ROOT);
    var issued=transactions.execute(status -> users.findByEmailIgnoreCase(normalized).map(user -> {
      var raw=new byte[32];random.nextBytes(raw);var token=Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
      tokens.save(new PasswordResetToken(user.getId(),hash(token),clock.instant().plus(Duration.ofMinutes(30))));return token;
    }).orElse(null));
    if(issued!=null)mail.send(normalized,issued);
  }

  public void reset(String rawToken,String newPassword){
    transactions.executeWithoutResult(status -> {
      var token=tokens.findByTokenHash(hash(rawToken)).orElseThrow(() -> new IllegalArgumentException("Token de recuperação inválido ou expirado."));
      token.consume(clock.instant());
      var user=users.findById(token.getUserId()).orElseThrow(() -> new IllegalArgumentException("Token de recuperação inválido ou expirado."));
      user.changePassword(encoder.encode(newPassword));
    });
  }

  static String hash(String value){try{return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}
