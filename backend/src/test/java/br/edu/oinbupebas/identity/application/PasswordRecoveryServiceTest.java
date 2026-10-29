package br.edu.oinbupebas.identity.application;
import static org.assertj.core.api.Assertions.assertThat;import org.junit.jupiter.api.Test;
class PasswordRecoveryServiceTest {@Test void hashesTokensWithoutPersistingRawValue(){assertThat(PasswordRecoveryService.hash("token-a")).hasSize(64).isNotEqualTo("token-a").isNotEqualTo(PasswordRecoveryService.hash("token-b"));}}
