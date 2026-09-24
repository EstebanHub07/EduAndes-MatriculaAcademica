package pe.edu.upeu.eduandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.request.DetalleMatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.DetalleMatriculaResponseDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import pe.edu.upeu.eduandes.entity.Curso;
import pe.edu.upeu.eduandes.entity.DetalleMatricula;
import pe.edu.upeu.eduandes.entity.Estudiante;
import pe.edu.upeu.eduandes.entity.Matricula;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.repository.EstudianteRepository;
import pe.edu.upeu.eduandes.repository.MatriculaRepository;
import pe.edu.upeu.eduandes.service.service.MatriculaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class MatriculaServiceImpl implements MatriculaService {
    private static final Logger log = LoggerFactory.getLogger(MatriculaServiceImpl.class);
    private static final Set<String> CAMPOS_ORDENABLES = Set.of(
            "id", "fecha", "periodo", "estado", "totalCreditos", "montoTotal");
    private static final String ORDEN_POR_DEFECTO = "fecha";

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public MatriculaServiceImpl(
            MatriculaRepository matriculaRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    @Transactional
    public MatriculaResponseDTO registrar(MatriculaRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de matrícula es obligatoria");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un curso en la matrícula");
        }

        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Estudiante no encontrado con id: " + request.getEstudianteId()));
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw new IllegalArgumentException("No se puede matricular a un estudiante inactivo");
        }

        Matricula matricula = new Matricula();
        matricula.setPeriodo(request.getPeriodo());
        matricula.setEstudiante(estudiante);
        matricula.setFecha(LocalDateTime.now());
        matricula.setEstado(EstadoMatricula.REGISTRADA);

        Set<Long> cursosIncluidos = new HashSet<>();
        int totalCreditos = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (DetalleMatriculaRequestDTO item : request.getDetalles()) {
            if (item == null || item.getCursoId() == null) {
                throw new IllegalArgumentException("Cada detalle debe incluir un curso");
            }
            if (!cursosIncluidos.add(item.getCursoId())) {
                throw new IllegalArgumentException("El curso " + item.getCursoId()
                        + " está repetido en la matrícula");
            }

            Curso curso = cursoRepository.findById(item.getCursoId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Curso no encontrado con id: " + item.getCursoId()));
            if (!Boolean.TRUE.equals(curso.getEstado())) {
                throw new IllegalArgumentException("El curso " + curso.getNombre() + " está inactivo");
            }
            if (item.getCreditos() == null || item.getCreditos() <= 0) {
                throw new IllegalArgumentException("Los créditos del curso deben ser mayores que cero");
            }
            if (item.getCosto() == null || item.getCosto().signum() < 0) {
                throw new IllegalArgumentException("El costo del curso no puede ser negativo");
            }

            int vacantesDisponibles;
            try {
                vacantesDisponibles = Integer.parseInt(curso.getVacantes());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Las vacantes del curso " + curso.getNombre()
                        + " no tienen un valor válido", ex);
            }
            if (vacantesDisponibles <= 0) {
                throw new IllegalArgumentException("El curso " + curso.getNombre() + " no tiene vacantes disponibles");
            }

            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(item.getCreditos());
            detalle.setCosto(item.getCosto());
            matricula.agregarDetalle(detalle);

            curso.setVacantes(Integer.toString(vacantesDisponibles - 1));
            totalCreditos = Math.addExact(totalCreditos, item.getCreditos());
            montoTotal = montoTotal.add(item.getCosto());
        }

        matricula.setTotalCreditos(totalCreditos);
        matricula.setMontoTotal(montoTotal);

        return convertirResponse(matriculaRepository.save(matricula));
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO buscar(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Matrícula no encontrada con id: " + id));
        return convertirResponse(matricula);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listar() {
        return matriculaRepository.findAll(Sort.by(Sort.Direction.DESC, ORDEN_POR_DEFECTO))
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> buscarMatriculas(
            Long estudianteId,
            EstadoMatricula estado,
            LocalDate desde,
            LocalDate hasta,
            String ordenarPor,
            String direccion) {
        long inicio = System.currentTimeMillis();
        log.info("Inicio buscar matrículas | estudianteId={} | estado={} | desde={} | hasta={}",
                estudianteId, estado, desde, hasta);

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas es inválido: 'desde' es posterior a 'hasta'");
        }

        String campo = (ordenarPor == null || ordenarPor.isBlank())
                ? ORDEN_POR_DEFECTO : ordenarPor.trim();
        if (!CAMPOS_ORDENABLES.contains(campo)) {
            throw new IllegalArgumentException("Campo de ordenamiento no permitido: " + campo
                    + ". Campos válidos: " + CAMPOS_ORDENABLES);
        }
        String sentido = (direccion == null || direccion.isBlank()) ? "desc" : direccion.trim();
        if (!sentido.equalsIgnoreCase("asc") && !sentido.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Dirección de ordenamiento no permitida: " + sentido
                    + ". Valores válidos: asc, desc");
        }

        LocalDateTime desdeHora = desde == null ? null : desde.atStartOfDay();
        LocalDateTime hastaHora = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        Sort sort = "asc".equalsIgnoreCase(sentido)
                ? Sort.by(campo).ascending() : Sort.by(campo).descending();

        List<MatriculaResponseDTO> resultado = matriculaRepository.findAll(sort).stream()
                .filter(m -> estudianteId == null || m.getEstudiante().getId().equals(estudianteId))
                .filter(m -> estado == null || m.getEstado() == estado)
                .filter(m -> desdeHora == null || !m.getFecha().isBefore(desdeHora))
                .filter(m -> hastaHora == null || !m.getFecha().isAfter(hastaHora))
                .map(this::convertirResponse)
                .toList();

        log.info("Fin buscar matrículas | filas={} | duracionMs={}",
                resultado.size(), System.currentTimeMillis() - inicio);
        return resultado;
    }

    private MatriculaResponseDTO convertirResponse(Matricula matricula) {
        List<DetalleMatriculaResponseDTO> detalles = matricula.getDetalles().stream()
                .map(detalle -> new DetalleMatriculaResponseDTO(
                        detalle.getId(),
                        detalle.getCurso().getId(),
                        detalle.getCurso().getNombre(),
                        detalle.getCurso().getCodigo(),
                        detalle.getCreditos(),
                        detalle.getCosto()))
                .toList();

        Estudiante estudiante = matricula.getEstudiante();
        return new MatriculaResponseDTO(
                matricula.getId(),
                matricula.getFecha(),
                matricula.getPeriodo(),
                estudiante.getId(),
                estudiante.getNombres() + " " + estudiante.getApellidos(),
                matricula.getEstado().name(),
                matricula.getTotalCreditos(),
                matricula.getMontoTotal(),
                detalles);
    }
}
