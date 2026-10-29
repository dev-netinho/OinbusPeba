package br.edu.oinbupebas.operations.domain;
import java.time.Instant;import java.util.*;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import org.springframework.transaction.annotation.Transactional;
public interface OutboxEventRepository extends JpaRepository<OutboxEvent,UUID>{@Query("select e from OutboxEvent e where e.publishedAt is null and e.attempts < 5 order by e.occurredAt")List<OutboxEvent> unpublished();
 @Modifying @Transactional @Query("update OutboxEvent e set e.publishedAt=:at where e.id=:id and e.publishedAt is null")int markPublished(@Param("id")UUID id,@Param("at")Instant at);
 @Modifying @Transactional @Query("update OutboxEvent e set e.attempts=e.attempts+1 where e.id=:id and e.publishedAt is null")int markFailed(@Param("id")UUID id);
}
