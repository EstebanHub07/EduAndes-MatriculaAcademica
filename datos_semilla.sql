-- Ejecutar en el esquema EDUANDES después del primer arranque de la API.

INSERT INTO carreras (nombre, descripcion, estado, fecha_creacion)
VALUES ('Ingeniería de Sistemas', 'EP Ingeniería de Sistemas', 1, CURRENT_TIMESTAMP);
INSERT INTO carreras (nombre, descripcion, estado, fecha_creacion)
VALUES ('Ingeniería Civil', 'EP Ingeniería Civil', 1, CURRENT_TIMESTAMP);
INSERT INTO carreras (nombre, descripcion, estado, fecha_creacion)
VALUES ('Arquitectura', 'EP Arquitectura y Urbanismo', 1, CURRENT_TIMESTAMP);

INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS401', 'Lenguaje de Programación II', 4, 4, 30, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS402', 'Base de Datos II', 4, 4, 25, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS403', 'Ingeniería de Requisitos', 4, 4, 2, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS501', 'Redes de Computadoras', 3, 5, 8, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS502', 'Sistemas Operativos', 1, 5, 0, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IS503', 'Arquitectura de Software', 6, 5, 10, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';

INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IC401', 'Mecánica de Materiales', 4, 4, 20, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería Civil';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IC402', 'Topografía', 4, 4, 15, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería Civil';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'IC403', 'Hidráulica', 5, 5, 0, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería Civil';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'AR401', 'Diseño Arquitectónico IV', 5, 4, 16, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Arquitectura';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'AR402', 'Urbanismo I', 4, 4, 12, 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Arquitectura';
INSERT INTO cursos (codigo, nombre, creditos, ciclo, vacantes, estado, carrera_id, fecha_creacion)
SELECT 'AR403', 'Construcción II', 4, 5, 10, 0, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Arquitectura';

INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410001', '71234567', 'Ana Lucía', 'Quispe Mamani', 'ana.quispe@upeu.edu.pe', 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410002', '72345678', 'Jorge Luis', 'Condori Apaza', 'jorge.condori@upeu.edu.pe', 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410003', '73456789', 'Carlos Alberto', 'Mamani Flores', 'carlos.mamani@upeu.edu.pe', 0, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería de Sistemas';
INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410004', '74567890', 'María Elena', 'Huamán Torres', 'maria.huaman@upeu.edu.pe', 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería Civil';
INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410005', '75678901', 'Pedro José', 'Ramos Soto', 'pedro.ramos@upeu.edu.pe', 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Ingeniería Civil';
INSERT INTO estudiantes (codigo, dni, nombres, apellidos, email, estado, carrera_id, fecha_creacion)
SELECT '202410006', '76789012', 'Lucía Isabel', 'Flores Rojas', 'lucia.flores@upeu.edu.pe', 1, id, CURRENT_TIMESTAMP
FROM carreras WHERE nombre = 'Arquitectura';

COMMIT;
