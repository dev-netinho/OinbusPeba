package br.edu.oinbupebas.personalization.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="personalization_favorite_journey",uniqueConstraints=@UniqueConstraint(columnNames={"userEmail","originId","destinationId"})) public class FavoriteJourney {
 @Id private UUID id; @Column(nullable=false) private String userEmail; @Column(nullable=false) private String originId; @Column(nullable=false) private String destinationId; @Column(nullable=false) private String label; @Column(nullable=false) private Instant createdAt;
 protected FavoriteJourney(){} public FavoriteJourney(String u,String o,String d,String l){id=UUID.randomUUID();userEmail=u;originId=o;destinationId=d;label=l;createdAt=Instant.now();}
 public UUID getId(){return id;} public String getOriginId(){return originId;} public String getDestinationId(){return destinationId;} public String getLabel(){return label;} public String getUserEmail(){return userEmail;}
}
