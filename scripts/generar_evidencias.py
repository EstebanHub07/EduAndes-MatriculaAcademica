from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "output" / "pdf" / "evidencias_eduandes.pdf"
OUTPUT.parent.mkdir(parents=True, exist_ok=True)

NAVY = colors.HexColor("#12345B")
BLUE = colors.HexColor("#1F5A94")
GOLD = colors.HexColor("#E3B23C")
LIGHT = colors.HexColor("#EEF3F8")
GREEN = colors.HexColor("#237A4B")
GRAY = colors.HexColor("#5C6773")

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="TitleEdu",
    parent=styles["Title"],
    fontName="Helvetica-Bold",
    fontSize=23,
    leading=28,
    textColor=NAVY,
    alignment=TA_CENTER,
    spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="SubtitleEdu",
    parent=styles["Normal"],
    fontName="Helvetica",
    fontSize=11,
    leading=15,
    textColor=GRAY,
    alignment=TA_CENTER,
    spaceAfter=18,
))
styles.add(ParagraphStyle(
    name="HeadingEdu",
    parent=styles["Heading2"],
    fontName="Helvetica-Bold",
    fontSize=14,
    leading=18,
    textColor=BLUE,
    spaceBefore=8,
    spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="BodyEdu",
    parent=styles["BodyText"],
    fontName="Helvetica",
    fontSize=9.5,
    leading=14,
    textColor=colors.HexColor("#202833"),
    spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="SmallEdu",
    parent=styles["BodyText"],
    fontName="Helvetica",
    fontSize=8,
    leading=11,
    textColor=GRAY,
))
styles.add(ParagraphStyle(
    name="CodeEdu",
    parent=styles["Code"],
    fontName="Courier",
    fontSize=8,
    leading=11,
    textColor=colors.HexColor("#132238"),
    backColor=LIGHT,
    borderPadding=7,
    spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="CellEdu",
    parent=styles["BodyText"],
    fontName="Helvetica",
    fontSize=8,
    leading=10,
    textColor=colors.HexColor("#202833"),
))
styles.add(ParagraphStyle(
    name="CellHeaderEdu",
    parent=styles["BodyText"],
    fontName="Helvetica-Bold",
    fontSize=8,
    leading=10,
    textColor=colors.white,
))


def footer(canvas, doc):
    canvas.saveState()
    canvas.resetTransforms()
    canvas.setStrokeColor(GOLD)
    canvas.setLineWidth(0.7)
    canvas.line(18 * mm, 15 * mm, 192 * mm, 15 * mm)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(GRAY)
    canvas.drawString(18 * mm, 10 * mm, "EduAndes - Evidencias tecnicas")
    canvas.drawRightString(192 * mm, 10 * mm, f"Pagina {canvas.getPageNumber()}")
    canvas.restoreState()


def p(text, style="BodyEdu"):
    return Paragraph(text, styles[style])


def status_table(rows, widths):
    formatted = [
        [Paragraph(str(value), styles["CellHeaderEdu"]) for value in rows[0]]
    ]
    formatted.extend(
        [Paragraph(str(value), styles["CellEdu"]) for value in row]
        for row in rows[1:]
    )
    table = Table(formatted, colWidths=widths, repeatRows=1, hAlign="LEFT")
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), NAVY),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
        ("FONTNAME", (0, 1), (-1, -1), "Helvetica"),
        ("FONTSIZE", (0, 0), (-1, -1), 8),
        ("LEADING", (0, 0), (-1, -1), 10),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.35, colors.HexColor("#AAB7C4")),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, LIGHT]),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]))
    return table


doc = SimpleDocTemplate(
    str(OUTPUT),
    pagesize=A4,
    rightMargin=18 * mm,
    leftMargin=18 * mm,
    topMargin=18 * mm,
    bottomMargin=22 * mm,
    title="Evidencias tecnicas EduAndes",
    author="Equipo EduAndes",
)

story = [
    Spacer(1, 14 * mm),
    p("EduAndes", "TitleEdu"),
    p("Evidencias tecnicas - API de matricula academica", "SubtitleEdu"),
    status_table([
        ["Dato", "Resultado verificado"],
        ["Fecha", "08 de octubre de 2026"],
        ["Rama", "fix/rubrica-villaizan"],
        ["Compilacion", "Correcta"],
        ["Pruebas", "4 ejecutadas, 0 errores, 0 fallos"],
        ["Coleccion Postman", "JSON valido, 14 solicitudes"],
        ["Base de datos", "Oracle no disponible en localhost:1522 durante esta revision"],
    ], [48 * mm, 120 * mm]),
    Spacer(1, 8 * mm),
    p("Alcance de la evidencia", "HeadingEdu"),
    p(
        "Este documento registra verificaciones reproducibles realizadas sobre el codigo. "
        "No presenta como exitosas llamadas HTTP que no pudieron ejecutarse: el listener de "
        "Oracle estaba apagado al momento de la revision. Health y CORS se excluyen por decision "
        "expresa del propietario del proyecto."
    ),
    p("Comando ejecutado", "HeadingEdu"),
    p(".\\mvnw.cmd clean test", "CodeEdu"),
    p(
        "Resultado: CarreraServiceImplTest ejecuto 1 prueba y MatriculaServiceImplTest ejecuto "
        "3 pruebas. Todas finalizaron sin errores ni fallos."
    ),
    p("Comportamientos cubiertos", "HeadingEdu"),
    status_table([
        ["Prueba", "Evidencia"],
        ["Calculo de costo", "3 creditos x S/ 120.00 = S/ 360.00"],
        ["Descuento de vacante", "El curso pasa de 5 a 4 vacantes"],
        ["RN-02", "Curso con cero vacantes produce ReglaNegocioException"],
        ["RN-04", "Matricula mayor a 20 creditos no se guarda"],
        ["Eliminacion protegida", "Carrera con estudiantes no se elimina"],
    ], [55 * mm, 113 * mm]),
    PageBreak(),
    p("Cumplimiento tecnico implementado", "TitleEdu"),
    p("Resumen verificable en el codigo fuente y los artefactos entregados.", "SubtitleEdu"),
    status_table([
        ["Criterio", "Estado", "Implementacion"],
        ["Perfiles", "LISTO", "dev usa update; prod usa validate y variables de entorno"],
        ["Modelo", "LISTO", "creditos, ciclo y vacantes son Integer"],
        ["DTO", "LISTO", "controladores reciben y devuelven DTO"],
        ["Errores", "LISTO", "400, 404, 409 y 500 con ErrorResponseDTO"],
        ["RN-01", "LISTO", "estudiante/curso activos y misma carrera"],
        ["RN-02", "LISTO", "bloqueo de curso, descuento y devolucion de vacante"],
        ["RN-03", "LISTO", "bloqueo de estudiante y matricula unica por periodo"],
        ["RN-04", "LISTO", "limite de 20 creditos y BigDecimal"],
        ["Reporte", "LISTO", "JPQL agregado con proyeccion"],
        ["Semilla", "LISTO", "3 carreras, 12 cursos y 6 estudiantes"],
        ["Postman", "LISTO", "14 solicitudes con validaciones de estado"],
        ["Health y CORS", "EXCLUIDO", "Fuera del alcance por decision del propietario"],
    ], [42 * mm, 25 * mm, 101 * mm]),
    Spacer(1, 8 * mm),
    p("Archivos de entrega", "HeadingEdu"),
    status_table([
        ["Archivo", "Proposito"],
        ["README.md", "Ejecucion, arquitectura, perfiles, endpoints y reglas"],
        ["datos_semilla.sql", "Datos requeridos para Oracle"],
        ["postman/EduAndes.postman_collection.json", "Casos funcionales automatizados"],
        ["output/pdf/evidencias_eduandes.pdf", "Este documento de evidencias"],
    ], [72 * mm, 96 * mm]),
    PageBreak(),
    p("Verificacion en vivo pendiente", "TitleEdu"),
    p("Pasos para completar las capturas de Swagger UI y Postman.", "SubtitleEdu"),
    p("1. Iniciar Oracle", "HeadingEdu"),
    p(
        "Confirma que FREEPDB1 atienda en localhost:1522 y que el esquema EDUANDES exista. "
        "Si el esquema conserva columnas VARCHAR2 antiguas para Curso, recrea o migra esas columnas."
    ),
    p("2. Arrancar y cargar datos", "HeadingEdu"),
    p(".\\mvnw.cmd spring-boot:run", "CodeEdu"),
    p("Ejecuta datos_semilla.sql una sola vez desde SQL Developer."),
    p("3. Capturar Swagger", "HeadingEdu"),
    p(
        "Abre http://localhost:8080/swagger-ui.html y captura el titulo Matricula API, la version v1 "
        "y los endpoints de carreras, cursos, estudiantes, matriculas y reportes."
    ),
    p("4. Ejecutar Postman", "HeadingEdu"),
    p(
        "Importa la coleccion, verifica sus identificadores y ejecuta las solicitudes en orden. "
        "La matricula valida guarda matriculaId para las dos pruebas de anulacion."
    ),
    p("5. Evidencias minimas", "HeadingEdu"),
    status_table([
        ["Captura", "Resultado esperado"],
        ["Swagger UI", "Todos los endpoints documentados"],
        ["Matricula valida", "201, 11 creditos, monto S/ 1320.00"],
        ["RN-01 a RN-04", "409 sin cambios parciales"],
        ["Reporte", "200 con codigo, curso, matriculados y montoRecaudado"],
        ["Anulacion", "200 ANULADA; repeticion 409; vacantes restauradas"],
    ], [55 * mm, 113 * mm]),
    Spacer(1, 8 * mm),
    p(
        "Estado final de esta revision: codigo y pruebas automatizadas listos; evidencia HTTP en vivo "
        "pendiente exclusivamente de disponer del listener Oracle.",
        "SmallEdu",
    ),
]

doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(OUTPUT)
