package pe.edu.upeu.eduandes.service.impl;

import org.springframework.beans.factory.annotation.Value;
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
import pe.edu.upeu.eduandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.repository.EstudianteRepository;
import pe.edu.upeu.eduandes.repository.MatriculaRepository;
import pe.edu.upeu.eduandes.service.service.MatriculaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatriculaServiceImpl implements MatriculaService {
    private static final String ORDEN_POR_DEFECTO = "fecha";

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final BigDecimal costoCredito;

    public MatriculaServiceImpl(
            MatriculaRepository matriculaRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            @Value("${matricula.costo-credito:120.00}") BigDecimal costoCredito) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.costoCredito = costoCredito;
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

        Estudiante estudiante = estudianteRepository.findByIdForUpdate(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + request.getEstudianteId()));
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw new ReglaNegocioException("No se puede matricular a un estudiante inactivo");
        }
        if (matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(
                estudiante.getId(), request.getPeriodo(), EstadoMatricula.REGISTRADA)) {
            throw new ReglaNegocioException(
                    "El estudiante ya tiene una matrícula registrada en el periodo " + request.getPeriodo());
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
                throw new ReglaNegocioException("El curso " + item.getCursoId()
                        + " está repetido en la matrícula");
            }

            Curso curso = cursoRepository.findByIdForUpdate(item.getCursoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Curso no encontrado con id: " + item.getCursoId()));
            if (!Boolean.TRUE.equals(curso.getEstado())) {
                throw new ReglaNegocioException("El curso " + curso.getNombre() + " está inactivo");
            }
            if (!curso.getCarrera().getId().equals(estudiante.getCarrera().getId())) {
                throw new ReglaNegocioException("El curso " + curso.getNombre()
                        + " no pertenece a la carrera del estudiante");
            }

            int creditos = curso.getCreditos();
            if (creditos <= 0) {
                throw new ReglaNegocioException("Los créditos del curso deben ser mayores que cero");
            }

            int vacantesDisponibles = curso.getVacantes();
            if (vacantesDisponibles <= 0) {
                throw new ReglaNegocioException(
                        "El curso " + curso.getNombre() + " no tiene vacantes disponibles");
            }

            totalCreditos = Math.addExact(totalCreditos, creditos);
            if (totalCreditos > 20) {
                throw new ReglaNegocioException("La matrícula no puede superar los 20 créditos");
            }
            BigDecimal costo = costoCredito
                    .multiply(BigDecimal.valueOf(creditos))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(creditos);
            detalle.setCosto(costo);
            matricula.agregarDetalle(detalle);

            curso.setVacantes(vacantesDisponibles - 1);
            montoTotal = montoTotal.add(costo);
        }

        matricula.setTotalCreditos(totalCreditos);
        matricula.setMontoTotal(montoTotal);

        return convertirResponse(matriculaRepository.save(matricula));
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO buscar(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Matrícula no encontrada con id: " + id));
        return convertirResponse(matricula);
    }

    @Override
    @Transactional
    public MatriculaResponseDTO anular(Long id) {
        Matricula matricula = matriculaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Matrícula no encontrada con id: " + id));
        if (matricula.getEstado() == EstadoMatricula.ANULADA) {
            throw new ReglaNegocioException("La matrícula ya está anulada");
        }

        for (DetalleMatricula detalle : matricula.getDetalles()) {
            Curso curso = detalle.getCurso();
            curso.setVacantes(curso.getVacantes() + 1);
        }

        matricula.setEstado(EstadoMatricula.ANULADA);
        return convertirResponse(matriculaRepository.save(matricula));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listar() {
        return matriculaRepository.findAll(Sort.by(Sort.Direction.DESC, ORDEN_POR_DEFECTO))
                .stream()
                .map(this::convertirResponse)
                .toList();
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
