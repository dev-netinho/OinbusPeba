package br.edu.oinbupebas.operations.application;

import br.edu.oinbupebas.operations.domain.OutboxEventRepository;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {
  private final OutboxEventRepository events; private final RabbitTemplate rabbit;
  OutboxPublisher(OutboxEventRepository events,RabbitTemplate rabbit){this.events=events;this.rabbit=rabbit;}
  @Scheduled(fixedDelayString="${app.outbox.delay:5000}") public void publish(){
    for(var event:events.unpublished()){
      try{
        var correlation=new CorrelationData(event.getId().toString());
        rabbit.convertAndSend("oinbupebas.events",event.getType(),event.getPayload(),message->{
          message.getMessageProperties().setMessageId(event.getId().toString());
          message.getMessageProperties().setContentType("application/json");return message;
        },correlation);
        var confirm=correlation.getFuture().get(5,TimeUnit.SECONDS);
        if(!confirm.isAck())throw new IllegalStateException("Broker não confirmou o evento: "+confirm.getReason());
        events.markPublished(event.getId(),Instant.now());
      }catch(Exception ex){events.markFailed(event.getId());}
    }
  }
}
