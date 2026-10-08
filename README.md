# EduAndes - API de matrícula académica

Backend REST construido con Spring Boot 4, Java 21, Spring Data JPA y Oracle. La API administra carreras, cursos, estudiantes y matrículas mediante DTO de entrada y salida; las reglas de matrícula viven en la capa de servicio.

## Requisitos

- Java 21
- Oracle con el servicio `FREEPDB1`
- Maven Wrapper incluido

## Perfiles

- `dev` es el perfil predeterminado. Usa Oracle en `localhost:1522`, usuario `eduandes` y `ddl-auto: update`.
- `prod` usa `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`, con `ddl-auto: validate` y Swagger deshabilitado.

Para iniciar en desarrollo:

```powershell
.\mvnw.cmd spring-boot:run
```

Para producción:

```powershell
$env:SPRING_PROFILES_ACTIVE='prod'
$env:DB_URL='jdbc:oracle:thin:@//servidor:1521/SERVICIO'
$env:DB_USERNAME='usuario'
$env:DB_PASSWORD='clave'
.\mvnw.cmd spring-boot:run
```

Swagger UI queda disponible en `http://localhost:8080/swagger-ui.html` cuando se usa `dev`.

## Datos semilla

Después del primer arranque, ejecuta [`datos_semilla.sql`](datos_semilla.sql) en SQL Developer. El script agrega tres carreras, doce cursos y seis estudiantes para probar las reglas del examen.

Si las tablas se crearon antes de que `creditos`, `ciclo` y `vacantes` fueran numéricos, recrea el esquema de desarrollo o migra esas tres columnas a `NUMBER` antes de iniciar esta versión.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET/POST | `/api/v1/carreras` | Listar y registrar carreras |
| GET/PUT/DELETE | `/api/v1/carreras/{id}` | Consultar, actualizar y eliminar una carrera |
| GET | `/api/v1/carreras/{id}/cursos` | Cursos de una carrera |
| GET/POST | `/api/v1/cursos` | Listar y registrar cursos |
| GET | `/api/v1/cursos/buscar` | Buscar por nombre, carrera, ciclo y vacantes |
| GET/PUT/DELETE | `/api/v1/cursos/{id}` | Consultar, actualizar y eliminar un curso |
| GET/POST | `/api/v1/estudiantes` | Listar y registrar estudiantes |
| GET/PUT/DELETE | `/api/v1/estudiantes/{id}` | Consultar, actualizar y eliminar un estudiante |
| GET/POST | `/api/v1/matriculas` | Listar y registrar matrículas |
| GET | `/api/v1/matriculas/{id}` | Consultar una matrícula con sus detalles |
| PATCH | `/api/v1/matriculas/{id}/anular` | Anular y devolver las vacantes |
| GET | `/api/v1/reportes/matriculados-por-curso` | Reporte por `periodo` y `carreraId` opcional |

Ejemplo de matrícula:

```json
{
  "periodo": "2026-2",
  "estudianteId": 1,
  "detalles": [
    { "cursoId": 1 },
    { "cursoId": 2 }
  ]
}
```

El servidor obtiene los créditos del curso y calcula el costo usando `matricula.costo-credito`.

## Reglas de matrícula

- El estudiante y los cursos deben estar activos y pertenecer a la misma carrera.
- Cada curso debe tener vacantes; registrar descuenta una y anular la devuelve.
- Un estudiante solo puede tener una matrícula `REGISTRADA` por periodo.
- Una matrícula no puede superar 20 créditos.
- La operación es transaccional y bloquea los registros involucrados para evitar sobreventa de vacantes.

## Respuestas de error

La API devuelve `ErrorResponseDTO` de forma uniforme:

- `400 Bad Request`: validación, JSON o parámetros incorrectos.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: regla de negocio o relación que impide eliminar.
- `500 Internal Server Error`: error inesperado sin exponer la traza.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas unitarias no requieren que Oracle esté encendido. La colección [`postman/EduAndes.postman_collection.json`](postman/EduAndes.postman_collection.json) contiene los casos funcionales; ajusta sus identificadores si la base ya tenía datos.

## Estructura

```text
controller -> service/service + service/impl -> repository -> entity
                 |                         |
                 +------ DTO request/response
```

Los controladores solo traducen HTTP. Los servicios contienen validaciones, transacciones y reglas. Los repositorios concentran las consultas y bloqueos de base de datos.
