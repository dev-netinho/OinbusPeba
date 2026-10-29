# Arquitetura

## Visão

O backend é um único artefato Spring Boot, organizado por domínio. Banco único é uma decisão operacional, não permissão para ignorar limites internos.

| Módulo | Responsabilidade | Dados próprios | Interface principal |
|---|---|---|---|
| `identity` | sessão, conta, papel e recuperação | `identity_*` | `/api/auth`, `/api/me` |
| `transit` | operadores, linhas, sentidos, paradas, calendários e viagens | `transit_*` | `TransitCatalog`, `/api/public/transit` |
| `journey` | busca dependente de horário e alternativas | consulta catálogo; histórico futuro | `JourneyPlanner`, `/api/public/journeys` |
| `simulation` | relógio e posição determinística no itinerário | execução efêmera compartilhada | SSE e polling `/api/simulation` |
| `personalization` | favoritos e preferências com propriedade | `personalization_*` | `/api/favorites` |
| `operations` | importação, auditoria, outbox, deduplicação e falhas | `operations_*` | `/api/admin/operations`, AMQP |

REST/JSON é síncrono entre navegador e backend. `async/await` no frontend não muda essa natureza. SSE transporta estado efêmero da simulação, com polling de fallback. RabbitMQ executa tarefas e eventos persistentes; consultas comuns não passam pela fila. Caddy é reverse proxy TLS, não API Gateway. Não há Service Mesh.

## Planejamento

A busca recebe data de serviço, saída, caminhada e baldeações. Para cada embarque, calcula chegada à parada, elimina partidas perdidas, preserva sentido/ordem, exige conexão mínima e soma caminhada, espera e tempo embarcado. O núcleo é determinístico e documenta complexidade `O(L × T × S²)` para rótulos, viagens e paradas do dataset demonstrativo. Não há promessa de ótimo global; no máximo três alternativas cronologicamente viáveis são apresentadas.

## Espaço e tempo

- Timezone: `America/Belem`; data de serviço é separada do horário.
- Migração aceita offsets em segundos, inclusive acima de 86.400, para viagens após meia-noite.
- Armazenamento: EPSG:4326; `geography(Point,4326)` e índices GiST para distâncias métricas.
- A simulação interpola trechos consecutivos da polilinha e não usa ponto aleatório.

## Segurança

Sessão HttpOnly, SameSite=Lax, Secure configurável/obrigatório em produção, CSRF por cookie/cabeçalho, BCrypt custo 12, autorização no backend e consulta de favoritos por usuário. CSP, limites de upload e Problem Details reduzem superfície. Tokens, senhas e GPS contínuo não entram em logs.
