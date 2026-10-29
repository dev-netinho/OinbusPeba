# Falhas e consistência

| Cenário | Detecção | Resposta | Recuperação | Evidência/teste |
|---|---|---|---|---|
| Backend indisponível | timeout/erro `fetch` | mensagem finita; nenhuma jornada substituta | botão de nova tentativa | teste Vitest de erro sem fallback |
| Timeout | `AbortSignal.timeout(8000)` | descarta resposta e explica | nova tentativa | código frontend |
| Banco indisponível | readiness falha | sem inventar dados persistidos | restart/conserto e Flyway | healthcheck Compose; container não executado neste host |
| Mapa externo | `tileerror` | lista de etapas permanece | nova carga da camada | estado implementado |
| SSE interrompido | `EventSource.onerror` | marca stale e inicia polling | reconecta na próxima carga | código frontend |
| RabbitMQ indisponível | exceção no publicador | consulta continua; outbox pendente | tentativas limitadas | teste de integração pendente |
| Mensagem duplicada | chave `(consumer,event_id)` | efeito idempotente na transação de staging | confirmar mesma transação | teste de integração Testcontainers (skipped sem Docker) |
| Evento persistente | DLQ | tarefa não anunciada como concluída | inspeção/reprocessamento admin | listener, retry, painel e reprocessamento implementados; broker real bloqueado |
| Edição concorrente | versão da linha/tarefa | HTTP 409 | recarregar versão | serviço e handler implementados; integração real bloqueada |
| Venda da última passagem | não aplicável | produto não vende/reserva | nenhuma | escopo/README |

Entrega é pelo menos uma vez; “exactly once” ponta a ponta não é prometido. Falhar um consumo reverte apenas a transação atual, preserva histórico e permite recuperação. A indisponibilidade do OinbuPebas não altera a operação real de transporte.
