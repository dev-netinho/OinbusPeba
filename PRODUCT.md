# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Stack

Confirmada pelo briefing: Vue 3, TypeScript, Vite, Vue Router, Pinia, Tailwind CSS e Leaflet no frontend; Java 21, Spring Boot, Maven, PostgreSQL/PostGIS, RabbitMQ e Flyway no backend; Docker Compose e proxy TLS na implantação.

## Users

- Pessoa em Parauapebas que precisa comparar deslocamentos por transporte coletivo sem criar conta.
- Usuário cadastrado que salva intenções de trajeto e preferências, sem reservar viagem.
- Administrador responsável por catálogo demonstrativo, avisos, importações, simulação e recuperação operacional.

## Product Purpose

O OinbuPebas é uma plataforma acadêmica de mobilidade urbana que combina busca local, horários programados e uma simulação compartilhada para explicar alternativas de deslocamento. O fluxo demonstrativo indispensável é Tropical → Partage Shopping, com transparência sobre dados sintéticos e sem alegar operação oficial ou em tempo real.

## Positioning

O produto torna visível a composição temporal de um trajeto — caminhada aproximada, espera, veículo, sentido e baldeação — e preserva a proveniência campo a campo, em vez de sugerir uma rota apenas porque uma linha passa perto da origem e do destino.

## Operating Context

Uso em celular e desktop, muitas vezes sob conectividade instável e em movimento. O mapa apoia a decisão, mas listas e etapas textuais permanecem utilizáveis sem a camada cartográfica. A referência temporal é `America/Belem`; horários e veículos demonstrativos compartilham o mesmo relógio.

## Capabilities and Constraints

- Consulta pública de linhas, paradas, partidas, avisos, trajetos e veículos simulados.
- Sessão segura para perfil, preferências, histórico e favoritos; papel `ADMIN` para operações protegidas.
- Planejamento dependente do horário, com até duas baldeações e limites de caminhada.
- Monólito modular único; REST/JSON e SSE no navegador, AMQP apenas em tarefas assíncronas reais.
- Não vende passagens, não recebe pagamentos, não controla veículos, não fornece GPS real e não representa oficialmente o poder público ou operadores.
- Dados operacionais incompletos são explicitamente sintéticos; nomes sustentados por fonte pública não tornam horários inferidos em dados oficiais.
- Deploy externo depende de autorização explícita para host, domínio e alterações.

## Brand Commitments

Nome `OinbuPebas`, idioma português brasileiro, voz clara e respeitosa, identidade própria com referências regionais discretas, alto contraste e aviso acadêmico persistente. Nenhuma marca de aplicativo existente deve ser imitada.

## Evidence on Hand

- Briefing funcional e acadêmico em `prompt.txt`.
- Publicações públicas municipais confirmam nomes recentes de linhas e a Parada Amazônica, mas não sustentam uma grade completa atual.
- O endereço do Partage Shopping é confirmado pelo site oficial; geometrias, partidas e conexões do dataset demonstrativo serão marcadas como sintéticas ou aproximadas.
- Não há logotipo, fotografias licenciadas, depoimentos, métricas de uso ou autorização de publicação externa; nada disso pode ser fabricado.

## Product Principles

1. Explicar a viagem, não apenas desenhar uma linha.
2. Distinguir programação, inferência e simulação em cada decisão.
3. Continuar útil quando mapa, SSE ou mensageria falharem.
4. Proteger localização e histórico por padrão.
5. Manter limites modulares e recuperação operacional proporcionais ao projeto.

## Accessibility & Inclusion

Navegação completa por teclado, foco visível, rótulos explícitos, contraste WCAG AA, mensagens que não dependem apenas de cor, preferência por movimento reduzido e equivalentes textuais para toda informação essencial do mapa.
