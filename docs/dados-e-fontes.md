# Dados e proveniência

Data de acesso das fontes: **2026-10-28**.

| Fonte | Publicação | Sustenta | Limites/licença |
|---|---:|---|---|
| [Prefeitura — melhorias no transporte](https://parauapebas.pa.gov.br/destaque/transporte-publico-de-parauapebas-passa-por-melhorias-a-partir-de-setembro/) | 29/08/2025 | nomes TA275, A001 Cidade Jardim, A002 Nova Carajás, A003 Paraíso, A005 UFRA e integração na Parada Amazônica | notícia, não fornece grade/geometria completa; licença não identificada |
| [Prefeitura — inauguração da Parada Amazônica e Rota Fácil](https://parauapebas.pa.gov.br/destaque/inauguracao-da-parada-amazonica-e-da-linha-de-onibus-rota-facil/) | 30/05/2025 | existência/nome da Parada Amazônica e ligação anunciada Vale–Lago Nova Carajás | não confirma horário atual; licença não identificada |
| [Plano de Mobilidade P8.1](https://parauapebas.pa.gov.br/wp-content/uploads/2023/06/Parauapebas_PMU_Relatorio_P8.1_ajustado.pdf) | documento histórico | referência ao bairro/linha alimentadora Tropical e estrutura proposta | proposta histórica; não é confirmação operacional atual |
| [Plano de Mobilidade P7.2](https://parauapebas.pa.gov.br/wp-content/uploads/2021/11/Parauapebas_PMU_Relatorio_P7.2_v10.1_compressed.pdf) | documento histórico | ocorrência histórica do código IB003 | frequência antiga não reutilizada como atual |
| [Partage Shopping — site oficial](https://www.partageshoppingparauapebas.com.br/) | página vigente | nome e endereço Rod. Dr. Faisal Salmen, lote 01-A, Alvorá | não sustenta serviço de ônibus |
| [OpenStreetMap, objeto do Partage](https://www.openstreetmap.org/way/163163063) | base colaborativa | coordenada aproximada do POI/edificação | ODbL; atribuição obrigatória; não valida acesso pedestre |

## Classificação por campo

- **Baseado em fonte pública:** nomes TA275/A001/A002/A003/A005, Tropical, Parada Amazônica e Partage Shopping.
- **Aproximado/inferido:** coordenadas de bairros/paradas e polilinhas, úteis apenas à demonstração.
- **Inteiramente sintético:** D01/D06/D07, horários, stop times, conexões, calendários, IDs e posições de veículos.

O catálogo Java expõe `provenance` em parada e linha. A migração prevê `jsonb` por entidade/padrão/viagem. Uma etiqueta global “real” é deliberadamente evitada.

## Dataset

9 linhas, 44 paradas, sentidos separados, calendários, viagens e até 12 veículos derivados de viagens simuladas. O fluxo Tropical → Partage foi criado para demonstração acadêmica e não comprova uma ligação municipal específica. Barreiras, calçadas, rios, segurança de travessia e acessibilidade física não foram validados.
