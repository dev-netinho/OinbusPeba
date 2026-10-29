package br.edu.oinbupebas.operations.application;

import br.edu.oinbupebas.config.MessagingConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class CatalogImportConsumer {
  private final CatalogImportService imports; private final ObjectMapper json;
  CatalogImportConsumer(CatalogImportService imports,ObjectMapper json){this.imports=imports;this.json=json;}
  @RabbitListener(queues=MessagingConfig.IMPORT_QUEUE)
  void consume(Message message) throws Exception {
    var messageId=message.getMessageProperties().getMessageId();
    if(messageId==null)throw new IllegalArgumentException("Evento sem messageId estável.");
    @SuppressWarnings("unchecked") var payload=json.readValue(message.getBody(),Map.class);
    imports.process(UUID.fromString(messageId),UUID.fromString(String.valueOf(payload.get("taskId"))));
  }
}
