package br.edu.oinbupebas.identity.domain;
import java.util.Optional; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserAccountRepository extends JpaRepository<UserAccount,UUID> { Optional<UserAccount> findByEmailIgnoreCase(String email); boolean existsByRole(String role); }
