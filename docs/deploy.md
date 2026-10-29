# Deploy

Nenhum deploy externo foi autorizado ou realizado. Faltam host/subdomínio, acesso SSH e aprovação explícita dos recursos a alterar.

## Preparação

1. Crie token DNS Cloudflare restrito apenas ao registro necessário, se DNS automatizado for desejado. Ele não substitui SSH.
2. Na VPS, instale Docker/Compose e confirme portas 80/443 livres.
3. Copie o repositório sem `.tools`, crie `.env` com segredos longos e `APP_DOMAIN`.
4. Faça backup antes de atualizar.

```sh
docker compose -f compose.yml -f compose.prod.yml pull
docker compose -f compose.yml -f compose.prod.yml build
docker compose -f compose.yml -f compose.prod.yml up -d
./scripts/smoke-test.sh "https://$APP_DOMAIN"
```

Caddy obtém/renova TLS. Apenas 80/443 ficam públicos; banco, RabbitMQ, frontend interno e Actuator sensível permanecem na rede Compose. Readiness pode ser roteado somente para checagem controlada.

## Backup e restauração

```sh
docker compose exec -T db pg_dump -Fc -U "$DATABASE_USER" "$DATABASE_NAME" > "backups/oinbupebas-$(date +%F).dump"
docker compose exec -T db pg_restore --clean --if-exists -U "$DATABASE_USER" -d "$DATABASE_NAME" < backups/arquivo.dump
```

Teste a restauração em banco separado. Proteja e rotacione backups; eles contêm dados de conta.

## Atualização e rollback

- Gere backup, leia migrações, construa imagens com tag imutável e rode smoke test.
- Migrações aditivas podem acompanhar o deploy. Migrações destrutivas exigem fase de compatibilidade e backup testado.
- Rollback de aplicação: reaponte tags anteriores e suba novamente. Não reverta banco automaticamente se a migração for incompatível; use procedimento específico.
- Confirme login, busca Tropical → Partage, SSE/polling, fila/outbox e logs sem segredos.
