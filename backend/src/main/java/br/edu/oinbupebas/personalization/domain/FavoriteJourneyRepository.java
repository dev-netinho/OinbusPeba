package br.edu.oinbupebas.personalization.domain;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface FavoriteJourneyRepository extends JpaRepository<FavoriteJourney,UUID>{List<FavoriteJourney> findAllByUserEmailOrderByCreatedAtDesc(String email);Optional<FavoriteJourney> findByIdAndUserEmail(UUID id,String email);}
