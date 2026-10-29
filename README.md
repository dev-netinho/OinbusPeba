# OinbuPebas

Plataforma acadêmica, independente e não oficial, para demonstrar planejamento de viagens por transporte coletivo em Parauapebas (PA). O sistema explica caminhada aproximada, espera, sentido, viagem e baldeação sobre uma rede demonstrativa, além de exibir veículos simulados compartilhados.

> **Projeto acadêmico — veículos e estimativas simulados. Não utilize como única referência para deslocamentos reais.** O OinbuPebas não vende passagens, não controla ônibus e não fornece GPS real. Se a plataforma ficar indisponível, o transporte real continua normalmente.

## Stack

- Backend: Java 21, Spring Boot 3.5, Spring Security, JPA, Flyway, PostgreSQL/PostGIS, RabbitMQ, SSE e OpenAPI.
- Frontend: Vue 3, TypeScript, Vite 8, Pinia, Tailwind CSS 4 e Leaflet.
- Operação: Docker Compose, Caddy TLS em desenvolvimento, túnel Cloudflare
  dedicado na VPS, Mailpit em desenvolvimento e healthchecks.

## Acesso público

Ambiente de produção validado em <https://oinbuspeba.olua.me>. Frontend, API,
PostgreSQL/PostGIS, RabbitMQ e o túnel Cloudflare dedicado estão ativos na
VPS autorizada. O acesso administrativo é criado no primeiro boot por
`ADMIN_EMAIL`/`ADMIN_PASSWORD`, mantidos somente no `.env` da VPS; solicite
esse acesso ao responsável pela operação sem compartilhar a senha.

## Início rápido

Requisitos recomendados: Docker Engine 27+ com Compose v2.24+.

```sh
cp .env.example .env
# troque as senhas locais se a máquina for compartilhada
docker compose up --build
```

- Aplicação: <http://localhost:4173>
- API: <http://localhost:8080>
- Swagger: <http://localhost:8080/swagger-ui>
- Mailpit: <http://localhost:8025>

PostgreSQL, RabbitMQ e Actuator não são publicados no host. Não há conta administrativa padrão: defina `ADMIN_EMAIL` e `ADMIN_PASSWORD` (mínimo de 14 caracteres) no ambiente de desenvolvimento para o bootstrap único; nunca versione esses valores.

## Desenvolvimento sem containers

```sh
cd backend && ./mvnw spring-boot:run
cd frontend && npm ci && npm run dev
```

O backend ainda exige PostgreSQL/PostGIS e RabbitMQ. Use o Compose para esses serviços. Quando a API está indisponível, a interface mostra erro e não substitui resultados reais por dados locais.

## Testes

```sh
cd backend && ./mvnw test
cd frontend && npm ci && npm test && npm run build
./scripts/smoke-test.sh http://localhost:8080
```

Testes de integração com Testcontainers exigem Docker. Consulte [docs/testes.md](docs/testes.md) para resultados realmente executados e lacunas.

## Configuração

Variáveis documentadas em `.env.example`. Em produção, use segredos longos fora do Git, `COOKIE_SECURE=true`, um domínio válido e compose sobreposto:

```sh
docker compose -f compose.yml -f compose.prod.yml -f compose.vps.yml up -d --build
```

Leia [docs/deploy.md](docs/deploy.md) antes de publicar. A configuração
`compose.vps.yml` usa a rede externa `traefik-public` e o túnel Cloudflare
dedicado, sem abrir portas do banco ou RabbitMQ.

## Cenário demonstrativo

Em 28/10/2026 às 07:20, selecione `Tropical` → `Partage Shopping`. O catálogo seed persistido fornece alternativas diretas e com baldeação; o planejador consulta viagens, sentidos, calendários e stop times do banco. Nomes confirmados por fontes públicas não tornam itinerários ou horários oficiais.

## Limitações

- A rede contém 9 linhas, 44 paradas e até 12 veículos derivados das viagens ativas; itinerários, horários e a maioria das coordenadas são sintéticos/aproximados.
- Caminhadas usam estimativa conservadora, não navegação viária nem avaliação de acessibilidade/travessia.
- O planejador é uma busca temporal genérica sobre o catálogo seed; caminhada continua sendo aproximação geodésica conservadora.
- Recuperação de senha usa token hash de uso único, validade de 30 minutos e SMTP configurável; Compose local aponta para Mailpit.
- Administração cobre edição de linhas com versão otimista, staging/publicação CSV, outbox, consumidor idempotente, retry/DLQ e reprocessamento; a integração real precisa de Docker.
- O mapa base depende do OpenStreetMap; listas continuam úteis em falha.

## Troubleshooting

- `readiness` falha: verifique `docker compose ps` e logs de `db`/`backend`.
- Sem mapa: a camada externa pode estar indisponível; use as etapas textuais.
- Posição desatualizada: o cliente muda para polling após falha de SSE.
- RabbitMQ fora: consultas do catálogo continuam; eventos ficam pendentes na outbox e tarefas não devem ser anunciadas como concluídas.
- Para uma conta administrativa local, configure `ADMIN_EMAIL` e `ADMIN_PASSWORD` antes de iniciar o backend; o sistema não cria credencial padrão.
