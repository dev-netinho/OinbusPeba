# Matriz de rastreabilidade

| Requisito | Módulo | Caso | Teste/evidência | Estado |
|---|---|---|---|---|
| Tropical → Partage temporal | journey/transit | UC-01 | `TimeDependentJourneyPlannerTest` | implementado demonstrativo |
| não embarcar atrasado | journey | UC-01 | `doesNotBoardMissedDeparture` | testado |
| simulação compartilhada SSE | simulation | UC-02 | `SimulationController`, UI stale/polling | implementado; integração não executada |
| sessão/cadastro | identity | UC-03 | SecurityConfig/AuthController/bootstrap | implementado não validado |
| favoritos com propriedade | personalization | UC-04 | repositório por e-mail e UI | implementado não validado |
| importação assíncrona | operations | UC-05 | tarefa, CSV, staging, outbox, consumidor e painel | implementado não validado |
| outbox/idempotência/DLQ | operations | UC-06 | outbox, listener, retry, DLQ, painel | implementado não validado sem RabbitMQ |
| 9 linhas/44 paradas/até 12 veículos | transit/simulation | UC-01/02 | V2 seed, planner e teste determinístico | implementado; integração PostGIS bloqueada |
| arquitetura modular | todos | todos | `ArchitectureTest` | teste executado |
| PWA sem cache privado | frontend | UC-01 | `sw.js` exclui `/api` | implementado |
| falhas honestas | frontend/operations | UC-01/06 | Vitest, retry/DLQ e matriz de falhas | implementado não validado |
| produção TLS/serviços internos | infra | — | Compose/Caddy | artefato pronto; não implantado |
