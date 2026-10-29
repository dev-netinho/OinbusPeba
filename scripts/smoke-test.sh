#!/bin/sh
set -eu
base_url="${1:-http://localhost:8080}"
curl -fsS "$base_url/actuator/health/readiness"
curl -fsS "$base_url/api/public/transit/catalog" >/dev/null
curl -fsS -H 'Content-Type: application/json' -d '{"originId":"tropical","destinationId":"partage","serviceDate":"2026-10-28","departure":"07:20","maxWalkMeters":800,"maxTransfers":2,"preference":"FASTEST"}' "$base_url/api/public/journeys/plan" | grep -q 'TA275'
printf '%s\n' 'Smoke test concluído.'
