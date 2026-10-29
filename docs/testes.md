# Testes e evidências

Executados em 2026-10-28 no Fedora Linux, com toolchains locais ignorados pelo Git.

| Comando | Resultado real |
|---|---|
| `JAVA_HOME=.tools/jdk21 .tools/maven/bin/mvn -q -f backend/pom.xml test` | sucesso; 22 testes executados, 0 falhas; 3 Testcontainers omitidos automaticamente sem Docker |
| `npm install --prefix frontend --ignore-scripts` | 310 pacotes; auditoria npm reportou 0 vulnerabilidades |
| `npm run lint --prefix frontend` | sucesso; 0 erros e 0 avisos |
| `npm run build --prefix frontend` | sucesso após correções de tipos; bundle JS ~239 kB bruto |
| `npm test --prefix frontend` | sucesso; teste de erro confirma que a API indisponível não vira sucesso local |
| `npm run test:e2e --prefix frontend` | não executado: navegador Chromium do Playwright não estava instalado; tentativa de download local não concluiu no tempo disponível |

O primeiro build do frontend falhou por ausência de tipos Node e configuração de lib; foi corrigido, não contado como aprovado.

Após a última rodada, os mesmos checks foram repetidos diretamente pelos binários locais (`node_modules/eslint`, `vitest`, `vue-tsc` e `vite`) porque o wrapper `npm` deixou de estar disponível no `PATH`: lint, Vitest (1 teste) e build Vite passaram.

## Cobertura existente

- Planejamento temporal genérico com direto, uma e duas baldeações, conexão perdida, sem serviço, caminhada, sentido e pós-meia-noite.
- Token de recuperação: uso único, expiração, reutilização e hash sem valor bruto.
- Validação CSV de cabeçalho, linha, coordenadas, cor e rejeição sem staging inválido.
- Simulação determinística por interpolação entre paradas programadas.
- Outbox com confirmação e infraestrutura Testcontainers declarada; integração foi omitida sem Docker.
- Dependência arquitetural e falha frontend sem fallback enganoso.

## Não executado neste ambiente

Docker/Podman não estão disponíveis neste ambiente do shell e não há `/var/run/docker.sock`; portanto não foram executados Testcontainers, migração PostGIS em banco real, RabbitMQ, Mailpit ou smoke test contra serviços. O Playwright também não pôde abrir a revisão Chromium compatível do projeto. Não há números de cobertura ou performance inventados. Os testes unitários cobrem calendário diário no catálogo, offsets pós-meia-noite, propriedade do domínio, idempotência no consumidor e concorrência HTTP apenas no código de serviço; a validação end-to-end aguarda o runtime externo.

## Critérios E2E

| Fluxo | Implementado | Executado | Estado/evidência |
|---|---:|---:|---|
| 1. Visitante Tropical → Partage | sim | não | bloqueado: Compose/Docker ausente |
| 2. Alternativa, etapas e mapa | sim | não | bloqueado: Compose/Docker ausente |
| 3. Veículo simulado e atualização | sim | não | bloqueado: Compose/Docker ausente |
| 4. Cadastro e favorito | sim | não | bloqueado: Compose/Docker ausente |
| 5. Reabrir favorito e recalcular | parcial | não | falta tela dedicada de reabertura; API recalcula por nova consulta |
| 6. Admin edita horário | parcial | não | edição de linha implementada; edição de horário dedicada ainda não |
| 7. Conflito concorrente | sim | não | serviço retorna 409; integração bloqueada |
| 8. Importação inválida | sim | não | parser/erros por linha implementados; broker/banco bloqueados |
| 9. Importação válida/publicação | sim | não | staging/publicação implementados; broker/banco bloqueados |
| 10. Evento duplicado | sim | não | chave de idempotência implementada; RabbitMQ bloqueado |
| 11. DLQ/reprocessamento | sim | não | retry/DLQ/painel implementados; RabbitMQ bloqueado |
| 12. Viewport móvel | sim | não | build responsivo aprovado; Chromium compatível ausente |

Os testes unitários usam fakes apenas no núcleo determinístico de planejamento e simulação. `InfrastructureIntegrationTest` usa PostgreSQL/PostGIS e RabbitMQ reais via Testcontainers, mas seus três cenários foram explicitamente skipped porque não existe runtime Docker. Não houve aprovação baseada apenas em screenshot ou HTTP 200.
