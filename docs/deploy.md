# Deploy

Deploy realizado e validado em `https://oinbuspeba.olua.me` na VPS `koss-vps`
(Ubuntu 24.04, Docker). Commit implantado: `2a22f36`. O subdomínio usa um
túnel Cloudflare dedicado ao OinbuPebas, que encaminha `/api` ao backend e o
restante ao frontend. O túnel e os serviços usam volumes/rede próprios; não
há portas públicas para PostgreSQL, RabbitMQ, Mailpit ou Actuator.

## Preparação

1. Crie token DNS Cloudflare restrito apenas ao registro necessário, se DNS automatizado for desejado. Ele não substitui SSH.
2. Na VPS, instale Docker/Compose e confirme o proxy/túnel autorizado.
3. Copie o repositório sem `.tools`, crie `.env` com segredos longos e `APP_DOMAIN`.
4. Faça backup antes de atualizar.

```sh
git fetch origin main && git checkout main && git pull --ff-only
docker compose -f compose.yml -f compose.prod.yml -f compose.vps.yml up -d --build
./scripts/smoke-test.sh "https://$APP_DOMAIN"
```

O Cloudflare termina o TLS público; o túnel dedicado alcança somente os
containers do projeto na rede `traefik-public`. Banco, RabbitMQ, frontend
interno e Actuator permanecem sem portas públicas.

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

## Rollback executado de forma segura

Para retornar à versão anterior, use o commit desejado sem remover volumes:

```sh
git checkout <commit-anterior>
docker compose -f compose.yml -f compose.prod.yml -f compose.vps.yml up -d --build
```

Não use `docker system prune`, não remova volumes `oinbupebas_*` e não faça
rollback automático de migrations incompatíveis. O `.env` de produção fica
em `/home/olua_developer/oinbupebas/.env` com permissão restrita e não é
versionado.
