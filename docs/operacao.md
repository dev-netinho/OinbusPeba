# Operação

## Rotina

- `docker compose ps` e `/actuator/health/readiness` para saúde.
- Observe falhas de publicação, idade da outbox e profundidade da DLQ; não trate tarefa pendente como concluída.
- Atualize aviso operacional sem afirmar impacto real não confirmado.
- Revogue sessões e credenciais administrativas comprometidas.

## Incidentes

1. Banco: bloqueie escrita, preserve volume/logs, restaure backup testado se necessário.
2. RabbitMQ: mantenha backend de consulta; outbox retém eventos; recupere broker antes de reprocessar.
3. SSE: clientes passam a polling; investigue proxy/timeouts.
4. Mapa: mantenha lista textual, comunique indisponibilidade externa.
5. App: rollback da imagem não interfere no transporte real.

## CSV v1

Formato UTF-8 com cabeçalho obrigatório `type,id,name,latitude,longitude,short_name,color,provenance`. A primeira versão aceita registros `STOP` e `LINE`; os campos que não se aplicam ficam vazios. Upload máximo 2 MB. O arquivo é persistido em tarefa, validado por linha e colocado em staging; somente `READY` pode ser publicado atomicamente. Exportações devem prefixar células que começam com `=`, `+`, `-` ou `@` para evitar CSV injection. Padrões, sentidos e horários continuam sendo mantidos pelo seed/catalogo e não são alegados como suporte GTFS completo.
