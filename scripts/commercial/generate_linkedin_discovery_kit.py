from __future__ import annotations

import shutil
import subprocess
from pathlib import Path

import imageio_ffmpeg
from PIL import Image, ImageDraw, ImageFont, ImageOps
from reportlab.lib.colors import HexColor
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas


ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "output" / "commercial" / "linkedin-discovery-kit"
TMP = ROOT / "tmp" / "commercial" / "linkedin-discovery-kit"
CERTIFIED_SCREENSHOT = (
    ROOT
    / "frontend"
    / "test-results"
    / "decision-review-keeps-AUDI-ca393--separates-unrealized-value-desktop"
    / "auditor-desktop.png"
)
PRODUCTION_SCREENSHOT = TMP / "workspace-production-auditor.png"

PDF_PATH = OUTPUT / "IMPERATOR_Discovery_Deck_ES.pdf"
SCREENSHOT_PATH = OUTPUT / "IMPERATOR_Workspace_Captura_Limpia.png"
COMMERCIAL_SCREENSHOT_PATH = OUTPUT / "IMPERATOR_Workspace_Captura_Comercial.png"
VIDEO_PATH = OUTPUT / "IMPERATOR_LinkedIn_72s_ES.mp4"

PAGE_W = 1080
PAGE_H = 1350
VIDEO_W = 1080
VIDEO_H = 1080
FPS = 30

INK = "#F2F5EF"
MUTED = "#96A29A"
TEAL = "#43DFA2"
GREEN = "#54E6A8"
RED = "#FF8B8B"
AMBER = "#D3F875"
PAPER = "#0B0F0E"
WHITE = "#F2F5EF"
MINT = "#123F33"
LINE = "#334640"
BLUE = "#71E7D2"
PANEL = "#161F1D"
PANEL_RAISED = "#1C2724"
ACCENT_DARK = "#123F33"


def ensure_directories() -> None:
    OUTPUT.mkdir(parents=True, exist_ok=True)
    TMP.mkdir(parents=True, exist_ok=True)


def fonts(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    path = Path("C:/Windows/Fonts/seguisb.ttf" if bold else "C:/Windows/Fonts/segoeui.ttf")
    return ImageFont.truetype(str(path), size=size)


def fit_text(text: str, font: ImageFont.FreeTypeFont, max_width: int) -> list[str]:
    words = text.split()
    lines: list[str] = []
    current = ""
    probe = ImageDraw.Draw(Image.new("RGB", (1, 1)))
    for word in words:
        candidate = f"{current} {word}".strip()
        if probe.textbbox((0, 0), candidate, font=font)[2] <= max_width:
            current = candidate
        else:
            if current:
                lines.append(current)
            current = word
    if current:
        lines.append(current)
    return lines


def draw_lines(
    draw: ImageDraw.ImageDraw,
    text: str,
    xy: tuple[int, int],
    font: ImageFont.FreeTypeFont,
    fill: str,
    max_width: int,
    gap: int = 8,
) -> int:
    x, y = xy
    line_height = font.size + gap
    for line in fit_text(text, font, max_width):
        draw.text((x, y), line, font=font, fill=fill)
        y += line_height
    return y


def clean_workspace_screenshot() -> Image.Image:
    source_path = PRODUCTION_SCREENSHOT if PRODUCTION_SCREENSHOT.exists() else CERTIFIED_SCREENSHOT
    if not source_path.exists():
        raise FileNotFoundError(f"Certified workspace screenshot not found: {source_path}")
    source = Image.open(source_path).convert("RGB")
    clean = Image.new("RGB", (source.width, source.height + 74), PAPER)
    clean.paste(source, (0, 0))
    draw = ImageDraw.Draw(clean)
    draw.rectangle((0, source.height, source.width, source.height + 74), fill=ACCENT_DARK)
    draw.text(
        (54, source.height + 22),
        "DEMO SINT\u00c9TICA / Sin datos ni resultados de clientes",
        font=fonts(24, True),
        fill=WHITE,
    )
    clean.save(SCREENSHOT_PATH, quality=95)
    return clean


def commercial_workspace_screenshot(clean: Image.Image) -> Image.Image:
    width = 1600
    height = 1600
    commercial = Image.new("RGB", (width, height), PAPER)
    draw = ImageDraw.Draw(commercial)

    draw.rounded_rectangle((72, 60, 136, 124), radius=8, fill=ACCENT_DARK, outline=TEAL, width=2)
    draw.text((94, 71), "I", font=fonts(30, True), fill=WHITE)
    draw.text((166, 58), "IMPERATOR", font=fonts(34, True), fill=WHITE)
    draw.text((166, 101), "Decision intelligence workspace", font=fonts(19), fill=MUTED)
    draw.rounded_rectangle((1180, 70, 1528, 118), radius=8, fill=ACCENT_DARK, outline=LINE, width=2)
    draw.text((1210, 82), "DEMO SINT\u00c9TICA / DATOS DE PRUEBA", font=fonts(17, True), fill=GREEN)

    draw.text((72, 180), "La evidencia antes de la decisi\u00f3n", font=fonts(58, True), fill=WHITE)
    draw.text(
        (74, 256),
        "Un caso ROI explicable, revisable y auditable.",
        font=fonts(28),
        fill=MUTED,
    )

    source_crop = clean.crop((0, 55, clean.width, min(1510, clean.height - 74)))
    source_crop.thumbnail((1456, 1050), Image.Resampling.LANCZOS)
    frame_x = (width - source_crop.width) // 2
    frame_y = 340
    draw.rounded_rectangle(
        (frame_x - 12, frame_y - 12, frame_x + source_crop.width + 12, frame_y + source_crop.height + 12),
        radius=12,
        fill=PANEL_RAISED,
        outline=LINE,
        width=3,
    )
    commercial.paste(source_crop, (frame_x, frame_y))

    metrics = [
        ("CASO", "DRC-AOA-001"),
        ("RECUPERACI\u00d3N ESTIMADA", "EUR 19.440/a\u00f1o"),
        ("CONFIANZA", "92%"),
        ("RESULTADO", "A\u00fan no realizado"),
    ]
    card_y = 1390
    card_w = 346
    gap = 24
    x = 72
    for label, value in metrics:
        draw.rounded_rectangle((x, card_y, x + card_w, 1530), radius=10, fill=PANEL, outline=LINE, width=2)
        draw.text((x + 22, card_y + 22), label, font=fonts(15, True), fill=MUTED)
        draw.text((x + 22, card_y + 67), value, font=fonts(23, True), fill=GREEN if label != "RESULTADO" else AMBER)
        x += card_w + gap

    commercial.save(COMMERCIAL_SCREENSHOT_PATH, quality=95)
    return commercial


def register_pdf_fonts() -> None:
    pdfmetrics.registerFont(TTFont("Segoe", "C:/Windows/Fonts/segoeui.ttf"))
    pdfmetrics.registerFont(TTFont("Segoe-Semibold", "C:/Windows/Fonts/seguisb.ttf"))


def pdf_text(
    c: canvas.Canvas,
    text: str,
    x: float,
    y: float,
    max_width: float,
    size: int,
    color: str = INK,
    bold: bool = False,
    leading: float | None = None,
) -> float:
    font_name = "Segoe-Semibold" if bold else "Segoe"
    leading = leading or size * 1.25
    words = text.split()
    lines: list[str] = []
    current = ""
    for word in words:
        candidate = f"{current} {word}".strip()
        if pdfmetrics.stringWidth(candidate, font_name, size) <= max_width:
            current = candidate
        else:
            if current:
                lines.append(current)
            current = word
    if current:
        lines.append(current)
    c.setFont(font_name, size)
    c.setFillColor(HexColor(color))
    for line in lines:
        c.drawString(x, y, line)
        y -= leading
    return y


def pdf_header(c: canvas.Canvas, page: int, label: str) -> None:
    c.setFillColor(HexColor(PAPER))
    c.rect(0, 0, PAGE_W, PAGE_H, fill=1, stroke=0)
    c.setFillColor(HexColor(ACCENT_DARK))
    c.roundRect(64, PAGE_H - 118, 46, 46, 5, fill=1, stroke=0)
    c.setFillColor(HexColor(WHITE))
    c.setFont("Segoe-Semibold", 22)
    c.drawCentredString(87, PAGE_H - 103, "I")
    c.setFillColor(HexColor(WHITE))
    c.setFont("Segoe-Semibold", 18)
    c.drawString(126, PAGE_H - 92, "IMPERATOR")
    c.setFont("Segoe", 10)
    c.setFillColor(HexColor(MUTED))
    c.drawString(126, PAGE_H - 108, label)
    c.drawRightString(PAGE_W - 64, PAGE_H - 96, f"{page}/5")


def pdf_footer(c: canvas.Canvas) -> None:
    c.setStrokeColor(HexColor(LINE))
    c.line(64, 62, PAGE_W - 64, 62)
    c.setFont("Segoe", 9)
    c.setFillColor(HexColor(MUTED))
    c.drawString(64, 40, "Demostraci\u00f3n sint\u00e9tica. No representa resultados de un cliente.")


def draw_flow(c: canvas.Canvas, labels: list[str], y: float) -> None:
    x = 64
    box_w = 122
    gap = 14
    for index, label in enumerate(labels):
        c.setFillColor(HexColor(PANEL_RAISED))
        c.setStrokeColor(HexColor(TEAL if index in {1, 3, 5} else LINE))
        c.setLineWidth(1.4)
        c.roundRect(x, y, box_w, 74, 6, fill=1, stroke=1)
        c.setFont("Segoe-Semibold", 12)
        c.setFillColor(HexColor(INK))
        c.drawCentredString(x + box_w / 2, y + 31, label)
        if index < len(labels) - 1:
            c.setStrokeColor(HexColor(MUTED))
            c.line(x + box_w + 3, y + 37, x + box_w + gap - 3, y + 37)
        x += box_w + gap


def create_pdf(commercial_screen: Image.Image) -> None:
    register_pdf_fonts()
    c = canvas.Canvas(str(PDF_PATH), pagesize=(PAGE_W, PAGE_H))
    c.setTitle("IMPERATOR - Descubrimiento y Decisi\u00f3n ROI")

    pdf_header(c, 1, "Decision intelligence")
    c.setFont("Segoe-Semibold", 15)
    c.setFillColor(HexColor(TEAL))
    c.drawString(64, 1140, "TFG / VALIDACION CON PROFESIONALES")
    y = pdf_text(
        c,
        "Las decisiones t\u00e9cnicas cambian. Su contexto se pierde.",
        64,
        1078,
        860,
        46,
        bold=True,
        leading=55,
    )
    y -= 22
    pdf_text(
        c,
        "Meses despu\u00e9s, explicar qui\u00e9n decidi\u00f3 qu\u00e9, por qu\u00e9, cu\u00e1nto cost\u00f3 y qu\u00e9 resultado produjo exige reconstrucci\u00f3n manual entre equipos y herramientas.",
        64,
        y,
        860,
        22,
        color=MUTED,
        leading=31,
    )
    c.setFillColor(HexColor(PANEL))
    c.setStrokeColor(HexColor(LINE))
    c.roundRect(64, 452, 952, 330, 7, fill=1, stroke=1)
    c.setFillColor(HexColor(INK))
    c.setFont("Segoe-Semibold", 24)
    c.drawString(96, 725, "La pregunta")
    pdf_text(
        c,
        "\u00bfPodemos demostrar por qu\u00e9 existe una decisi\u00f3n operativa, qu\u00e9 evidencia la respalda y qu\u00e9 valor econ\u00f3mico produjo?",
        96,
        670,
        835,
        31,
        bold=True,
        leading=42,
    )
    c.setFillColor(HexColor(ACCENT_DARK))
    c.roundRect(96, 488, 835, 76, 5, fill=1, stroke=0)
    c.setFillColor(HexColor(AMBER))
    c.setFont("Segoe-Semibold", 19)
    c.drawString(120, 519, "No otra plataforma de IA. Un sistema para gestionar decisiones.")
    pdf_footer(c)
    c.showPage()

    pdf_header(c, 2, "El vacio operativo")
    c.setFont("Segoe-Semibold", 38)
    c.setFillColor(HexColor(INK))
    c.drawString(64, 1110, "La evidencia existe, pero esta fragmentada")
    columns = [
        ("GitHub", "Qu\u00e9 cambi\u00f3 y qui\u00e9n lo implement\u00f3", BLUE),
        ("AWS", "Qu\u00e9 recursos y costes genera", AMBER),
        ("Decision", "Qui\u00e9n aprob\u00f3 y con qu\u00e9 motivo", TEAL),
        ("Finance", "Qu\u00e9 valor se estim\u00f3 y realiz\u00f3", GREEN),
    ]
    x = 64
    for title, body, color in columns:
        c.setFillColor(HexColor(PANEL))
        c.setStrokeColor(HexColor(LINE))
        c.roundRect(x, 735, 226, 245, 6, fill=1, stroke=1)
        c.setFillColor(HexColor(color))
        c.rect(x, 932, 226, 48, fill=1, stroke=0)
        c.setFillColor(HexColor(PAPER))
        c.setFont("Segoe-Semibold", 18)
        c.drawString(x + 18, 948, title)
        pdf_text(c, body, x + 18, 888, 188, 16, color=INK, leading=23)
        x += 242
    c.setFillColor(HexColor(INK))
    c.setFont("Segoe-Semibold", 25)
    c.drawString(64, 635, "El coste oculto")
    bullets = [
        "Reuniones para reconstruir el contexto.",
        "Coste cloud dif\u00edcil de atribuir a una decisi\u00f3n.",
        "Aprobaciones y responsables repartidos entre sistemas.",
        "Resultado esperado que nunca vuelve a medirse.",
    ]
    yy = 575
    for bullet in bullets:
        c.setFillColor(HexColor(TEAL))
        c.circle(76, yy + 6, 5, fill=1, stroke=0)
        pdf_text(c, bullet, 98, yy, 800, 19, color=INK)
        yy -= 100
    pdf_footer(c)
    c.showPage()

    pdf_header(c, 3, "Una Decisi\u00f3n. Una trazabilidad. Un valor.")
    c.setFont("Segoe-Semibold", 38)
    c.setFillColor(HexColor(INK))
    c.drawString(64, 1110, "IMPERATOR reconstruye el ciclo completo")
    draw_flow(c, ["Evidence", "Decision", "Recommendation", "ROI", "Approval", "Ledger", "Outcome"], 905)
    c.setFillColor(HexColor(PANEL))
    c.setStrokeColor(HexColor(LINE))
    c.roundRect(64, 420, 952, 380, 7, fill=1, stroke=1)
    c.setFont("Segoe-Semibold", 22)
    c.setFillColor(HexColor(TEAL))
    c.drawString(96, 745, "Caso sint\u00e9tico DRC-AOA-001")
    metrics = [
        ("Coste actual", "EUR 2.340/mes"),
        ("Coste proyectado", "EUR 720/mes"),
        ("Recuperaci\u00f3n estimada", "EUR 19.440/a\u00f1o"),
        ("Confianza / riesgo", "92% / LOW"),
    ]
    yy = 665
    for label, value in metrics:
        c.setFont("Segoe", 16)
        c.setFillColor(HexColor(MUTED))
        c.drawString(96, yy, label)
        c.setFont("Segoe-Semibold", 22)
        c.setFillColor(HexColor(INK))
        c.drawRightString(968, yy - 2, value)
        c.setStrokeColor(HexColor(LINE))
        c.line(96, yy - 18, 968, yy - 18)
        yy -= 72
    c.setFillColor(HexColor(ACCENT_DARK))
    c.roundRect(64, 260, 952, 100, 5, fill=1, stroke=0)
    c.setFillColor(HexColor(GREEN))
    c.setFont("Segoe-Semibold", 19)
    c.drawString(92, 315, "La IA puede explicar. No decide, no aprueba y no calcula el ROI.")
    pdf_footer(c)
    c.showPage()

    pdf_header(c, 4, "Decision Review Workspace")
    c.setFont("Segoe-Semibold", 36)
    c.setFillColor(HexColor(INK))
    c.drawString(64, 1112, "El revisor ve la evidencia antes de actuar")
    crop = commercial_screen
    preview = TMP / "workspace_pdf.png"
    crop.save(preview)
    c.drawImage(str(preview), 64, 210, width=952, height=820, preserveAspectRatio=True, anchor="c")
    c.setFillColor(HexColor(PANEL))
    c.setStrokeColor(HexColor(LINE))
    c.roundRect(64, 92, 952, 90, 5, fill=1, stroke=1)
    c.setFillColor(HexColor(INK))
    c.setFont("Segoe-Semibold", 15)
    c.drawString(90, 139, "Rol AUDITOR: solo lectura, evidencia confidencial redactada y valor no realizado separado.")
    c.setFont("Segoe", 11)
    c.setFillColor(HexColor(MUTED))
    c.drawString(90, 116, "Captura real del workspace certificado con datos sint\u00e9ticos de prueba.")
    pdf_footer(c)
    c.showPage()

    pdf_header(c, 5, "Busco entrevistas, no acceso a sistemas")
    c.setFont("Segoe-Semibold", 40)
    c.setFillColor(HexColor(INK))
    c.drawString(64, 1098, "Quiero contrastar el problema con profesionales")
    pdf_text(
        c,
        "Estoy realizando mi TFG de DAM y busco conversaciones de 15-20 minutos con CTOs, Platform, DevOps, FinOps y Security.",
        64,
        1022,
        900,
        23,
        color=MUTED,
        leading=32,
    )
    items = [
        ("No venta", "La primera conversaci\u00f3n es discovery."),
        ("No acceso", "No necesito credenciales ni conectar sistemas."),
        ("No datos", "No necesito informaci\u00f3n confidencial."),
        ("Un caso", "Quiero entender una decisi\u00f3n reciente y medible."),
    ]
    yy = 790
    for title, body in items:
        c.setFillColor(HexColor(PANEL))
        c.setStrokeColor(HexColor(LINE))
        c.roundRect(64, yy, 952, 112, 6, fill=1, stroke=1)
        c.setFont("Segoe-Semibold", 20)
        c.setFillColor(HexColor(TEAL))
        c.drawString(92, yy + 70, title)
        c.setFont("Segoe", 17)
        c.setFillColor(HexColor(INK))
        c.drawString(270, yy + 70, body)
        yy -= 132
    c.setFillColor(HexColor(ACCENT_DARK))
    c.roundRect(64, 176, 952, 130, 7, fill=1, stroke=0)
    c.setFillColor(HexColor(WHITE))
    c.setFont("Segoe-Semibold", 25)
    c.drawString(96, 247, "\u00bfMe dedicar\u00edas 20 minutos para contarme c\u00f3mo lo hac\u00e9is hoy?")
    c.setFont("Segoe", 15)
    c.drawString(96, 214, "Busco ideas, problemas reales y feedback profesional para validar IMPERATOR.")
    pdf_footer(c)
    c.save()


def video_base() -> Image.Image:
    return Image.new("RGB", (VIDEO_W, VIDEO_H), PAPER)


def video_brand(draw: ImageDraw.ImageDraw, section: str, index: int) -> None:
    draw.rounded_rectangle((64, 48, 114, 98), radius=6, fill=ACCENT_DARK, outline=TEAL, width=2)
    draw.text((83, 57), "I", font=fonts(24, True), fill=WHITE)
    draw.text((132, 50), "IMPERATOR", font=fonts(24, True), fill=INK)
    draw.text((132, 80), section, font=fonts(14), fill=MUTED)
    draw.text((962, 58), f"0{index}", font=fonts(18, True), fill=TEAL)


def slide_text(index: int, eyebrow: str, title: str, body: str, accent: str = TEAL) -> Image.Image:
    im = video_base()
    draw = ImageDraw.Draw(im)
    video_brand(draw, "Decision intelligence", index)
    draw.text((72, 202), eyebrow.upper(), font=fonts(18, True), fill=accent)
    y = draw_lines(draw, title, (72, 252), fonts(49, True), INK, 900, 10)
    draw_lines(draw, body, (72, y + 42), fonts(26), MUTED, 890, 14)
    draw.rectangle((72, 922, 1008, 924), fill=LINE)
    draw.text((72, 955), "TFG DAM / Validaci\u00f3n con profesionales", font=fonts(18), fill=MUTED)
    return im


def slide_flow() -> Image.Image:
    im = video_base()
    draw = ImageDraw.Draw(im)
    video_brand(draw, "Una Decisi\u00f3n. Un ROI.", 3)
    draw.text((72, 184), "IMPERATOR RECONSTRUYE EL CICLO", font=fonts(18, True), fill=TEAL)
    draw.text((72, 230), "De evidencia a valor verificable", font=fonts(46, True), fill=INK)
    labels = ["Evidence", "Decision", "Recommendation", "ROI", "Approval", "Ledger", "Outcome"]
    y = 420
    x = 70
    for pos, label in enumerate(labels):
        width = 128
        draw.rounded_rectangle((x, y, x + width, y + 82), radius=8, fill=PANEL_RAISED, outline=TEAL if pos % 2 else LINE, width=3)
        label_font = fonts(14 if label == "Recommendation" else 17, True)
        bbox = draw.textbbox((0, 0), label, font=label_font)
        draw.text((x + (width - (bbox[2] - bbox[0])) / 2, y + 29), label, font=label_font, fill=INK)
        if pos < len(labels) - 1:
            draw.line((x + width, y + 41, x + width + 13, y + 41), fill=MUTED, width=3)
        x += width + 13
    draw.rounded_rectangle((72, 620, 1008, 820), radius=8, fill=ACCENT_DARK, outline=LINE, width=2)
    draw.text((102, 660), "IA explicativa", font=fonts(22, True), fill=GREEN)
    draw_lines(draw, "No decide. No aprueba. No calcula el ROI.", (102, 706), fonts(31, True), INK, 820, 10)
    return im


def slide_metrics() -> Image.Image:
    im = video_base()
    draw = ImageDraw.Draw(im)
    video_brand(draw, "Caso sint\u00e9tico DRC-AOA-001", 4)
    draw.text((72, 178), "UN RESULTADO ENTENDIBLE", font=fonts(18, True), fill=TEAL)
    draw.text((72, 226), "Decisi\u00f3n ROI en una sola vista", font=fonts(46, True), fill=INK)
    metrics = [
        ("Coste actual", "EUR 2.340/mes", MUTED),
        ("Coste proyectado", "EUR 720/mes", BLUE),
        ("Recuperaci\u00f3n estimada", "EUR 19.440/a\u00f1o", TEAL),
        ("Confianza / riesgo", "92% / LOW", GREEN),
    ]
    y = 350
    for label, value, color in metrics:
        draw.rounded_rectangle((72, y, 1008, y + 112), radius=8, fill=PANEL_RAISED, outline=LINE, width=2)
        draw.text((102, y + 25), label, font=fonts(20), fill=MUTED)
        bbox = draw.textbbox((0, 0), value, font=fonts(27, True))
        draw.text((970 - (bbox[2] - bbox[0]), y + 36), value, font=fonts(27, True), fill=color)
        y += 130
    draw.text((72, 905), "Valores can\u00f3nicos. No son resultados de un cliente.", font=fonts(18), fill=RED)
    return im


def slide_workspace(commercial_screen: Image.Image) -> Image.Image:
    im = video_base()
    draw = ImageDraw.Draw(im)
    video_brand(draw, "Decision Review Workspace", 5)
    draw.text((72, 145), "EVIDENCIA ANTES DE ACCI\u00d3N", font=fonts(18, True), fill=TEAL)
    draw.text((72, 186), "Una decisi\u00f3n auditable", font=fonts(42, True), fill=INK)
    crop = commercial_screen.copy()
    crop.thumbnail((800, 730), Image.Resampling.LANCZOS)
    frame = Image.new("RGB", (crop.width + 18, crop.height + 18), PANEL_RAISED)
    frame.paste(crop, (9, 9))
    im.paste(frame, ((VIDEO_W - frame.width) // 2, 285))
    draw.rounded_rectangle((72, 936, 1008, 1018), radius=8, fill=ACCENT_DARK, outline=LINE, width=2)
    draw.text((102, 962), "Rol AUDITOR / Solo lectura / Evidencia redactada", font=fonts(21, True), fill=WHITE)
    return im


def slide_invitation() -> Image.Image:
    im = video_base()
    draw = ImageDraw.Draw(im)
    video_brand(draw, "Discovery", 6)
    draw.text((72, 190), "NO BUSCO VENDERTE UNA PLATAFORMA", font=fonts(18, True), fill=TEAL)
    y = draw_lines(draw, "Busco 15-20 minutos para entender c\u00f3mo lo hac\u00e9is hoy.", (72, 250), fonts(47, True), INK, 900, 11)
    points = ["Sin acceso a sistemas", "Sin datos confidenciales", "Un caso operativo reciente", "Feedback profesional para mi TFG"]
    y += 55
    for point in points:
        draw.ellipse((76, y + 11, 90, y + 25), fill=GREEN)
        draw.text((114, y), point, font=fonts(27), fill=INK)
        y += 80
    draw.rounded_rectangle((72, 870, 1008, 980), radius=8, fill=ACCENT_DARK, outline=TEAL, width=2)
    draw.text((102, 907), "\u00bfTe interesar\u00eda participar en una breve entrevista?", font=fonts(28, True), fill=WHITE)
    return im


def create_video(commercial_screen: Image.Image) -> None:
    slides = [
        (slide_text(1, "El problema", "Las decisiones cambian. El contexto se pierde.", "Meses despu\u00e9s, reconstruir por qu\u00e9 se decidi\u00f3 algo exige unir personas, tickets, c\u00f3digo, costes y aprobaciones."), 11),
        (slide_text(2, "La consecuencia", "Sabemos cu\u00e1nto gastamos. No siempre sabemos por qu\u00e9.", "GitHub muestra cambios. AWS muestra costes. Pero la trazabilidad de la decisi\u00f3n suele quedar repartida."), 11),
        (slide_flow(), 12),
        (slide_metrics(), 13),
        (slide_workspace(commercial_screen), 13),
        (slide_invitation(), 12),
    ]
    ffmpeg = Path(imageio_ffmpeg.get_ffmpeg_exe())
    chunks: list[Path] = []
    for idx, (frame, duration) in enumerate(slides, start=1):
        image_path = TMP / f"video_slide_{idx:02d}.png"
        clip_path = TMP / f"video_clip_{idx:02d}.mp4"
        frame.save(image_path)
        subprocess.run(
            [
                str(ffmpeg), "-y", "-loop", "1", "-i", str(image_path),
                "-t", str(duration), "-r", str(FPS),
                "-vf", f"fade=t=in:st=0:d=0.3,fade=t=out:st={duration - 0.3}:d=0.3,format=yuv420p",
                "-c:v", "libx264", "-preset", "medium", "-crf", "20",
                "-movflags", "+faststart", str(clip_path),
            ],
            check=True,
            capture_output=True,
        )
        chunks.append(clip_path)
    concat_path = TMP / "video_concat.txt"
    concat_path.write_text("\n".join(f"file '{p.as_posix()}'" for p in chunks), encoding="utf-8")
    subprocess.run(
        [
            str(ffmpeg), "-y", "-f", "concat", "-safe", "0", "-i", str(concat_path),
            "-c", "copy", "-movflags", "+faststart", str(VIDEO_PATH),
        ],
        check=True,
        capture_output=True,
    )


def main() -> None:
    ensure_directories()
    clean_screen = clean_workspace_screenshot()
    commercial_screen = commercial_workspace_screenshot(clean_screen)
    create_pdf(commercial_screen)
    create_video(commercial_screen)
    print(PDF_PATH)
    print(SCREENSHOT_PATH)
    print(COMMERCIAL_SCREENSHOT_PATH)
    print(VIDEO_PATH)


if __name__ == "__main__":
    main()
