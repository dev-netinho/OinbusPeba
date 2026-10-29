-- Geometrias demonstrativas com pontos intermediários levemente deslocados.
-- Elas melhoram a leitura do corredor no mapa, mas não são navegação viária oficial.
WITH points AS (
  SELECT p.id, (dp.path)[1] AS idx, dp.geom,
         lead(dp.geom) OVER (PARTITION BY p.id ORDER BY (dp.path)[1]) AS next_geom
  FROM transit_pattern p
  CROSS JOIN LATERAL ST_DumpPoints(p.geometry) dp
), expanded AS (
  SELECT id, idx * 2 AS seq, geom FROM points
  UNION ALL
  SELECT id, idx * 2 + 1, ST_SetSRID(ST_MakePoint(
    (ST_X(geom)+ST_X(next_geom))/2 + CASE WHEN idx % 2 = 0 THEN 0.00055 ELSE -0.00045 END,
    (ST_Y(geom)+ST_Y(next_geom))/2 + CASE WHEN idx % 2 = 0 THEN 0.00022 ELSE -0.00018 END
  ),4326)
  FROM points WHERE next_geom IS NOT NULL
), rebuilt AS (
  SELECT id, ST_MakeLine(geom ORDER BY seq) AS geometry
  FROM expanded GROUP BY id
)
UPDATE transit_pattern p
SET geometry = rebuilt.geometry,
    provenance = p.provenance || '{"geometria":"polilinha_sintetica_com_pontos_intermediarios","validacao":"nao_e_navegacao_viaria"}'::jsonb
FROM rebuilt WHERE rebuilt.id=p.id;
