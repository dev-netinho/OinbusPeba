# ADR 0001 — Monólito modular

Status: aceito — 2026-10-28

## Decisão

Um único backend Spring Boot contém `identity`, `transit`, `journey`, `simulation`, `personalization` e `operations`. Cada módulo expõe API de aplicação e possui suas tabelas; acesso direto a repositório alheio é proibido por teste ArchUnit.

## Motivo

O domínio e a equipe acadêmica não justificam custo operacional de microsserviços. REST atende o navegador, chamadas Java explícitas atendem operações imediatas e RabbitMQ é reservado a importação/estatísticas/eventos persistentes.

## Consequências

Banco único simplifica transações, mas não elimina fronteiras internas. O backend é um único artefato; frontend, PostgreSQL e RabbitMQ permanecem processos separados.
