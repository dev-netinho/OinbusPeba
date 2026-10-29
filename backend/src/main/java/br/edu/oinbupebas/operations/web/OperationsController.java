package br.edu.oinbupebas.operations.web;

import br.edu.oinbupebas.config.MessagingConfig;
import br.edu.oinbupebas.operations.application.CatalogImportService;
import br.edu.oinbupebas.operations.domain.OutboxEventRepository;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api/admin/operations")
public class OperationsController {
  private final OutboxEventRepository outbox; private final CatalogImportService imports; private final RabbitAdmin rabbit;
  OperationsController(OutboxEventRepository outbox,CatalogImportService imports,RabbitAdmin rabbit){this.outbox=outbox;this.imports=imports;this.rabbit=rabbit;}
  @GetMapping("/outbox") Object outbox(){return outbox.unpublished();}
  @GetMapping("/imports") Object imports(){return imports.list();}
  @GetMapping("/imports/{id}") Object task(@PathVariable UUID id){return imports.find(id);}
  @GetMapping("/messaging") Object messaging(){return Map.of("importQueue",queueCount(MessagingConfig.IMPORT_QUEUE),"deadLetterQueue",queueCount(MessagingConfig.DLQ));}
  @PostMapping(value="/imports",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @ResponseStatus(HttpStatus.ACCEPTED)
  Object upload(@RequestPart MultipartFile file) throws IOException{return imports.create(file);}
  @PostMapping("/imports/{id}/publish") Object publish(@PathVariable UUID id,@RequestParam long version,Principal principal){return imports.publish(id,version,principal.getName());}
  @PostMapping("/imports/{id}/reprocess") @ResponseStatus(HttpStatus.ACCEPTED) Object reprocess(@PathVariable UUID id,Principal principal){return imports.reprocess(id,principal.getName());}
  private int queueCount(String name){var properties=rabbit.getQueueProperties(name);if(properties==null)return -1;return (Integer)properties.getOrDefault(RabbitAdmin.QUEUE_MESSAGE_COUNT,0);}
}
