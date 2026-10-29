package br.edu.oinbupebas.operations.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="operations_outbox_event") public class OutboxEvent {
 @Id private UUID id; @Column(nullable=false) private String type; @Column(nullable=false,columnDefinition="text") private String payload; @Column(nullable=false) private Instant occurredAt; private Instant publishedAt; private int attempts;
 protected OutboxEvent(){} public OutboxEvent(String t,String p){id=UUID.randomUUID();type=t;payload=p;occurredAt=Instant.now();} public UUID getId(){return id;} public String getType(){return type;} public String getPayload(){return payload;} public void published(){publishedAt=Instant.now();} public void failed(){attempts++;}
 public Instant getOccurredAt(){return occurredAt;} public Instant getPublishedAt(){return publishedAt;} public int getAttempts(){return attempts;}
}
