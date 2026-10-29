# Casos de uso

## UC-01 — Planejar Tropical → Partage

**Ator:** visitante. **Objetivo:** comparar uma viagem demonstrativa. **Pré-condição:** data/saída válidas. **Fluxo:** informa origem/destino; sistema encontra partidas não perdidas; avalia conexão; apresenta até três alternativas e etapas. **Alternativas:** amplia caminhada/horário. **Falhas:** sem serviço ou API indisponível são explicados. **Pós-condição:** nenhuma reserva. **Aceite:** D01 + TA275, Parada Amazônica e rótulo sintético aparecem.

## UC-02 — Acompanhar veículo simulado

**Ator:** visitante. **Fluxo:** abre mapa; recebe SSE; seleciona marcador; vê linha, sentido, próxima parada e ETA simulado. **Falha:** SSE cai, posição fica stale e polling assume. **Aceite:** nenhum texto afirma GPS real.

## UC-03 — Cadastrar e autenticar

**Ator:** visitante. **Fluxo:** envia e-mail/nome/senha; senha é BCrypt; entra por sessão HttpOnly; sai invalidando sessão. **Falhas:** resposta de recuperação evita enumeração. **Aceite:** recurso protegido retorna 401 sem sessão.

## UC-04 — Salvar favorito

**Ator:** usuário. **Pré-condição:** sessão. **Fluxo:** salva intenção origem/destino; lista; reabre e recalcula; remove. **Falha:** ID alheio não é removido. **Pós-condição:** nenhuma viagem é reservada.

## UC-05 — Importar catálogo

**Ator:** administrador. **Fluxo:** envia CSV até 2 MB; cria tarefa `PENDING` e evento outbox; worker valida staging; prévia precede publicação atômica. **Falhas:** extensão/tamanho inválido é rejeitado; RabbitMQ indisponível preserva outbox. **Estado:** recepção/outbox implementadas; staging/confirmar publicação pendentes.

## UC-06 — Recuperar falha

**Ator:** administrador. **Fluxo:** inspeciona tarefa/DLQ, corrige causa, solicita reprocessamento auditado. **Aceite:** mensagem duplicada não repete efeito. **Estado:** API, painel, consumidor, staging, retry e reprocessamento implementados; validação real aguarda RabbitMQ.
