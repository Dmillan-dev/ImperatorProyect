from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from reportlab.lib.colors import Color, HexColor
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfbase import pdfmetrics
from reportlab.platypus import (
    Flowable,
    KeepTogether,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[2]
OUTPUT_DIR = ROOT / "output" / "pdf"
OUTPUT_FILE = OUTPUT_DIR / "IMPERATOR_Guia_Entrevistas_Discovery_ES.pdf"

PAGE_W, PAGE_H = A4
MARGIN_X = 15 * mm
MARGIN_TOP = 16 * mm
MARGIN_BOTTOM = 14 * mm
CONTENT_W = PAGE_W - 2 * MARGIN_X

INK = HexColor("#071211")
PANEL = HexColor("#101C19")
PANEL_2 = HexColor("#15241F")
LINE = HexColor("#294038")
TEXT = HexColor("#F5F2E8")
MUTED = HexColor("#AEBDB4")
GREEN = HexColor("#49E5B2")
LIME = HexColor("#B9F56B")
TEAL = HexColor("#123F36")
AMBER = HexColor("#F0C86A")
RED = HexColor("#FF857D")
WHITE = HexColor("#FFFFFF")


def register_fonts() -> tuple[str, str]:
    regular_candidates = [
        Path("C:/Windows/Fonts/arial.ttf"),
        Path("C:/Windows/Fonts/calibri.ttf"),
    ]
    bold_candidates = [
        Path("C:/Windows/Fonts/arialbd.ttf"),
        Path("C:/Windows/Fonts/calibrib.ttf"),
    ]
    regular = next((path for path in regular_candidates if path.exists()), None)
    bold = next((path for path in bold_candidates if path.exists()), None)
    if regular and bold:
        pdfmetrics.registerFont(TTFont("ImperatorRegular", str(regular)))
        pdfmetrics.registerFont(TTFont("ImperatorBold", str(bold)))
        return "ImperatorRegular", "ImperatorBold"
    return "Helvetica", "Helvetica-Bold"


FONT, FONT_BOLD = register_fonts()


styles = getSampleStyleSheet()
BODY = ParagraphStyle(
    "Body",
    parent=styles["BodyText"],
    fontName=FONT,
    fontSize=8.9,
    leading=12.4,
    textColor=TEXT,
    spaceAfter=3,
)
BODY_SMALL = ParagraphStyle(
    "BodySmall",
    parent=BODY,
    fontSize=7.7,
    leading=10.3,
    textColor=MUTED,
)
LABEL = ParagraphStyle(
    "Label",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=7.1,
    leading=8.5,
    textColor=GREEN,
    spaceAfter=2,
)
H1 = ParagraphStyle(
    "H1",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=23,
    leading=26,
    textColor=TEXT,
    spaceAfter=7,
)
H2 = ParagraphStyle(
    "H2",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=15,
    leading=18,
    textColor=TEXT,
    spaceBefore=2,
    spaceAfter=7,
)
H3 = ParagraphStyle(
    "H3",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=10.5,
    leading=13,
    textColor=TEXT,
    spaceAfter=4,
)
METRIC = ParagraphStyle(
    "Metric",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=20,
    leading=22,
    textColor=TEXT,
)
CALLOUT = ParagraphStyle(
    "Callout",
    parent=BODY,
    fontName=FONT_BOLD,
    fontSize=10.2,
    leading=14,
    textColor=TEXT,
)


def p(text: str, style: ParagraphStyle = BODY) -> Paragraph:
    return Paragraph(text, style)


def bullets(items: Iterable[str], style: ParagraphStyle = BODY) -> list[Paragraph]:
    return [p(f"<font color='#49E5B2'>•</font> {item}", style) for item in items]


class Rule(Flowable):
    def __init__(self, width: float = CONTENT_W, color: Color = LINE, thickness: float = 0.7):
        super().__init__()
        self.width = width
        self.height = 5
        self.color = color
        self.thickness = thickness

    def draw(self) -> None:
        self.canv.setStrokeColor(self.color)
        self.canv.setLineWidth(self.thickness)
        self.canv.line(0, self.height / 2, self.width, self.height / 2)


def panel(content, widths=None, padding=9, background=PANEL, border=LINE):
    if not isinstance(content, list):
        content = [[content]]
    elif content and not isinstance(content[0], list):
        content = [[item] for item in content]
    table = Table(content, colWidths=widths, hAlign="LEFT")
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), background),
                ("BOX", (0, 0), (-1, -1), 0.7, border),
                ("INNERGRID", (0, 0), (-1, -1), 0.45, border),
                ("LEFTPADDING", (0, 0), (-1, -1), padding),
                ("RIGHTPADDING", (0, 0), (-1, -1), padding),
                ("TOPPADDING", (0, 0), (-1, -1), padding),
                ("BOTTOMPADDING", (0, 0), (-1, -1), padding),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        )
    )
    return table


def title_block(number: str, title: str, subtitle: str) -> list:
    return [
        p(f"IMPERATOR / DISCOVERY FIELD GUIDE / {number}", LABEL),
        p(title, H1),
        p(subtitle, BODY),
        Spacer(1, 4),
    ]


def page_header_footer(canvas, doc) -> None:
    canvas.saveState()
    canvas.setFillColor(INK)
    canvas.rect(0, 0, PAGE_W, PAGE_H, stroke=0, fill=1)
    canvas.setStrokeColor(LINE)
    canvas.setLineWidth(0.5)
    canvas.line(MARGIN_X, 10 * mm, PAGE_W - MARGIN_X, 10 * mm)
    canvas.setFont(FONT_BOLD, 6.7)
    canvas.setFillColor(MUTED)
    canvas.drawString(MARGIN_X, 6.4 * mm, "IMPERATOR - Sistema Operativo para la Inteligencia Operativa")
    canvas.drawRightString(PAGE_W - MARGIN_X, 6.4 * mm, f"{doc.page}")
    canvas.restoreState()


def metric_card(label: str, value: str, note: str):
    return [p(label.upper(), LABEL), p(value, METRIC), p(note, BODY_SMALL)]


def score_cell(value: str, meaning: str):
    return [p(value, H3), p(meaning, BODY_SMALL)]


def line_rows(labels: list[str], row_height=15 * mm):
    rows = []
    for label in labels:
        rows.append([p(label, LABEL), p("", BODY)])
    table = Table(rows, colWidths=[48 * mm, CONTENT_W - 48 * mm], rowHeights=[row_height] * len(rows))
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), PANEL),
                ("BOX", (0, 0), (-1, -1), 0.7, LINE),
                ("INNERGRID", (0, 0), (-1, -1), 0.45, LINE),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 7),
                ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    return table


def cover_story() -> list:
    return [
        Spacer(1, 10 * mm),
        p("IMPERATOR / COMMERCIAL DISCOVERY", LABEL),
        p("Guía de entrevistas<br/>y medición", ParagraphStyle("Cover", parent=H1, fontSize=31, leading=34)),
        p(
            "Un instrumento de campo para comprobar si las empresas necesitan reconstruir decisiones operativas, "
            "relacionarlas con evidencia y demostrar su impacto económico.",
            ParagraphStyle("CoverBody", parent=BODY, fontSize=11.2, leading=16, textColor=MUTED),
        ),
        Spacer(1, 12 * mm),
        panel(
            [[
                metric_card("Empresas objetivo", "30-40", "Lista inicial con señales públicas"),
                metric_card("Conversaciones", "10-15", "Casos reales, no opiniones"),
                metric_card("Meta", "1-2", "Design partners cualificados"),
            ]],
            widths=[CONTENT_W / 3] * 3,
            padding=11,
            background=PANEL_2,
            border=GREEN,
        ),
        Spacer(1, 10 * mm),
        p("LA PREGUNTA CENTRAL", LABEL),
        panel(
            p(
                "¿Existe una decisión operativa reciente, costosa y difícil de reconstruir que una empresa "
                "estaría dispuesta a validar con un piloto acotado?",
                ParagraphStyle("CoverQuestion", parent=CALLOUT, fontSize=15, leading=20),
            ),
            background=TEAL,
            border=GREEN,
            padding=14,
        ),
        Spacer(1, 8 * mm),
        p("REGLA DE DISCOVERY", LABEL),
        p(
            "Primero entiende el caso. Solo después enseña una pantalla. El objetivo no es que guste IMPERATOR; "
            "es descubrir qué ocurrió, cuánto costó y por qué el proceso actual no basta.",
            CALLOUT,
        ),
        Spacer(1, 17 * mm),
        p("Versión de campo - España - Septiembre 2026", BODY_SMALL),
        p("Uso: discovery permitido. Conexión e ingesta de datos de cliente no autorizadas.", BODY_SMALL),
    ]


def build_story() -> list:
    story: list = []
    story.extend(cover_story())
    story.append(PageBreak())

    story.extend(title_block("01", "Qué se está validando", "Una hipótesis comercial concreta, no una plataforma completa."))
    story.append(
        panel(
            p(
                "Las organizaciones AWS-first tienen dificultades para reconstruir por qué se tomó una decisión "
                "operativa, conectar código y coste cloud, identificar al aprobador y comprobar el resultado económico.",
                CALLOUT,
            ),
            background=TEAL,
            border=GREEN,
            padding=12,
        )
    )
    story.append(Spacer(1, 6 * mm))
    flow = [
        ("EVIDENCE", "Hechos observados"),
        ("DECISION", "Proceso y autoridad"),
        ("RECOMMENDATION", "Propuesta determinista"),
        ("ROI", "Impacto explicable"),
        ("LEDGER", "Historia auditable"),
        ("VALUE", "Resultado validado"),
    ]
    story.append(
        panel(
            [[[p(a, LABEL), p(b, BODY_SMALL)] for a, b in flow]],
            widths=[CONTENT_W / 6] * 6,
            padding=6,
            background=PANEL,
        )
    )
    story.append(Spacer(1, 7 * mm))
    story.append(p("Señales válidas", H2))
    valid = bullets([
        "La persona relata una decisión real de los últimos 6-12 meses.",
        "Nombra sistemas, participantes, tiempos y una consecuencia económica o de control.",
        "Reconoce un fallo actual: reconstrucción manual, motivo disperso o resultado nunca validado.",
        "Acepta concretar un siguiente paso con responsable y fecha.",
    ])
    invalid = bullets([
        "'Suena interesante', 'la IA está de moda' o 'seguro que serviría'.",
        "Opiniones hipotéticas sin un caso reciente.",
        "Interés de una sola persona sin sponsor, Platform y Finance/FinOps.",
        "Peticiones que exigen multi-tenant, otro cloud o ejecución autónoma.",
    ])
    story.append(panel([[[p("CUENTA COMO EVIDENCIA", LABEL), *valid], [p("NO CUENTA", LABEL), *invalid]]], widths=[CONTENT_W / 2] * 2, padding=9))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Límites actuales", H2))
    story.append(panel([[p("Discovery y demo sintética", BODY), p("PERMITIDO", LABEL)], [p("Conectar sistemas o ingerir datos reales", BODY), p("PROHIBIDO", ParagraphStyle("No", parent=LABEL, textColor=RED))], [p("Ejecutar piloto", BODY), p("NO AUTORIZADO", ParagraphStyle("No2", parent=LABEL, textColor=AMBER))]], widths=[CONTENT_W - 42 * mm, 42 * mm], padding=8))
    story.append(PageBreak())

    story.extend(title_block("02", "A quién entrevistar", "Empresas y roles capaces de aportar evidencia útil y, más adelante, patrocinar un caso."))
    story.append(p("Perfil de cuenta prioritario", H2))
    story.append(panel([[p("SaaS B2B / producto digital / IA", BODY), p("100-500 personas", BODY), p("AWS + repositorio productivo", BODY)], [p("Platform o DevOps visible", BODY), p("Cloud/IA con coste material", BODY), p("Finance/FinOps accesible", BODY)]], widths=[CONTENT_W / 3] * 3, padding=8, background=PANEL_2))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Roles y valor de la conversación", H2))
    role_rows = [
        [p("ROL", LABEL), p("QUÉ SABE", LABEL), p("SEÑAL QUE BUSCAS", LABEL)],
        [p("CTO / VP Engineering", BODY), p("Prioridades y autoridad", BODY_SMALL), p("Sponsor y presupuesto del problema", BODY_SMALL)],
        [p("Head of Platform / SRE", BODY), p("Flujo real y sistemas", BODY_SMALL), p("Tiempo de reconstrucción y evidencia", BODY_SMALL)],
        [p("FinOps / Finance", BODY), p("Baseline y resultado", BODY_SMALL), p("Método para validar euros", BODY_SMALL)],
        [p("Security / Compliance", BODY), p("Control y auditoría", BODY_SMALL), p("Qué evidencia aceptaría", BODY_SMALL)],
        [p("Product / Business owner", BODY), p("Objetivo y valor", BODY_SMALL), p("Por qué existe la capacidad", BODY_SMALL)],
    ]
    story.append(panel(role_rows, widths=[43 * mm, 59 * mm, CONTENT_W - 102 * mm], padding=7))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Descalificación temprana", H2))
    story.extend(bullets([
        "El primer caso exige Azure/GCP, múltiples cuentas, múltiples repositorios o conectores no certificados.",
        "Requiere prompts, código fuente, datos personales/restringidos o secretos.",
        "Espera decisiones autónomas, ejecución en producción o ahorro garantizado.",
        "No puede identificar una decisión ni un responsable del resultado.",
    ]))
    story.append(Spacer(1, 5 * mm))
    story.append(panel(p("Una empresa grande y prestigiosa puede ser una entrevista excelente y un primer piloto pésimo. Puntúa acceso, caso acotable y velocidad de decisión.", CALLOUT), background=TEAL, border=GREEN, padding=10))
    story.append(PageBreak())

    story.extend(title_block("03", "Proceso de 2-3 semanas", "Un embudo pequeño, disciplinado y basado en evidencia."))
    funnel = [[
        metric_card("Identificar", "30-40", "Cuentas con fit público"),
        metric_card("Contactar", "15-20", "Personas relevantes"),
        metric_card("Entrevistar", "10-15", "Decisiones recientes"),
        metric_card("Cualificar", "2-3", "Casos y dueños"),
        metric_card("Objetivo", "1-2", "Pilotos potenciales"),
    ]]
    story.append(panel(funnel, widths=[CONTENT_W / 5] * 5, padding=7, background=PANEL_2, border=GREEN))
    story.append(Spacer(1, 7 * mm))
    weeks = [
        [p("SEMANA 1", LABEL), p("Construir lista, puntuar fit, buscar introducciones y contactar. No enviar el deck en frío.", BODY)],
        [p("SEMANA 2", LABEL), p("Realizar entrevistas, registrar hechos el mismo día y perseguir un caso hasta el resultado.", BODY)],
        [p("SEMANA 3", LABEL), p("Agrupar patrones, recontactar candidatos fuertes y solicitar un workshop de pilot-readiness.", BODY)],
    ]
    story.append(panel(weeks, widths=[28 * mm, CONTENT_W - 28 * mm], padding=9))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Preparación de cada llamada", H2))
    story.extend(bullets([
        "Define una hipótesis de 1 línea sobre el problema de esa empresa.",
        "Identifica rol, sector, tamaño aproximado y señales públicas. No infieras gasto ni arquitectura privada.",
        "Prepara 2 preguntas específicas y una vía de cierre.",
        "Pide permiso para tomar notas; no grabes sin consentimiento expreso.",
        "No almacenes datos personales, secretos o información confidencial en el repositorio.",
    ]))
    story.append(Spacer(1, 5 * mm))
    story.append(p("Mensaje inicial", H2))
    story.append(panel(p("Estoy investigando cómo equipos de Engineering y Platform reconstruyen decisiones técnicas que afectan a costes, riesgo o auditoría. Busco una conversación de 20-25 minutos sobre un caso reciente; no necesito acceso a sistemas ni estoy intentando venderte una plataforma.", BODY), background=PANEL_2, padding=10))
    story.append(PageBreak())

    story.extend(title_block("04", "Guion de 25 minutos", "Pregunta por hechos. Profundiza. Enseña una sola pantalla solo cuando el caso lo justifique."))
    timeline = [
        [p("0-3 MIN", LABEL), p("Contexto del rol", H3), p("¿Qué responsabilidad tienes sobre infraestructura, costes, producto o decisiones técnicas?", BODY_SMALL)],
        [p("3-15 MIN", LABEL), p("Un caso real", H3), p("Cuéntame una decisión operativa reciente que tuviera impacto económico.", BODY_SMALL)],
        [p("15-20 MIN", LABEL), p("Resultado y dolor", H3), p("¿Cómo supisteis si funcionó? ¿Qué fue lento, difícil o quedó sin comprobar?", BODY_SMALL)],
        [p("20-23 MIN", LABEL), p("Demo opcional", H3), p("Una pantalla del caso sintético DRC-AOA-001. Nunca las cinco por defecto.", BODY_SMALL)],
        [p("23-25 MIN", LABEL), p("Compromiso", H3), p("¿Quién debería participar en un workshop y qué fecha tendría sentido?", BODY_SMALL)],
    ]
    story.append(panel(timeline, widths=[25 * mm, 46 * mm, CONTENT_W - 71 * mm], padding=8))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Las 7 preguntas base", H2))
    questions = [
        "Cuéntame una decisión operativa reciente que tuviera impacto económico.",
        "¿Qué evidencia se consultó antes de decidir y dónde estaba?",
        "¿Quién participó y quién tuvo autoridad para aprobarla?",
        "¿Dónde quedó registrado el motivo y los supuestos?",
        "¿Pudisteis comprobar después si produjo el resultado esperado?",
        "¿Qué parte fue más lenta, costosa o difícil?",
        "¿Sería suficientemente importante como para pagar por resolverla? ¿De qué presupuesto saldría?",
    ]
    for idx, question in enumerate(questions, 1):
        story.append(p(f"<font color='#49E5B2'><b>{idx:02d}</b></font> &nbsp; {question}", BODY))
    story.append(Spacer(1, 5 * mm))
    story.append(panel(p("No preguntes: '¿Te gustaría esta función?'. Pregunta: '¿Qué hiciste la última vez, cuánto tardó y qué ocurrió después?'.", CALLOUT), background=TEAL, border=GREEN, padding=10))
    story.append(PageBreak())

    story.extend(title_block("05", "Sondas y señales", "Qué preguntar después de la primera respuesta y cómo interpretar lo que oyes."))
    probe_rows = [
        [p("CUANDO OIGAS...", LABEL), p("PREGUNTA...", LABEL), p("BUSCAS...", LABEL)],
        [p("'Tuvimos que investigar'", BODY), p("¿Cuántas personas y horas?", BODY_SMALL), p("Coste de reconstrucción", BODY_SMALL)],
        [p("'Lo vimos en AWS'", BODY), p("¿Qué otra evidencia necesitasteis?", BODY_SMALL), p("Fragmentación GitHub/AWS/Jira", BODY_SMALL)],
        [p("'Lo aprobó el CTO'", BODY), p("¿Dónde quedó el motivo?", BODY_SMALL), p("Trazabilidad y autoridad", BODY_SMALL)],
        [p("'Ahorramos bastante'", BODY), p("¿Qué baseline y periodo usasteis?", BODY_SMALL), p("Impacto verificable", BODY_SMALL)],
        [p("'No volvimos a mirarlo'", BODY), p("¿Qué impedía validarlo?", BODY_SMALL), p("Hueco Business Value", BODY_SMALL)],
        [p("'Nos interesa'", BODY), p("¿Qué caso probarías y quién lo patrocina?", BODY_SMALL), p("Compromiso real", BODY_SMALL)],
    ]
    story.append(panel(probe_rows, widths=[43 * mm, 64 * mm, CONTENT_W - 107 * mm], padding=7))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Lo que puedes esperar", H2))
    expectations = [
        [p("RESPUESTA", LABEL), p("LECTURA", LABEL), p("ACCIÓN", LABEL)],
        [p("Caso detallado + cifras", BODY), p("Evidencia fuerte", BODY_SMALL), p("Solicita responsable financiero", BODY_SMALL)],
        [p("Caso real sin cifras", BODY), p("Dolor posible", BODY_SMALL), p("Busca tiempo, riesgo o rango", BODY_SMALL)],
        [p("Proceso claro y barato", BODY), p("Dolor débil", BODY_SMALL), p("No fuerces el encaje", BODY_SMALL)],
        [p("Problema fuerte, stack distinto", BODY), p("Mercado futuro", BODY_SMALL), p("Registra; no amplíes MVP", BODY_SMALL)],
        [p("Entusiasmo sin próximo paso", BODY), p("Señal social", BODY_SMALL), p("No cuenta como piloto", BODY_SMALL)],
        [p("Sponsor + caso + fecha", BODY), p("Compromiso", BODY_SMALL), p("Candidato a qualification gate", BODY_SMALL)],
    ]
    story.append(panel(expectations, widths=[52 * mm, 49 * mm, CONTENT_W - 101 * mm], padding=7, background=PANEL_2))
    story.append(PageBreak())

    story.extend(title_block("06", "Ficha de entrevista", "Rellénala durante la llamada. Usa hechos y expresiones del interlocutor, no interpretaciones."))
    story.append(line_rows(["Discovery ID / fecha / empresa", "Participante y rol", "Decisión concreta", "Motivo / trigger", "Evidencia y sistemas"], row_height=15 * mm))
    story.append(Spacer(1, 5 * mm))
    story.append(line_rows(["Personas / aprobador", "Proceso actual", "Tiempo / esfuerzo", "Impacto económico o riesgo", "Resultado y medición"], row_height=15 * mm))
    story.append(PageBreak())

    story.extend(title_block("07", "Ficha de cierre", "Termina la entrevista con viabilidad, autoridad y una acción observable."))
    story.append(line_rows(["Qué parte fue más difícil", "Datos que serían necesarios", "Datos que deben quedar fuera", "Presupuesto / dueño económico", "Objeciones / por qué no comprarían"], row_height=15 * mm))
    story.append(Spacer(1, 5 * mm))
    story.append(line_rows(["Siguiente acción", "Responsable", "Fecha", "Etapa del funnel", "Nota textual más importante"], row_height=15 * mm))
    story.append(PageBreak())

    story.extend(title_block("08", "Medición sin autoengaño", "Puntúa al terminar, antes de mirar otras entrevistas. Conserva evidencia para cada punto."))
    criteria = [
        ("Frecuencia", "Raro", "Ocasional", "Recurrente"),
        ("Impacto económico", "Ninguno", "Difícil de cuantificar", "Cuantificable"),
        ("Evidencia fragmentada", "No", "Algo", "Múltiples sistemas / manual"),
        ("Aprobación / auditoría", "No importa", "Parcial", "Material / obligatoria"),
        ("Interés en piloto", "No", "Curiosidad", "Caso + responsable + fecha"),
    ]
    score_rows = [[p("CRITERIO", LABEL), p("0", LABEL), p("1", LABEL), p("2", LABEL), p("PUNTOS", LABEL)]]
    for name, zero, one, two in criteria:
        score_rows.append([p(name, BODY), p(zero, BODY_SMALL), p(one, BODY_SMALL), p(two, BODY_SMALL), p("____", BODY)])
    score_rows.append([p("TOTAL", H3), "", "", p("Umbral orientativo: 7/10", BODY_SMALL), p("____ / 10", H3)])
    story.append(panel(score_rows, widths=[35 * mm, 35 * mm, 37 * mm, 52 * mm, CONTENT_W - 159 * mm], padding=6))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Gates obligatorios", H2))
    story.append(panel([[p("CASO CONCRETO", LABEL), p("SÍ / NO", CALLOUT), p("DOLOR ACTUAL", LABEL), p("SÍ / NO", CALLOUT)], [p("SIGUIENTE PASO", LABEL), p("SÍ / NO", CALLOUT), p("RESPONSABLE + FECHA", LABEL), p("SÍ / NO", CALLOUT)]], widths=[40 * mm, 32 * mm, 52 * mm, CONTENT_W - 124 * mm], padding=8, background=TEAL, border=GREEN))
    story.append(Spacer(1, 6 * mm))
    story.append(panel(p("Regla: una puntuación >= 7 no convierte a nadie en candidato fuerte si no hay caso concreto y dolor actual. Para contar como interés en piloto debe existir además una acción, un responsable y una fecha.", CALLOUT), background=PANEL_2, border=AMBER, padding=10))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Clasificación", H2))
    story.append(panel([[score_cell("0-3", "NO-GO / poco dolor"), score_cell("4-6", "DISCOVERY CONTINUE"), score_cell("7-10", "FUERTE, si pasa gates"), score_cell("QUALIFIED", "Pasa la checklist completa")]], widths=[CONTENT_W / 4] * 4, padding=9))
    story.append(PageBreak())

    story.extend(title_block("09", "Fit antes de contactar", "Prioriza el acceso y la probabilidad de aprender. No confundas señales públicas con hechos internos."))
    fit_rows = [
        [p("SEÑAL PÚBLICA", LABEL), p("0", LABEL), p("1", LABEL), p("2", LABEL)],
        [p("Tamaño / complejidad", BODY), p("Muy pequeña o corporación", BODY_SMALL), p("Cercana al rango", BODY_SMALL), p("100-500", BODY_SMALL)],
        [p("Cloud / AWS", BODY), p("Sin señal", BODY_SMALL), p("Cloud genérico", BODY_SMALL), p("AWS público", BODY_SMALL)],
        [p("Producto / ingeniería", BODY), p("Bajo", BODY_SMALL), p("Equipo técnico", BODY_SMALL), p("SaaS / plataforma", BODY_SMALL)],
        [p("Coste / control", BODY), p("No visible", BODY_SMALL), p("Posible", BODY_SMALL), p("IA, FinTech, regulación", BODY_SMALL)],
        [p("Acceso", BODY), p("Frío", BODY_SMALL), p("Contacto de segundo grado", BODY_SMALL), p("Introducción cálida", BODY_SMALL)],
    ]
    story.append(panel(fit_rows, widths=[46 * mm, 43 * mm, 43 * mm, CONTENT_W - 132 * mm], padding=7))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Dos rankings distintos", H2))
    story.append(panel([[[p("PROSPECT FIT SCORE", LABEL), p("Antes de contactar", H3), p("Decide dónde invertir tiempo. No valida el problema.", BODY_SMALL)], [p("PROBLEM EVIDENCE SCORE", LABEL), p("Después de entrevistar", H3), p("Mide hechos, impacto y compromiso. Decide si avanzar.", BODY_SMALL)]]], widths=[CONTENT_W / 2] * 2, padding=10, background=PANEL_2, border=GREEN))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Reglas anti-sesgo", H2))
    story.extend(bullets([
        "No puntúes mejor una empresa por su marca o por viajar a verla.",
        "No rellenes huecos con inferencias sobre AWS, GitHub o gasto.",
        "Registra citas breves y hechos observables; separa interpretación.",
        "No cambies las preguntas base durante las primeras 10 entrevistas.",
        "Registra también entrevistas negativas: reducen riesgo de producto.",
    ]))
    story.append(PageBreak())

    story.extend(title_block("10", "Síntesis después de 10", "Busca patrones repetidos, no una media que oculte diferencias."))
    pattern_rows = [
        [p("PATRÓN", LABEL), p("SECUENCIA OBSERVABLE", LABEL), p("EVIDENCIA MÍNIMA", LABEL)],
        [p("Cloud cost", BODY), p("Coste sube -> investigar -> decidir -> no validar", BODY_SMALL), p("3 casos independientes", BODY_SMALL)],
        [p("IA / modelos", BODY), p("Uso cambia -> comparar coste/calidad -> aprobar", BODY_SMALL), p("2 casos con euros", BODY_SMALL)],
        [p("Arquitectura", BODY), p("PR + tickets + chat -> motivo disperso", BODY_SMALL), p("3 casos con retraso", BODY_SMALL)],
        [p("Auditoría", BODY), p("Piden justificar -> reconstrucción manual", BODY_SMALL), p("2 casos con control", BODY_SMALL)],
    ]
    story.append(panel(pattern_rows, widths=[36 * mm, 91 * mm, CONTENT_W - 127 * mm], padding=8))
    story.append(Spacer(1, 6 * mm))
    story.append(p("Matriz de síntesis", H2))
    matrix_rows = [[p("EMPRESA", LABEL), p("CASO", LABEL), p("DOLOR", LABEL), p("€/TIEMPO", LABEL), p("SISTEMAS", LABEL), p("SCORE", LABEL), p("ETAPA", LABEL)]]
    for _ in range(8):
        matrix_rows.append(["", "", "", "", "", "", ""])
    matrix = Table(matrix_rows, colWidths=[25 * mm, 33 * mm, 28 * mm, 25 * mm, 29 * mm, 16 * mm, CONTENT_W - 156 * mm], rowHeights=[9 * mm] + [13 * mm] * 8)
    matrix.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), PANEL), ("BOX", (0, 0), (-1, -1), 0.7, LINE), ("INNERGRID", (0, 0), (-1, -1), 0.45, LINE), ("VALIGN", (0, 0), (-1, -1), "TOP"), ("LEFTPADDING", (0, 0), (-1, -1), 4), ("RIGHTPADDING", (0, 0), (-1, -1), 4), ("TOPPADDING", (0, 0), (-1, -1), 4)]))
    story.append(matrix)
    story.append(Spacer(1, 5 * mm))
    story.append(panel(p("Hito 1: tres empresas independientes confirman el problema con casos propios. Hito 2: una pasa el Design Partner Qualification Gate. Solo entonces hay señal comercial útil.", CALLOUT), background=TEAL, border=GREEN, padding=10))
    story.append(PageBreak())

    story.extend(title_block("11", "Design Partner Gate", "Una conversación prometedora no autoriza un piloto. Todos los controles siguientes deben pasar."))
    checks = [
        "[ ] Problema recurrente y material.",
        "[ ] Una Decision comparable a DRC-AOA-001 identificada.",
        "[ ] Sponsor CTO / VP Engineering.",
        "[ ] Platform y Finance/FinOps participan.",
        "[ ] Un repositorio GitHub y una cuenta/región AWS bastan.",
        "[ ] Acceso read-only aceptable en principio.",
        "[ ] Baseline de coste y fuente posterior de resultado.",
        "[ ] Datos restringidos, prompts y secretos quedan fuera.",
        "[ ] Aprobación humana y ejecución externa aceptadas.",
        "[ ] Resultado medible sin prometer ahorro.",
        "[ ] Workshop con responsable y fecha.",
        "[ ] No exige saltarse Sprint 4.4, Pilot Identity Conformance ni Pilot Readiness.",
    ]
    story.append(panel([[p(checks[i], BODY), p(checks[i + 1], BODY)] for i in range(0, len(checks), 2)], widths=[CONTENT_W / 2] * 2, padding=8))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Resultado del gate", H2))
    story.append(panel([[score_cell("QUALIFIED", "Todos los controles pasan"), score_cell("CONTINUE", "Falta evidencia o dueño"), score_cell("FUTURE FIT", "Buen problema, fuera del MVP"), score_cell("NO-GO", "No hay dolor o viabilidad")]], widths=[CONTENT_W / 4] * 4, padding=9, background=PANEL_2))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Cierre recomendado", H2))
    story.append(panel(p("Me habéis contado un caso concreto. Si reuniéramos a Engineering, Platform y Finance para definir evidencia, baseline y criterio de resultado, ¿quién debería participar y qué fecha tendría sentido?", CALLOUT), background=TEAL, border=GREEN, padding=11))
    story.append(PageBreak())

    story.extend(title_block("12", "Cómo usar la demo", "La demo es una herramienta de contraste después del descubrimiento, no el inicio de la reunión."))
    demo_rows = [
        [p("SI EL DOLOR ES...", LABEL), p("MUESTRA...", LABEL), p("PREGUNTA...", LABEL)],
        [p("Prioridad y dinero", BODY), p("Executive Workspace", BODY_SMALL), p("¿Qué dato necesitarías primero?", BODY_SMALL)],
        [p("Confianza", BODY), p("Decision Detail", BODY_SMALL), p("¿Qué evidencia faltaría?", BODY_SMALL)],
        [p("Auditoría", BODY), p("Decision Ledger", BODY_SMALL), p("¿Qué actor o motivo exigirías?", BODY_SMALL)],
        [p("Resultado", BODY), p("Business Value", BODY_SMALL), p("¿Quién validaría el valor?", BODY_SMALL)],
    ]
    story.append(panel(demo_rows, widths=[45 * mm, 52 * mm, CONTENT_W - 97 * mm], padding=8))
    story.append(Spacer(1, 7 * mm))
    story.append(p("Qué debes declarar", H2))
    story.extend(bullets([
        "DRC-AOA-001 es un caso canónico sintético.",
        "30 Evidence proceden del dataset de demostración GitHub + AWS.",
        "19.440 EUR/año es una estimación determinista bajo supuestos explícitos.",
        "92% es confianza del caso sintético; no rendimiento predictivo validado en cliente.",
        "El valor realizado permanece vacío hasta una validación posterior de Finance.",
        "La IA puede explicar, pero no decide, aprueba ni ejecuta.",
    ]))
    story.append(Spacer(1, 7 * mm))
    story.append(panel(p("Pregunta final de demo: 'Si hubierais tenido esto en vuestro caso, ¿qué habría cambiado y qué parte no os serviría?'. La segunda mitad es la más valiosa.", CALLOUT), background=TEAL, border=GREEN, padding=11))
    story.append(Spacer(1, 9 * mm))
    story.append(p("Resultado esperado del proceso", H2))
    story.append(panel([[metric_card("Confirmación", "3+", "Empresas con caso propio"), metric_card("Candidato", "1", "Pasa qualification gate"), metric_card("Aprendizaje", "1", "Patrón prioritario")]], widths=[CONTENT_W / 3] * 3, padding=10, background=PANEL_2, border=GREEN))

    return story


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    doc = SimpleDocTemplate(
        str(OUTPUT_FILE),
        pagesize=A4,
        rightMargin=MARGIN_X,
        leftMargin=MARGIN_X,
        topMargin=MARGIN_TOP,
        bottomMargin=MARGIN_BOTTOM,
        title="IMPERATOR - Guia de entrevistas y medicion de discovery",
        author="IMPERATOR",
        subject="Guia operativa para entrevistas de problem discovery y cualificacion de design partners",
    )
    doc.build(build_story(), onFirstPage=page_header_footer, onLaterPages=page_header_footer)
    print(OUTPUT_FILE)


if __name__ == "__main__":
    main()
