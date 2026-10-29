# Progresso

Atualizado em 2026-10-29 após deploy externo autorizado e smoke tests públicos.

## Evidência de produção

- URL: `https://oinbuspeba.olua.me` (HTTPS válido via túnel Cloudflare dedicado).
- Commit implantado: `2f54e11dfb9227655a373fda99c797938562fcba`.
- Frontend, backend, PostgreSQL/PostGIS e RabbitMQ estão saudáveis; os volumes
  `oinbupebas_postgres_data` e `oinbupebas_rabbitmq_data` são próprios do projeto.
- Flyway v1–v3 e seed demonstrativo foram aplicados no banco de produção.
- Smoke tests externos aprovados: catálogo, Tropical → Partage com alternativas
  direta/1/2 baldeações, SSE, cadastro, login/logout, favorito persistido e 401
  em endpoint administrativo sem sessão.
- Testcontainers continua não executável no workstation por falta de Docker;
  o build remoto e a inicialização real no VPS foram aprovados.

## Legenda de estado

- **não implementado**: não há fluxo funcional correspondente.
- **parcial**: existe uma parte do fluxo, mas falta comportamento obrigatório.
- **implementado não validado**: o código existe, porém a infraestrutura necessária ainda não foi exercitada.
- **validado com evidência**: foi executado um teste ou comando reproduzível, indicado na coluna de teste.
- **bloqueado externamente**: depende de autorização, credencial ou infraestrutura que não está disponível no ambiente.

## Auditoria requisito × implementação

| Requisito | Existente | Lacuna concreta | Teste necessário | Estado |
|---|---|---|---|---|
| Monólito modular com seis módulos | Pacotes `identity`, `transit`, `journey`, `simulation`, `personalization` e `operations`; ArchUnit | Algumas fronteiras ainda expõem repositórios diretamente em controllers e faltam APIs explícitas de administração/importação | ArchUnit e testes de aplicação entre módulos | parcial |
| Catálogo PostgreSQL/PostGIS | Migrações V1–V3, seed de 44 paradas/9 linhas, padrões, calendários, viagens e stop times; `PostgresTransitCatalog` lê JDBC com SRID 4326 e índices | Banco real não foi exercitado neste host | Testcontainers PostgreSQL/PostGIS, Flyway e consultas reais | implementado não validado |
| Dataset de 6–10 linhas e 40–80 paradas | 9 linhas e 44 paradas persistidas por V2, com ambos os sentidos, calendários e partidas | Contagens aguardam banco real | Contagens e cenário Tropical → Partage após migrações | implementado não validado |
| Proveniência granular | Paradas, linhas, padrões e viagens seed carregam JSONB/texto de proveniência; importação exige `provenance` | Assertions no banco real aguardam Docker | Consulta da API e assertions no banco | implementado não validado |
| Planejador temporal genérico | `TimeDependentJourneyPlanner` busca sobre `TransitCatalog` persistido, respeita sentidos, horários, conexão mínima, caminhada e offsets | Rede viária e acessibilidade não são modeladas | Direto, 1 e 2 baldeações, partida perdida, sem serviço, caminhada, sentido e pós-meia-noite | validado com evidência |
| Até três alternativas não redundantes | Seleção multiobjetivo de melhor tempo, menos baldeações e menos caminhada; UI renderiza cartões | Diversidade final depende da rede seed e precisa de consulta real para confirmação | Testes de diversidade por tempo, caminhada e baldeações | implementado não validado |
| Busca local sem API paga | Selects locais com três opções | Não consulta catálogo persistido nem todos os POIs/paradas; clique no mapa não define coordenada | API de busca e E2E de seleção | parcial |
| Autenticação por sessão, CSRF e autorização | Spring Security, BCrypt, cookie de sessão, CSRF e papéis USER/ADMIN | Login ainda sem E2E real; Basic Auth está habilitado desnecessariamente; cookie Secure depende de ambiente | MockMvc/integração de login, CSRF, 401/403 e sessão | parcial |
| Cadastro e propriedade dos favoritos | Cadastro persistente e favoritos filtrados por principal | Delete inexistente responde 204; faltam preferências, histórico e exclusão de conta | Testes de propriedade, conta e concorrência | parcial |
| Recuperação de senha | Token aleatório armazenado somente como SHA-256, validade 30 min, consumo único e SMTP/Mailpit configurável | Mailpit real não foi iniciado | Token válido, expirado, reutilizado, inválido e conta inexistente | implementado não validado |
| Bootstrap administrativo seguro | `AdminBootstrap` exige `ADMIN_EMAIL`/`ADMIN_PASSWORD` pareados, senha ≥14 e não cria padrão | Startup contra banco real não foi executado | Configuração completa/incompleta e senha não registrada | implementado não validado |
| CRUD administrativo e concorrência otimista | Edição de linhas, importação e simulação têm rotas protegidas; `version` produz 409 e auditoria | Avisos e controles avançados de simulação ainda não têm CRUD dedicado | MockMvc e integração com duas versões concorrentes | parcial |
| Simulação compartilhada | Serviço deriva viagens/paradas do catálogo, interpola geometria e usa Clock injetável; SSE/polling existentes | Banco real e E2E SSE não foram exercitados | Teste determinístico de geometria/paradas e E2E SSE/fallback | implementado não validado |
| Outbox transacional | Entidade/tabela, agendador e publicação AMQP com `messageId` | Evento é marcado publicado sem publisher confirm; não há claim concorrente, consumer funcional nem correlação completa | RabbitMQ real: confirmação, reinício e publicação duplicada | parcial |
| Consumidor idempotente | Listener de importação registra `eventId` e staging na mesma transação; duplicata é ignorada | Consumer real não foi exercitado sem RabbitMQ | Evento duplicado deve produzir um único efeito na mesma transação | implementado não validado |
| Retry, DLQ e reprocessamento | Fila de importação, retry 3x com backoff, DLQ, contagem administrativa e reprocessamento auditado | Broker real não foi exercitado | Falha transitória, permanente, DLQ e reprocessamento reais | implementado não validado |
| Importação CSV com staging/publicação atômica | V3 persiste bytes/tarefa, valida CSV STOP/LINE, registra erros por linha, faz staging e publicação transacional/upsert | Teste real de banco e broker bloqueado | CSV inválido preserva catálogo; CSV válido publica conjunto consistente | implementado não validado |
| Interface pública integrada | Página responsiva, catálogo real, login/cadastro/recuperação, favoritos, edição admin e importação; falhas não viram sucesso | E2E real aguarda serviços e Chromium | Vitest de erro sem fallback e E2E com backend real | implementado não validado |
| Estados e acessibilidade | Labels, foco, alerta, localização negada, layout móvel e aviso persistente | Sessão expirada, permissão, mapa indisponível e posição obsoleta não estão todos ligados a respostas reais | Testes de componente e E2E teclado/mobile/falhas | parcial |
| PWA básica | Manifesto, ícones e service worker de shell | Estratégia offline ainda precisa ser validada sem cache privado/telemetria antiga | Build e teste offline controlado | implementado não validado |
| Compose desenvolvimento/produção | `compose.yml`, `compose.prod.yml`, Dockerfiles, Caddy e healthchecks | Docker/Podman não está instalado neste host nesta auditoria | `docker compose up`, healthchecks, migrations e smoke test | bloqueado externamente |
| Testes backend unitários | 22 testes executados: planner, tokens, CSV, simulação e arquitetura | Sem cobertura de integração real | `./mvnw test` após cada fatia | validado com evidência |
| Testcontainers PostGIS/RabbitMQ | `InfrastructureIntegrationTest` cobre seed, PostGIS, importação, duplicata e DLQ e é omitido sem Docker | Runtime de containers ausente, portanto 3 testes foram skipped | Suíte Testcontainers com migrations, persistência e AMQP | bloqueado externamente |
| E2E dos 12 fluxos | Especificação atual cobre falha de catálogo sem fallback; componentes reais estão integrados | Chromium não foi disponibilizado e Compose não sobe sem Docker | Playwright apontado para Compose saudável | bloqueado externamente |
| Documentação acadêmica e diagramas | README, relatório MD/PDF, documentos e 9 diagramas renderizados | Precisam ser sincronizados somente depois das implementações e validações desta rodada | Verificação de links, renderização e coerência com código | implementado não validado |
| Deploy externo | `compose.vps.yml`, túnel dedicado, DNS CNAME e volumes isolados | Playwright público completo ainda não foi executado | Smoke externo de frontend, API, catálogo, planejamento, SSE, conta e autorização | validado com evidência |

## Evidência preservada da rodada anterior

- Maven: 22 testes executados e aprovados; 3 testes Testcontainers foram skipped por ausência de Docker.
- ESLint e build Vite: aprovados.
- Vitest: 1 teste aprovado.
- Playwright: execução atual bloqueada antes do browser; o teste antigo contra fallback foi substituído para não validar sucesso fictício.
- Auditoria npm: 0 vulnerabilidades reportadas.

Esses resultados não validam PostgreSQL/PostGIS, RabbitMQ, migrações, autenticação integrada ou os fluxos administrativos. A presente rodada deve substituir evidências antigas quando o comportamento correspondente mudar.

## Condições externas observadas agora

- Ambiente: Fedora Linux 44, x86_64, usuário `olua-dev`, `/bin/sh`, dentro do escopo Flatpak do VS Code; não é container Docker.
- `docker`, `docker-compose`, `podman`, `nerdctl`, `java`, `mvn`, `node`, `npm` e navegadores do sistema não estão no `PATH`.
- Há um JDK 21, Maven e Node locais em `.tools/`, ignorados pelo Git, que podem ser usados sem alteração global.
- Não existe `/var/run/docker.sock` nem `/run/podman/podman.sock`; não há processos Docker/Podman/PostgreSQL/RabbitMQ.
- Playwright 1.56.1 é a versão do projeto. O cache possui Chromium 1248 (Chrome for Testing 156.0.8078.4), enquanto a versão do projeto requer a revisão 1194; ele não foi usado como substituto arbitrário. A tentativa `node frontend/node_modules/playwright-core/cli.js install chromium` não produziu progresso em aproximadamente 12 segundos e foi interrompida.
- O deploy foi autorizado para `koss-vps` e `oinbuspeba.olua.me`; a credencial Cloudflare foi usada apenas para DNS e para o túnel dedicado do projeto.

### Retomada quando o ambiente for liberado

No Fedora, disponibilize Docker Engine/Compose ou Podman compatível com Testcontainers e permita o socket ao usuário `olua-dev`; depois, no repositório, execute `docker compose config`, `docker compose up --build -d`, `docker compose ps`, `./scripts/smoke-test.sh http://localhost:8080`, `JAVA_HOME=.tools/jdk21 .tools/maven/bin/mvn -f backend/pom.xml test` e o Playwright com a revisão Chromium instalada pela própria versão 1.56.1. Não é necessário apagar os volumes existentes; para banco limpo, use um projeto Compose isolado com volumes nomeados distintos.
