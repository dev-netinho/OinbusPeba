# OinbuPebas — sistema visual

<!-- impeccable:design-schema 1 -->

## Direção

**Caderno de percurso.** A interface se comporta como um registro de campo contemporâneo: papel claro, tinta profunda, réguas finas e uma espinha temporal que costura cada etapa. A referência regional vive na paleta de floresta, rio e urucum e na sobriedade da sinalização cívica — nunca em ornamentos folclóricos.

## Form

Direção sorteada pelo processo `impeccable` em modo `operate`, seed corroborável `4d3a6c5d`, índice atribuído 7. A implementação adotou comp-first; a referência gerada está registrada abaixo.

## Princípios

- O primeiro viewport já é o planejador funcionando.
- A espinha do trajeto é o elemento memorável e também o principal mecanismo de compreensão.
- Mapa e lista têm peso equivalente; qualquer informação essencial do mapa aparece em texto.
- Superfícies são planas, com linhas de 1px e poucos raios. Sem gradientes, vidro ou sombras decorativas.
- Dados programados, inferidos e simulados recebem rótulos textuais, nunca apenas cor.

## Tokens

- `paper`: `#F3EBDD`; `paper-raised`: `#FFFDF7`
- `ink`: `#18231F`; `forest`: `#173D32`
- `urucum`: `#B94724`; `river`: `#397D8C`; `leaf`: `#687F52`
- `rule`: `#C9C0AF`; `muted`: `#5D675F`
- Texto: Source Sans 3; títulos editoriais: Newsreader; dados: Source Sans 3 com numerais tabulares.
- Raios: 12px para painéis, 999px apenas para tags e controles compactos.

## Composição

Desktop usa três zonas assimétricas: formulário de 22–26rem, mapa flexível e resultados/detalhes em faixa inferior. Mobile transforma formulário, resultados e detalhes numa sequência única; o mapa tem altura limitada e nunca bloqueia o fluxo textual.

## Movimento

Apenas a seleção de uma alternativa percorre a espinha temporal de cima para baixo. Duração curta, sem overshoot; em `prefers-reduced-motion`, a mudança é instantânea.

## Referência de composição

Comp gerado para direção visual: `.impeccable/mocks/decision/oinbupebas-caderno-percurso.png`. É referência de composição e acabamento, não evidência geográfica ou operacional.
