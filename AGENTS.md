# Orientações para agentes

- Leia `prompt.txt`, `PRODUCT.md`, `DESIGN.md` (quando existir) e `docs/progresso.md` antes de mudanças amplas.
- Preserve o monólito modular: módulos podem consumir APIs públicas de outros módulos, nunca seus repositórios internos.
- Todo dado demonstrativo precisa declarar proveniência; horários, geometrias aproximadas e veículos nunca são apresentados como operação real.
- Use `America/Belem`, `EPSG:4326` para armazenamento e `geography`/projeção métrica para distância.
- Não exponha PostgreSQL, RabbitMQ, Mailpit nem Actuator sensível em produção.
- Não inclua segredos. Atualize `.env.example` e a documentação ao adicionar configuração.
- Mudanças funcionais exigem teste proporcional e atualização de `docs/progresso.md` com resultados reais.
- Não faça deploy, DNS ou alterações de VPS sem autorização explícita do alvo.
