-- Rede acadêmica reproduzível. Nomes apoiados em fontes públicas e todos os
-- horários/geometrias marcados como sintéticos na proveniência.
INSERT INTO transit_operator (id, name, provenance) VALUES
('00000000-0000-0000-0000-000000000001', 'Operador demonstrativo OinbuPebas',
 '{"natureza":"sintetico","aviso":"Não representa operador ou serviço oficial"}');

INSERT INTO transit_stop (id, name, location, accessible, provenance) VALUES
('tropical','Tropical',ST_SetSRID(ST_MakePoint(-49.9304,-6.0639),4326)::geography,NULL,'{"nome":"fonte_publica_historica","coordenada":"aproximada"}'),
('av-tropical','Av. Tropical',ST_SetSRID(ST_MakePoint(-49.9213,-6.0672),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('apoena','Apoena',ST_SetSRID(ST_MakePoint(-49.9101,-6.0698),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('amazonia','Bairro Amazônia',ST_SetSRID(ST_MakePoint(-49.8990,-6.0712),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('beira-rio','Beira Rio',ST_SetSRID(ST_MakePoint(-49.8874,-6.0744),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('parada-amazonica','Parada Amazônica',ST_SetSRID(ST_MakePoint(-49.8758,-6.0770),4326)::geography,NULL,'{"nome":"fonte_municipal","coordenada":"aproximada"}'),
('pa275-1','Corredor PA-275 · 1',ST_SetSRID(ST_MakePoint(-49.8680,-6.0800),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('pa275-2','Corredor PA-275 · 2',ST_SetSRID(ST_MakePoint(-49.8610,-6.0830),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('partage','Partage Shopping',ST_SetSRID(ST_MakePoint(-49.85605,-6.09164),4326)::geography,NULL,'{"nome":"ponto_interesse_verificavel","coordenada":"osm"}'),
('cidade-jardim','Cidade Jardim',ST_SetSRID(ST_MakePoint(-49.8870,-6.0580),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('nova-carajas','Nova Carajás',ST_SetSRID(ST_MakePoint(-49.8500,-6.0490),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('paraiso','Paraíso',ST_SetSRID(ST_MakePoint(-49.9030,-6.0810),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('ufra','UFRA',ST_SetSRID(ST_MakePoint(-49.9220,-6.0470),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('rio-verde','Rio Verde',ST_SetSRID(ST_MakePoint(-49.8810,-6.0610),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('cidade-nova','Cidade Nova',ST_SetSRID(ST_MakePoint(-49.8920,-6.0730),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('liberdade','Liberdade',ST_SetSRID(ST_MakePoint(-49.9160,-6.0860),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('uniao','União',ST_SetSRID(ST_MakePoint(-49.8740,-6.0910),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('alvora','Alvorá',ST_SetSRID(ST_MakePoint(-49.8610,-6.0960),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('vale-sol','Vale do Sol',ST_SetSRID(ST_MakePoint(-49.9320,-6.0790),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('complexo-altamira','Complexo Altamira',ST_SetSRID(ST_MakePoint(-49.9100,-6.1020),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('tropical-2','Tropical · Rua 12',ST_SetSRID(ST_MakePoint(-49.9261,-6.0621),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('tropical-3','Tropical · Rua 8',ST_SetSRID(ST_MakePoint(-49.9230,-6.0648),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('apoena-2','Apoena · Praça',ST_SetSRID(ST_MakePoint(-49.9142,-6.0680),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('amazonia-2','Amazônia · Avenida A',ST_SetSRID(ST_MakePoint(-49.9048,-6.0701),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('beira-rio-2','Beira Rio · Mercado',ST_SetSRID(ST_MakePoint(-49.8923,-6.0730),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('cidade-jardim-2','Cidade Jardim · Escola',ST_SetSRID(ST_MakePoint(-49.8912,-6.0552),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('cidade-jardim-3','Cidade Jardim · Praça',ST_SetSRID(ST_MakePoint(-49.8842,-6.0570),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('nova-carajas-2','Nova Carajás · Etapa 1',ST_SetSRID(ST_MakePoint(-49.8560,-6.0502),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('nova-carajas-3','Nova Carajás · Etapa 2',ST_SetSRID(ST_MakePoint(-49.8610,-6.0520),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('paraiso-2','Paraíso · Rua Principal',ST_SetSRID(ST_MakePoint(-49.8990,-6.0790),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('paraiso-3','Paraíso · Feira',ST_SetSRID(ST_MakePoint(-49.8950,-6.0772),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('ufra-2','UFRA · Acesso',ST_SetSRID(ST_MakePoint(-49.9181,-6.0502),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('rio-verde-2','Rio Verde · Praça',ST_SetSRID(ST_MakePoint(-49.8850,-6.0628),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('cidade-nova-2','Cidade Nova · Hospital',ST_SetSRID(ST_MakePoint(-49.8950,-6.0702),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('liberdade-2','Liberdade · Av. Central',ST_SetSRID(ST_MakePoint(-49.9120,-6.0830),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('uniao-2','União · Praça',ST_SetSRID(ST_MakePoint(-49.8790,-6.0880),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('alvora-2','Alvorá · Av. dos Ipês',ST_SetSRID(ST_MakePoint(-49.8650,-6.0930),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('vale-sol-2','Vale do Sol · Escola',ST_SetSRID(ST_MakePoint(-49.9270,-6.0760),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('altamira-2','Altamira · Praça',ST_SetSRID(ST_MakePoint(-49.9060,-6.0990),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('pa160','Corredor PA-160',ST_SetSRID(ST_MakePoint(-49.8830,-6.0680),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('faruk-salmen','Av. Faruk Salmen',ST_SetSRID(ST_MakePoint(-49.8960,-6.0880),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('novo-brasil','Novo Brasil',ST_SetSRID(ST_MakePoint(-49.8690,-6.0780),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('polo-moveleiro','Polo Moveleiro',ST_SetSRID(ST_MakePoint(-49.8830,-6.0950),4326)::geography,NULL,'{"natureza":"sintetico"}'),
('terminal-cidade-nova','Terminal Cidade Nova',ST_SetSRID(ST_MakePoint(-49.8890,-6.0720),4326)::geography,NULL,'{"natureza":"sintetico"}');

INSERT INTO transit_line (id, operator_id, short_name, name, color, provenance) VALUES
('demo-tropical','00000000-0000-0000-0000-000000000001','D01','Tropical ↔ Parada Amazônica','#176A9B','{"natureza":"sintetico"}'),
('ta275','00000000-0000-0000-0000-000000000001','TA275','Parada Amazônica ↔ Partage','#537A3B','{"nome":"fonte_municipal","itinerario_horarios":"sintetico"}'),
('a001','00000000-0000-0000-0000-000000000001','A001','Cidade Jardim','#A44A2D','{"nome":"fonte_municipal","demais_campos":"sintetico"}'),
('a002','00000000-0000-0000-0000-000000000001','A002','Nova Carajás','#67558F','{"nome":"fonte_municipal","demais_campos":"sintetico"}'),
('a003','00000000-0000-0000-0000-000000000001','A003','Paraíso','#A47B22','{"nome":"fonte_municipal","demais_campos":"sintetico"}'),
('a005','00000000-0000-0000-0000-000000000001','A005','UFRA','#397D8C','{"nome":"fonte_municipal","demais_campos":"sintetico"}'),
('d06','00000000-0000-0000-0000-000000000001','D06','Liberdade ↔ União','#8A4F62','{"natureza":"sintetico"}'),
('d07','00000000-0000-0000-0000-000000000001','D07','Vale do Sol ↔ Altamira','#77654B','{"natureza":"sintetico"}'),
('d08','00000000-0000-0000-0000-000000000001','D08','Tropical ↔ Partage','#2E746B','{"natureza":"inteiramente_sintetico","finalidade":"alternativa_direta_de_teste"}');

CREATE TEMP TABLE seed_route(line_id text PRIMARY KEY, stops text[]) ON COMMIT DROP;
INSERT INTO seed_route VALUES
('demo-tropical',ARRAY['tropical','av-tropical','apoena','amazonia','beira-rio','parada-amazonica']),
('ta275',ARRAY['parada-amazonica','pa275-1','pa275-2','partage']),
('a001',ARRAY['cidade-jardim','cidade-jardim-2','cidade-jardim-3','rio-verde','parada-amazonica']),
('a002',ARRAY['nova-carajas','nova-carajas-2','nova-carajas-3','novo-brasil','parada-amazonica']),
('a003',ARRAY['paraiso','paraiso-2','paraiso-3','cidade-nova','parada-amazonica']),
('a005',ARRAY['ufra','ufra-2','tropical-2','cidade-nova','parada-amazonica']),
('d06',ARRAY['liberdade','liberdade-2','paraiso','cidade-nova','uniao-2','uniao']),
('d07',ARRAY['vale-sol','vale-sol-2','tropical','liberdade','altamira-2','complexo-altamira']),
('d08',ARRAY['tropical','apoena','pa160','parada-amazonica','pa275-1','partage']);

INSERT INTO transit_pattern(id,line_id,direction_id,headsign,geometry,provenance)
SELECT md5('pattern-'||r.line_id||'-'||d)::uuid, r.line_id, d,
       CASE WHEN d=0 THEN z.name ELSE a.name END,
       ST_MakeLine(array_agg(s.location::geometry ORDER BY CASE WHEN d=0 THEN u.ord ELSE cardinality(r.stops)-u.ord+1 END)),
       '{"natureza":"sintetico","geometria":"aproximacao_por_paradas"}'::jsonb
FROM seed_route r CROSS JOIN generate_series(0,1) d
CROSS JOIN LATERAL unnest(r.stops) WITH ORDINALITY u(stop_id,ord)
JOIN transit_stop s ON s.id=u.stop_id
JOIN transit_stop a ON a.id=r.stops[1]
JOIN transit_stop z ON z.id=r.stops[cardinality(r.stops)]
GROUP BY r.line_id,r.stops,d,z.name,a.name;

INSERT INTO transit_pattern_stop(pattern_id,stop_id,stop_sequence,distance_m)
SELECT md5('pattern-'||r.line_id||'-'||d)::uuid, u.stop_id,
       CASE WHEN d=0 THEN u.ord ELSE cardinality(r.stops)-u.ord+1 END,
       (CASE WHEN d=0 THEN u.ord-1 ELSE cardinality(r.stops)-u.ord END)*1200
FROM seed_route r CROSS JOIN generate_series(0,1) d
CROSS JOIN LATERAL unnest(r.stops) WITH ORDINALITY u(stop_id,ord);

INSERT INTO transit_calendar(id,name,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date)
VALUES ('00000000-0000-0000-0000-000000000010','Demonstração diária',true,true,true,true,true,true,true,'2025-01-01','2035-12-31');

-- Partidas a cada 30 minutos; offsets podem exceder 24h no modelo.
INSERT INTO transit_trip(id,pattern_id,calendar_id,service_start_seconds,provenance)
SELECT md5('trip-'||p.id||'-'||start_second)::uuid,p.id,
       '00000000-0000-0000-0000-000000000010',start_second,
       '{"natureza":"sintetico","nao_e_horario_oficial":true}'::jsonb
FROM transit_pattern p CROSS JOIN generate_series(21600,75600,1800) start_second;

INSERT INTO transit_stop_time(trip_id,stop_sequence,arrival_offset_seconds,departure_offset_seconds)
SELECT t.id,ps.stop_sequence,(ps.stop_sequence-1)*480,(ps.stop_sequence-1)*480+30
FROM transit_trip t JOIN transit_pattern_stop ps ON ps.pattern_id=t.pattern_id;

CREATE INDEX transit_pattern_line_idx ON transit_pattern(line_id);
CREATE INDEX transit_pattern_stop_stop_idx ON transit_pattern_stop(stop_id,pattern_id,stop_sequence);
CREATE INDEX transit_trip_pattern_calendar_start_idx ON transit_trip(pattern_id,calendar_id,service_start_seconds);
CREATE INDEX transit_trip_calendar_idx ON transit_trip(calendar_id);
