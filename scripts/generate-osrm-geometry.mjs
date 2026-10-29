/** Generate the checked-in PostGIS migration from public OSRM/OSM routes.
 * Usage: node scripts/generate-osrm-geometry.mjs 2026-10-29
 */
import {writeFile} from 'node:fs/promises';
const accessDate=process.argv[2]??new Date().toISOString().slice(0,10);
const routes={
  'demo-tropical':[['tropical',-49.9304,-6.0639],['av-tropical',-49.9213,-6.0672],['apoena',-49.9101,-6.0698],['amazonia',-49.899,-6.0712],['beira-rio',-49.8874,-6.0744],['parada-amazonica',-49.8758,-6.077]],
  ta275:[['parada-amazonica',-49.8758,-6.077],['pa275-1',-49.868,-6.08],['pa275-2',-49.861,-6.083],['partage',-49.85605,-6.09164]],
  a001:[['cidade-jardim',-49.887,-6.058],['cidade-jardim-2',-49.8912,-6.0552],['cidade-jardim-3',-49.8842,-6.057],['rio-verde',-49.881,-6.061],['parada-amazonica',-49.8758,-6.077]],
  a002:[['nova-carajas',-49.85,-6.049],['nova-carajas-2',-49.856,-6.0502],['nova-carajas-3',-49.861,-6.052],['novo-brasil',-49.869,-6.078],['parada-amazonica',-49.8758,-6.077]],
  a003:[['paraiso',-49.903,-6.081],['paraiso-2',-49.899,-6.079],['paraiso-3',-49.895,-6.0772],['cidade-nova',-49.892,-6.073],['parada-amazonica',-49.8758,-6.077]],
  a005:[['ufra',-49.922,-6.047],['ufra-2',-49.9181,-6.0502],['tropical-2',-49.9261,-6.0621],['cidade-nova',-49.892,-6.073],['parada-amazonica',-49.8758,-6.077]],
  d06:[['liberdade',-49.916,-6.086],['liberdade-2',-49.912,-6.083],['paraiso',-49.903,-6.081],['cidade-nova',-49.892,-6.073],['uniao-2',-49.879,-6.088],['uniao',-49.874,-6.091]],
  d07:[['vale-sol',-49.932,-6.079],['vale-sol-2',-49.927,-6.076],['tropical',-49.9304,-6.0639],['liberdade',-49.916,-6.086],['altamira-2',-49.906,-6.099],['complexo-altamira',-49.91,-6.102]],
  d08:[['tropical',-49.9304,-6.0639],['apoena',-49.9101,-6.0698],['pa160',-49.883,-6.068],['parada-amazonica',-49.8758,-6.077],['pa275-1',-49.868,-6.08],['partage',-49.85605,-6.09164]]
};
async function route(points){
  const coords=points.map(point=>`${point[1]},${point[2]}`).join(';');
  const url=`https://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=geojson&steps=false`;
  const response=await fetch(url); if(!response.ok) throw new Error(`${response.status} ${url}`);
  const data=await response.json(); if(data.code!=='Ok') throw new Error(`${data.code} ${url}`);
  return data.routes[0].geometry.coordinates;
}
let sql=`-- Rotas pré-computadas pelo OSRM sobre dados OpenStreetMap.\n-- Consulta: ${accessDate}; uso: demonstração acadêmica, não itinerário oficial.\n`;
for(const [line,stops] of Object.entries(routes)) for(const direction of [0,1]){
  const coordinates=await route(direction===0?stops:stops.toReversed());
  const wkt=coordinates.map(([lon,lat])=>`${lon.toFixed(6)} ${lat.toFixed(6)}`).join(',');
  sql+=`UPDATE transit_pattern SET geometry=ST_GeomFromText('LINESTRING(${wkt})',4326), provenance=provenance || '{"geometria":"OSRM_sobre_OSM","fonte":"https://router.project-osrm.org","mapa_base":"OpenStreetMap","data_consulta":"${accessDate}","limitacao":"rota_demonstrativa_nao_oficial"}'::jsonb WHERE id=md5('pattern-${line}-${direction}')::uuid;\n`;
}
await writeFile('backend/src/main/resources/db/migration/V5__osrm_osm_route_geometry.sql',sql);
console.log(`generated ${sql.length} bytes`);
