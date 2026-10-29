#!/usr/bin/env python3
"""Gera docs/relatorio.pdf a partir do relatório Markdown sem acesso externo."""
from pathlib import Path
import re
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Image

ROOT = Path(__file__).resolve().parents[1]
source = ROOT / "docs" / "relatorio.md"
target = ROOT / "docs" / "relatorio.pdf"
font = Path("/usr/share/fonts/dejavu/DejaVuSans.ttf")
bold = Path("/usr/share/fonts/dejavu/DejaVuSans-Bold.ttf")
if font.exists():
    pdfmetrics.registerFont(TTFont("Report", str(font)))
    pdfmetrics.registerFont(TTFont("ReportBold", str(bold)))
    base, strong = "Report", "ReportBold"
else:
    base, strong = "Helvetica", "Helvetica-Bold"

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="Cover", fontName=strong, fontSize=28, leading=33, textColor=colors.HexColor("#173D32"), alignment=TA_CENTER, spaceAfter=24))
styles.add(ParagraphStyle(name="H1x", fontName=strong, fontSize=18, leading=22, textColor=colors.HexColor("#173D32"), spaceBefore=14, spaceAfter=8))
styles.add(ParagraphStyle(name="H2x", fontName=strong, fontSize=13, leading=17, textColor=colors.HexColor("#397D8C"), spaceBefore=10, spaceAfter=5))
styles.add(ParagraphStyle(name="Bodyx", fontName=base, fontSize=9.5, leading=14, textColor=colors.HexColor("#18231F"), spaceAfter=7))
styles.add(ParagraphStyle(name="Foot", fontName=base, fontSize=8, textColor=colors.HexColor("#58635C")))

def footer(canvas, doc):
    canvas.saveState(); canvas.setFont(base, 8); canvas.setFillColor(colors.HexColor("#58635C"))
    canvas.drawString(2*cm, 1.2*cm, "OinbuPebas · relatório acadêmico · dados demonstrativos")
    canvas.drawRightString(A4[0]-2*cm, 1.2*cm, str(doc.page)); canvas.restoreState()

story = [Paragraph("OinbuPebas", styles["Cover"]), Paragraph("Plataforma acadêmica de mobilidade urbana para Parauapebas", styles["H2x"]), Spacer(1, 2*cm)]
for line in source.read_text(encoding="utf-8").splitlines()[1:]:
    text = line.strip()
    if not text:
        story.append(Spacer(1, 4)); continue
    if text.startswith("## "):
        story.append(Paragraph(text[3:], styles["H1x"])); continue
    if text.startswith("### "):
        story.append(Paragraph(text[4:], styles["H2x"])); continue
    if text.startswith("**") and text.endswith("  "):
        text = text[:-2]
    text = re.sub(r"\[([^]]+)\]\([^)]+\)", r"\1", text)
    text = re.sub(r"`([^`]+)`", r"<font name='ReportBold'>\1</font>", text)
    text = text.replace("**", "")
    story.append(Paragraph(text, styles["Bodyx"]))

story.append(PageBreak()); story.append(Paragraph("Diagramas selecionados", styles["H1x"]))
for name in ("01-contexto.png", "02-containers.png", "03-componentes.png", "05-sequencia-trajeto.png", "08-deploy.png"):
    path = ROOT / "docs" / "diagramas" / name
    if path.exists():
        image = Image(str(path)); image._restrictSize(16*cm, 10*cm); story.extend([image, Spacer(1, 10)])

doc = SimpleDocTemplate(str(target), pagesize=A4, rightMargin=2*cm, leftMargin=2*cm, topMargin=1.8*cm, bottomMargin=1.8*cm, title="OinbuPebas — Relatório acadêmico", author="[preencher]")
doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(target)
