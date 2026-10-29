# Regras de negócio

- **RN-01:** visitante consulta catálogo, trajeto e simulação sem conta (UC-01/02).
- **RN-02:** embarque só ocorre após caminhada e nunca após a partida (teste `doesNotBoardMissedDeparture`).
- **RN-03:** baldeação exige ordem, sentido e conexão mínima; máximo de duas (UC-01).
- **RN-04:** caminhada inicial/final respeita limite de 100–3.000 m e não é navegação validada.
- **RN-05:** serviço usa `America/Belem` e separa data de serviço de instante.
- **RN-06:** favorito representa intenção e sempre deve ser recalculado (UC-04).
- **RN-07:** usuário só lê/exclui seus recursos (consulta por `userEmail`).
- **RN-08:** conteúdo público exibe aviso acadêmico persistente.
- **RN-09:** posição é simulada ao longo da geometria e leva timestamp/stale.
- **RN-10:** evento de negócio persiste na outbox antes de publicação; consumidores deduplicam por `eventId`.
- **RN-11:** busca contabiliza “mais consultadas”, nunca prova embarque.
- **RN-12:** edição concorrente não sobrescreve silenciosamente.
