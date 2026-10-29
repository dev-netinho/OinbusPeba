package br.edu.oinbupebas.identity.application;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
class SmtpRecoveryMailDelivery implements RecoveryMailDelivery {
  private final JavaMailSender sender; private final boolean enabled; private final String baseUrl;
  SmtpRecoveryMailDelivery(JavaMailSender sender,@Value("${app.mail.enabled:false}") boolean enabled,
      @Value("${app.mail.recovery-base-url}") String baseUrl){this.sender=sender;this.enabled=enabled;this.baseUrl=baseUrl;}
  public boolean available(){return enabled;}
  public void send(String recipient,String rawToken){
    var message=new SimpleMailMessage(); message.setTo(recipient);message.setFrom("nao-responda@oinbupebas.local");
    message.setSubject("Recuperação de senha — OinbuPebas");
    message.setText("Use este link uma única vez em até 30 minutos:\n"+baseUrl+"?token="+URLEncoder.encode(rawToken,StandardCharsets.UTF_8)+"\n\nSe você não solicitou, ignore esta mensagem.");
    sender.send(message);
  }
}
