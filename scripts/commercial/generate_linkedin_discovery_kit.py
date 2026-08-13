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


def create_legacy_pdf(commercial_screen: Image.Image) -> None:
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


DASHBOARD_OUTPUTS = [
    OUTPUT / "IMPERATOR_Comercial_01_Executive_Workspace.png",
    OUTPUT / "IMPERATOR_Comercial_02_Decision_Detail.png",
    OUTPUT / "IMPERATOR_Comercial_03_Decision_Ledger.png",
    OUTPUT / "IMPERATOR_Comercial_04_Business_Value.png",
    OUTPUT / "IMPERATOR_Comercial_05_Discovery.png",
]


def dashboard_card(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    *,
    fill: str = PANEL,
    outline: str = LINE,
    radius: int = 18,
    accent: bool = False,
) -> None:
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=2)
    if accent:
        x1, y1, x2, _ = box
        draw.rounded_rectangle((x1 + 2, y1 + 2, x2 - 2, y1 + 8), radius=4, fill=TEAL)


def dashboard_label(draw: ImageDraw.ImageDraw, xy: tuple[int, int], value: str, color: str = MUTED) -> None:
    draw.text(xy, value.upper(), font=fonts(13, True), fill=color)


def dashboard_nav(draw: ImageDraw.ImageDraw, active: str) -> None:
    labels = [
        ("Executive Workspace", 153),
        ("Decisions", 102),
        ("Decision Ledger", 132),
        ("Business Value", 124),
        ("Integrations", 112),
    ]
    draw.rounded_rectangle((28, 22, 1052, 86), radius=32, fill="#0D1513", outline=LINE, width=2)
    x = 42
    for label, width in labels:
        if label == active:
            draw.rounded_rectangle((x, 32, x + width, 76), radius=14, fill=ACCENT_DARK, outline=TEAL, width=2)
            color = WHITE
        else:
            color = MUTED
        bbox = draw.textbbox((0, 0), label, font=fonts(13, True))
        draw.text((x + (width - (bbox[2] - bbox[0])) / 2, 45), label, font=fonts(13, True), fill=color)
        x += width + 10
    draw.rounded_rectangle((930, 34, 1038, 74), radius=13, fill=ACCENT_DARK, outline=LINE, width=1)
    draw.text((947, 46), "SYNTHETIC", font=fonts(12, True), fill=GREEN)


def dashboard_base(active: str, eyebrow: str, title: str, subtitle: str) -> tuple[Image.Image, ImageDraw.ImageDraw]:
    image = Image.new("RGB", (PAGE_W, PAGE_H), PAPER)
    draw = ImageDraw.Draw(image)
    dashboard_nav(draw, active)
    dashboard_label(draw, (32, 111), eyebrow)
    title_y = 145
    title_end = draw_lines(draw, title, (32, title_y), fonts(47, True), WHITE, 1000, 5)
    draw_lines(draw, subtitle, (32, title_end + 8), fonts(18), MUTED, 990, 7)
    return image, draw


def metric_card(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    label: str,
    value: str,
    *,
    value_color: str = WHITE,
) -> None:
    dashboard_card(draw, box, fill="#101917", radius=16)
    x1, y1, _, _ = box
    dashboard_label(draw, (x1 + 18, y1 + 18), label)
    draw.text((x1 + 18, y1 + 59), value, font=fonts(25, True), fill=value_color)


def draw_kv_rows(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    rows: list[tuple[str, str]],
    *,
    title: str | None = None,
) -> None:
    dashboard_card(draw, box, fill="#101917", radius=16)
    x1, y1, x2, _ = box
    y = y1 + 22
    if title:
        dashboard_label(draw, (x1 + 18, y), title)
        y += 38
    row_height = 52
    for index, (label, value) in enumerate(rows):
        dashboard_label(draw, (x1 + 18, y + 7), label)
        value_box = draw.textbbox((0, 0), value, font=fonts(17, True))
        draw.text((x2 - 18 - (value_box[2] - value_box[0]), y + 4), value, font=fonts(17, True), fill=WHITE)
        if index < len(rows) - 1:
            draw.line((x1 + 18, y + 39, x2 - 18, y + 39), fill=LINE, width=1)
        y += row_height


def executive_workspace_surface() -> Image.Image:
    image, draw = dashboard_base(
        "Executive Workspace",
        "IMPERATOR / DECISION ENGINE",
        "Executive Decision Workspace",
        "Una sola decisi\u00f3n. Su evidencia, impacto, autoridad humana y estado econ\u00f3mico.",
    )
    dashboard_card(draw, (32, 260, 630, 665), fill="#14211D", accent=True)
    dashboard_label(draw, (62, 298), "Executive summary")
    draw.text((62, 350), "Estimated annual recovery", font=fonts(23, True), fill=MUTED)
    draw.text((62, 407), "EUR 19.440", font=fonts(78, True), fill=WHITE)
    draw.rounded_rectangle((62, 505, 230, 544), radius=12, fill=ACCENT_DARK, outline=TEAL, width=1)
    draw.text((80, 516), "92% confidence", font=fonts(15, True), fill=GREEN)
    mini = [
        ("CURRENT", "EUR 2.340/mo"),
        ("PROJECTED", "EUR 720/mo"),
        ("RISK", "LOW"),
    ]
    x = 62
    for label, value in mini:
        dashboard_card(draw, (x, 570, x + 166, 640), fill="#0F1816", radius=12)
        dashboard_label(draw, (x + 12, 584), label)
        draw.text((x + 12, 611), value, font=fonts(15, True), fill=WHITE)
        x += 178

    dashboard_card(draw, (650, 260, 1048, 665), fill="#101917", outline=TEAL)
    dashboard_label(draw, (678, 294), "Decision to review today")
    draw_lines(draw, "Recover AI onboarding assistant spend", (678, 345), fonts(31, True), WHITE, 340, 5)
    facts = [
        ("ANNUAL RECOVERY", "EUR 19.440"),
        ("RISK", "LOW"),
        ("CONFIDENCE", "92%"),
        ("STATUS", "APPROVED"),
    ]
    for idx, (label, value) in enumerate(facts):
        col = idx % 2
        row = idx // 2
        x1 = 678 + col * 174
        y1 = 455 + row * 82
        dashboard_card(draw, (x1, y1, x1 + 160, y1 + 70), fill="#0B1311", radius=12)
        dashboard_label(draw, (x1 + 12, y1 + 12), label)
        draw.text((x1 + 12, y1 + 37), value, font=fonts(17, True), fill=GREEN if label != "STATUS" else AMBER)
    draw.rounded_rectangle((678, 615, 1020, 649), radius=11, fill=GREEN)
    cta = "Review decision"
    cta_box = draw.textbbox((0, 0), cta, font=fonts(16, True))
    draw.text((849 - (cta_box[2] - cta_box[0]) / 2, 624), cta, font=fonts(16, True), fill="#062018")

    metrics = [
        ("EVIDENCE", "30 records", GREEN),
        ("SOURCES", "GitHub + AWS", BLUE),
        ("POLICY", "DRC-AOA-001-v1", GREEN),
        ("REALIZED VALUE", "Not yet realized", AMBER),
    ]
    x = 32
    for label, value, color in metrics:
        metric_card(draw, (x, 688, x + 246, 808), label, value, value_color=color)
        x += 257

    dashboard_card(draw, (32, 832, 1048, 1270), fill="#101917")
    dashboard_label(draw, (54, 856), "Why this decision matters")
    points = [
        "AI consumption is attributable to one operational case.",
        "The recommendation is deterministic and preserves a high-cost fallback.",
        "A human reviewer remains the only approval authority.",
        "Realized value stays unavailable until Finance validates the outcome.",
    ]
    y = 905
    for point in points:
        draw.ellipse((58, y + 8, 68, y + 18), fill=TEAL)
        draw.text((84, y), point, font=fonts(19), fill=WHITE)
        y += 62
    dashboard_card(draw, (724, 1058, 1016, 1238), fill="#0C201A", outline=TEAL)
    dashboard_label(draw, (750, 1083), "Confidence")
    draw.text((750, 1122), "92%", font=fonts(45, True), fill=GREEN)
    draw_lines(draw, "Evidence complete. Risk LOW.", (750, 1180), fonts(16), MUTED, 235, 4)
    return image


def decision_detail_surface() -> Image.Image:
    image, draw = dashboard_base(
        "Decisions",
        "IMPERATOR / DECISION DETAIL",
        "AI Onboarding Assistant",
        "Toda la evidencia antes de actuar. IMPERATOR recomienda; la empresa decide.",
    )
    dashboard_card(draw, (32, 240, 1048, 378), fill="#101917")
    dashboard_label(draw, (54, 264), "Decision list")
    draw.rounded_rectangle((54, 304, 1026, 354), radius=12, fill=ACCENT_DARK, outline=TEAL, width=2)
    draw.text((72, 319), "DRC-AOA-001 / AI model change", font=fonts(17, True), fill=WHITE)
    impact = "+EUR 19.440 estimated"
    impact_box = draw.textbbox((0, 0), impact, font=fonts(17, True))
    draw.text((1008 - (impact_box[2] - impact_box[0]), 319), impact, font=fonts(17, True), fill=GREEN)

    dashboard_card(draw, (32, 400, 640, 650), fill="#101917", outline=TEAL)
    dashboard_label(draw, (58, 428), "Evidence review")
    draw.text((58, 475), "Can we trust this recommendation?", font=fonts(28, True), fill=WHITE)
    evidence_facts = [("BASED ON", "30 Evidence"), ("POLICY", "Deterministic"), ("CONFIDENCE", "92%"), ("RISK", "LOW")]
    for idx, (label, value) in enumerate(evidence_facts):
        x1 = 58 + (idx % 2) * 278
        y1 = 530 + (idx // 2) * 74
        dashboard_card(draw, (x1, y1, x1 + 262, y1 + 62), fill="#0B1311", radius=11)
        dashboard_label(draw, (x1 + 12, y1 + 10), label)
        draw.text((x1 + 12, y1 + 34), value, font=fonts(17, True), fill=GREEN)
    draw_kv_rows(
        draw,
        (660, 400, 1048, 650),
        [
            ("Current monthly", "EUR 2.340"),
            ("Projected monthly", "EUR 720"),
            ("Monthly recovery", "EUR 1.620"),
            ("Annual recovery", "EUR 19.440"),
        ],
        title="Financial impact",
    )

    cards = [
        ("EVIDENCE SOURCES", [("GitHub", "Read-only"), ("AWS", "Read-only")]),
        ("RECOMMENDATION", [("Type", "MODEL_CHANGE"), ("Fallback", "Preserved")]),
        ("OWNERSHIP", [("Approver", "Human"), ("Audit role", "Read-only")]),
    ]
    x = 32
    for title, rows in cards:
        draw_kv_rows(draw, (x, 675, x + 327, 875), rows, title=title)
        x += 344

    dashboard_card(draw, (32, 900, 1048, 1088), fill="#101917")
    dashboard_label(draw, (54, 925), "Decision")
    draw.text((54, 966), "Recover AI onboarding assistant spend", font=fonts(25, True), fill=WHITE)
    state = [
        ("CASE", "DRC-AOA-001"),
        ("STATUS", "APPROVED"),
        ("POLICY", "DRC-AOA-001-v1"),
        ("VALUE", "ESTIMATED"),
    ]
    x = 54
    for label, value in state:
        dashboard_card(draw, (x, 1015, x + 228, 1068), fill="#0B1311", radius=10)
        dashboard_label(draw, (x + 10, 1025), label)
        value_box = draw.textbbox((0, 0), value, font=fonts(14, True))
        draw.text((x + 218 - (value_box[2] - value_box[0]), 1026), value, font=fonts(14, True), fill=GREEN if value != "ESTIMATED" else AMBER)
        x += 242

    dashboard_card(draw, (32, 1112, 1048, 1286), fill=ACCENT_DARK, outline=TEAL)
    dashboard_label(draw, (54, 1138), "Human authority")
    draw.text((54, 1175), "Review the evidence before approving the recommendation.", font=fonts(23, True), fill=WHITE)
    draw.text((54, 1216), "La IA puede explicar. No decide, no aprueba y no calcula el ROI.", font=fonts(17), fill=MUTED)
    draw.rounded_rectangle((790, 1170, 1020, 1234), radius=14, fill=GREEN)
    draw.text((826, 1191), "Review decision", font=fonts(17, True), fill="#062018")
    return image


def decision_ledger_surface() -> Image.Image:
    image, draw = dashboard_base(
        "Decision Ledger",
        "IMPERATOR / IMMUTABLE RECORD",
        "Decision Ledger",
        "La historia auditable de la decisi\u00f3n: qu\u00e9 ocurri\u00f3, qui\u00e9n actu\u00f3, cu\u00e1ndo y por qu\u00e9.",
    )
    dashboard_card(draw, (32, 252, 1048, 520), fill="#101917")
    dashboard_label(draw, (54, 278), "Current ledger")
    dashboard_label(draw, (54, 326), "DATE")
    dashboard_label(draw, (180, 326), "FACT")
    dashboard_label(draw, (515, 326), "ACTOR")
    dashboard_label(draw, (690, 326), "STATE")
    dashboard_label(draw, (865, 326), "VALUE")
    draw.rounded_rectangle((52, 356, 1028, 426), radius=12, fill="#17231F", outline=LINE, width=1)
    draw.text((68, 380), "2 AUG", font=fonts(16, True), fill=MUTED)
    draw.text((180, 380), "Decision approved", font=fonts(18, True), fill=WHITE)
    draw.text((515, 380), "ADMIN", font=fonts(16, True), fill=MUTED)
    draw.text((690, 380), "APPROVED", font=fonts(16, True), fill=GREEN)
    draw.text((865, 380), "ESTIMATED", font=fonts(16, True), fill=AMBER)
    draw.text((54, 458), "No implementation or result-validation fact is claimed in the current commercial view.", font=fonts(16), fill=MUTED)

    dashboard_card(draw, (32, 546, 1048, 785), fill="#101917")
    dashboard_label(draw, (54, 572), "Decision lifecycle")
    lifecycle = [
        ("01", "Evidence", "COMPLETE", GREEN),
        ("02", "Recommendation", "COMPLETE", GREEN),
        ("03", "Approval", "RECORDED", GREEN),
        ("04", "Implementation", "PENDING", MUTED),
        ("05", "Result", "PENDING", MUTED),
    ]
    x = 54
    for number, label, status, color in lifecycle:
        dashboard_card(draw, (x, 620, x + 182, 750), fill="#0B1311", outline=TEAL if status != "PENDING" else LINE, radius=13)
        draw.text((x + 16, 638), number, font=fonts(14, True), fill=color)
        draw_lines(draw, label, (x + 16, 674), fonts(17, True), WHITE, 150, 3)
        draw.text((x + 16, 720), status, font=fonts(12, True), fill=color)
        x += 194

    dashboard_card(draw, (32, 812, 1048, 1065), fill="#101917")
    dashboard_label(draw, (54, 838), "Ledger guarantees")
    guarantees = [
        ("APPEND ONLY", "No update or delete"),
        ("ORDERED", "Timestamp ascending"),
        ("TRACEABLE", "Decision to Evidence"),
        ("HUMAN", "Actor and reason"),
    ]
    x = 54
    for label, value in guarantees:
        dashboard_card(draw, (x, 888, x + 226, 1028), fill="#0B1311", radius=12)
        dashboard_label(draw, (x + 16, 910), label, GREEN)
        draw_lines(draw, value, (x + 16, 954), fonts(18, True), WHITE, 190, 4)
        x += 242

    dashboard_card(draw, (32, 1092, 1048, 1284), fill="#0C201A", outline=TEAL)
    dashboard_label(draw, (54, 1118), "The business rule")
    draw.text((54, 1163), "Si una entrada es incorrecta, se a\u00f1ade otra.", font=fonts(26, True), fill=WHITE)
    draw.text((54, 1210), "La historia anterior nunca se sobrescribe.", font=fonts(19), fill=MUTED)
    return image


def business_value_surface() -> Image.Image:
    image, draw = dashboard_base(
        "Business Value",
        "IMPERATOR / BUSINESS VALUE",
        "What value could this Decision recover?",
        "La estimaci\u00f3n se mantiene separada del valor realizado hasta que Finance valida el resultado.",
    )
    dashboard_card(draw, (32, 270, 650, 635), fill="#14211D", accent=True)
    dashboard_label(draw, (62, 302), "Estimated annual recovery")
    draw.text((62, 370), "EUR 19.440", font=fonts(80, True), fill=WHITE)
    draw.rounded_rectangle((62, 497, 268, 538), radius=12, fill=ACCENT_DARK, outline=TEAL, width=1)
    draw.text((82, 508), "Deterministic ROI", font=fonts(15, True), fill=GREEN)
    draw.text((62, 574), "Canonical scenario / not a customer result", font=fonts(16), fill=MUTED)

    draw_kv_rows(
        draw,
        (672, 270, 1048, 635),
        [
            ("Current annual cost", "EUR 28.080"),
            ("Projected annual cost", "EUR 8.640"),
            ("Estimated recovery", "EUR 19.440"),
            ("Realized value", "Unavailable"),
        ],
        title="ROI of this decision",
    )

    dashboard_card(draw, (32, 662, 1048, 862), fill="#101917")
    dashboard_label(draw, (54, 690), "Value states")
    states = [
        ("01", "ESTIMATED", "Recommendation + ROI", GREEN),
        ("02", "IMPLEMENTED", "Ledger fact required", MUTED),
        ("03", "VALIDATED", "Finance evidence required", MUTED),
        ("04", "REALIZED", "Only after validation", MUTED),
    ]
    x = 54
    for number, state, condition, color in states:
        dashboard_card(draw, (x, 735, x + 226, 832), fill="#0B1311", outline=TEAL if state == "ESTIMATED" else LINE, radius=12)
        draw.text((x + 14, 750), number, font=fonts(13, True), fill=color)
        draw.text((x + 14, 776), state, font=fonts(16, True), fill=color)
        draw.text((x + 14, 805), condition, font=fonts(12), fill=MUTED)
        x += 242

    dashboard_card(draw, (32, 890, 1048, 1110), fill="#101917")
    dashboard_label(draw, (54, 918), "Why the separation matters")
    bullets = [
        "Forecasts remain forecasts until evidence proves the outcome.",
        "Approval does not automatically become realized business value.",
        "The Ledger preserves the exact path from estimate to validation.",
    ]
    y = 965
    for bullet in bullets:
        draw.ellipse((58, y + 8, 68, y + 18), fill=TEAL)
        draw.text((84, y), bullet, font=fonts(18), fill=WHITE)
        y += 52

    dashboard_card(draw, (32, 1138, 1048, 1288), fill=ACCENT_DARK, outline=TEAL)
    dashboard_label(draw, (54, 1162), "Current commercial claim")
    draw.text((54, 1202), "EUR 19.440 estimated / realized value not yet available", font=fonts(25, True), fill=WHITE)
    draw.text((54, 1247), "Nunca se presenta como ahorro de un cliente.", font=fonts(16), fill=MUTED)
    return image


def discovery_surface() -> Image.Image:
    image, draw = dashboard_base(
        "Executive Workspace",
        "IMPERATOR / DISCOVERY",
        "\u00bfC\u00f3mo reconstru\u00eds hoy una decisi\u00f3n operativa?",
        "Busco conversaciones de 15-20 minutos para contrastar el problema, no para vender una plataforma.",
    )
    prompts = [
        ("01", "DECISION", "\u00bfPod\u00e9is explicar meses despu\u00e9s por qu\u00e9 se tom\u00f3?"),
        ("02", "OWNERSHIP", "\u00bfQui\u00e9n aprueba cuando existe impacto econ\u00f3mico?"),
        ("03", "EVIDENCE", "\u00bfQu\u00e9 sistemas contienen la prueba y cu\u00e1nto cuesta reunirla?"),
        ("04", "OUTCOME", "\u00bfComprob\u00e1is despu\u00e9s si el ahorro esperado se realiz\u00f3?"),
    ]
    y = 285
    for number, label, question in prompts:
        dashboard_card(draw, (32, y, 1048, y + 150), fill="#101917")
        draw.rounded_rectangle((54, y + 36, 114, y + 96), radius=14, fill=ACCENT_DARK, outline=TEAL, width=2)
        draw.text((72, y + 54), number, font=fonts(18, True), fill=GREEN)
        dashboard_label(draw, (142, y + 32), label, GREEN)
        draw_lines(draw, question, (142, y + 65), fonts(22, True), WHITE, 850, 5)
        y += 168

    dashboard_card(draw, (32, 973, 1048, 1135), fill="#0C201A", outline=TEAL)
    dashboard_label(draw, (54, 1000), "What I need")
    draw.text((54, 1042), "Un caso real contado sin credenciales ni datos confidenciales.", font=fonts(25, True), fill=WHITE)
    draw.text((54, 1085), "Las conclusiones se utilizar\u00e1n de forma anonimizada para el TFG.", font=fonts(17), fill=MUTED)

    draw.rounded_rectangle((32, 1165, 1048, 1288), radius=18, fill=GREEN)
    draw.text((58, 1192), "\u00bfMe dedicar\u00edas 20 minutos para entender c\u00f3mo lo hac\u00e9is hoy?", font=fonts(26, True), fill="#062018")
    draw.text((58, 1243), "Mensaje directo o comentario en LinkedIn.", font=fonts(17, True), fill="#123F33")
    return image


def create_commercial_surfaces() -> list[Image.Image]:
    surfaces = [
        executive_workspace_surface(),
        decision_detail_surface(),
        decision_ledger_surface(),
        business_value_surface(),
        discovery_surface(),
    ]
    for surface, path in zip(surfaces, DASHBOARD_OUTPUTS, strict=True):
        surface.save(path, quality=95)
    surfaces[0].save(COMMERCIAL_SCREENSHOT_PATH, quality=95)
    return surfaces


def create_pdf(surfaces: list[Image.Image]) -> None:
    register_pdf_fonts()
    c = canvas.Canvas(str(PDF_PATH), pagesize=(PAGE_W, PAGE_H))
    c.setTitle("IMPERATOR - Executive Decision Discovery")
    for index, surface in enumerate(surfaces, start=1):
        page_path = TMP / f"dashboard_pdf_page_{index}.png"
        surface.save(page_path)
        c.drawImage(str(page_path), 0, 0, width=PAGE_W, height=PAGE_H)
        c.showPage()
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
    commercial_workspace_screenshot(clean_screen)
    surfaces = create_commercial_surfaces()
    create_pdf(surfaces)
    create_video(surfaces[1])
    print(PDF_PATH)
    print(SCREENSHOT_PATH)
    print(COMMERCIAL_SCREENSHOT_PATH)
    print(VIDEO_PATH)


if __name__ == "__main__":
    main()
