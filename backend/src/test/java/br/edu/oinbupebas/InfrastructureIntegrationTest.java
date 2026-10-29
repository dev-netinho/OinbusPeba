package br.edu.oinbupebas;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.oinbupebas.config.MessagingConfig;
import br.edu.oinbupebas.journey.api.JourneyPlanner;
import br.edu.oinbupebas.operations.application.CatalogImportService;
import br.edu.oinbupebas.transit.api.TransitCatalog;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker=true)
@SpringBootTest(properties={"app.outbox.delay=100","app.mail.enabled=false"})
class InfrastructureIntegrationTest {
  @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>(DockerImageName.parse("postgis/postgis:17-3.5-alpine").asCompatibleSubstituteFor("postgres"));
  @Container static final RabbitMQContainer RABBIT=new RabbitMQContainer("rabbitmq:4.1-management-alpine");
  @DynamicPropertySource static void properties(DynamicPropertyRegistry registry){
    registry.add("spring.datasource.url",POSTGRES::getJdbcUrl);registry.add("spring.datasource.username",POSTGRES::getUsername);registry.add("spring.datasource.password",POSTGRES::getPassword);
    registry.add("spring.rabbitmq.host",RABBIT::getHost);registry.add("spring.rabbitmq.port",RABBIT::getAmqpPort);registry.add("spring.rabbitmq.username",RABBIT::getAdminUsername);registry.add("spring.rabbitmq.password",RABBIT::getAdminPassword);
  }
  @Autowired TransitCatalog catalog; @Autowired JourneyPlanner planner; @Autowired CatalogImportService imports;
  @Autowired JdbcClient jdbc; @Autowired RabbitTemplate rabbit; @Autowired RabbitAdmin rabbitAdmin;

  @Test void migrationsSeedPostgisAndPlannerUsePersistedTimetable(){
    assertThat(catalog.stops()).hasSize(44);assertThat(catalog.lines()).hasSize(9);
    assertThat(jdbc.sql("select count(*) from pg_extension where extname='postgis'").query(Integer.class).single()).isOne();
    var journeys=planner.plan(new JourneyPlanner.Query("tropical","partage",LocalDate.of(2026,10,28),LocalTime.of(7,20),800,2,"FASTEST"));
    assertThat(journeys).isNotEmpty();assertThat(journeys).anySatisfy(journey->assertThat(journey.transfers()).isZero());
  }

  @Test void validImportIsConsumedAndDuplicateEventHasOneIdempotencyRecord() throws Exception {
    var csv="type,id,name,latitude,longitude,short_name,color,provenance\nSTOP,teste-integracao,Parada Integração,-6.08,-49.88,,,sintetico\n";
    var task=imports.create(new MockMultipartFile("file","catalogo.csv","text/csv",csv.getBytes()));
    await(()->"READY".equals(imports.find(task.id()).status()));
    var eventId=UUID.randomUUID();var message=MessageBuilder.withBody(("{\"taskId\":\""+task.id()+"\"}").getBytes()).setMessageId(eventId.toString()).setContentType("application/json").build();
    rabbit.send(MessagingConfig.EVENTS,"catalog.import.requested",message);rabbit.send(MessagingConfig.EVENTS,"catalog.import.requested",message);
    await(()->jdbc.sql("select count(*) from operations_processed_event where consumer='catalog-import' and event_id=:id").param("id",eventId).query(Integer.class).single()==1);
    assertThat(jdbc.sql("select count(*) from operations_processed_event where consumer='catalog-import' and event_id=:id").param("id",eventId).query(Integer.class).single()).isOne();
  }

  @Test void persistentFailureIsRetriedAndBecomesObservableInDlq(){
    var eventId=UUID.randomUUID();var missingTask=UUID.randomUUID();var message=MessageBuilder.withBody(("{\"taskId\":\""+missingTask+"\"}").getBytes()).setMessageId(eventId.toString()).setContentType("application/json").build();
    rabbit.send(MessagingConfig.EVENTS,"catalog.import.requested",message);
    await(()->queueCount(MessagingConfig.DLQ)>0);assertThat(queueCount(MessagingConfig.DLQ)).isPositive();
  }

  private int queueCount(String queue){var props=rabbitAdmin.getQueueProperties(queue);return props==null?0:(Integer)props.getOrDefault(RabbitAdmin.QUEUE_MESSAGE_COUNT,0);}
  private static void await(BooleanSupplier condition){long deadline=System.nanoTime()+15_000_000_000L;while(System.nanoTime()<deadline){if(condition.getAsBoolean())return;try{Thread.sleep(100);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new AssertionError(e);}}throw new AssertionError("Condição assíncrona não ocorreu em 15 segundos");}
}
