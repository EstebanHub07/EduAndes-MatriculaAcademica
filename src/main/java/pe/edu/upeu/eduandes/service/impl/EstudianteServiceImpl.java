package pe.edu.upeu.eduandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.request.EstudianteRequestDTO;
import pe.edu.upeu.eduandes.dto.response.EstudianteResponseDTO;
import pe.edu.upeu.eduandes.entity.Carrera;
import pe.edu.upeu.eduandes.entity.Estudiante;
import pe.edu.upeu.eduandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CarreraRepository;
import pe.edu.upeu.eduandes.repository.EstudianteRepository;
import pe.edu.upeu.eduandes.service.service.EstudianteService;

import java.util.List;

@Service
public class EstudianteServiceImpl implements EstudianteService {
    private static final Logger log = LoggerFactory.getLogger(EstudianteServiceImpl.class);

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;

    public EstudianteServiceImpl(
            EstudianteRepository estudianteRepository,
            CarreraRepository carreraRepository) {
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
    }

    @Override
    @Transactional
    public EstudianteResponseDTO create(EstudianteRequestDTO request) {
        validarSolicitud(request);

        String codigo = request.getCodigo().trim();
        String dni = request.getDni().trim();
        String email = normalizarEmail(request.getEmail());

        if (estudianteRepository.existsByDni(dni)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el DNI: " + dni);
        }
        if (estudianteRepository.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el correo: " + email);
        }
        if (estudianteRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el código: " + codigo);
        }

        Carrera carrera = buscarCarreraActiva(request.getCarreraId());
        Estudiante estudiante = new Estudiante();
        estudiante.setCodigo(codigo);
        estudiante.setDni(dni);
        estudiante.setNombres(request.getNombres().trim());
        estudiante.setApellidos(request.getApellidos().trim());
        estudiante.setEmail(email);
        estudiante.setEstado(request.getEstado());
        estudiante.setCarrera(carrera);

        Estudiante guardado = estudianteRepository.save(estudiante);
        log.info("Estudiante registrado correctamente id={}", guardado.getId());
        return convertirResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO read(Long id) {
        return convertirResponse(buscarEstudiante(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> readAll() {
        return estudianteRepository.findAll().stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public EstudianteResponseDTO update(Long id, EstudianteRequestDTO request) {
        validarSolicitud(request);

        Estudiante estudiante = buscarEstudiante(id);
        String codigo = request.getCodigo().trim();
        String dni = request.getDni().trim();
        String email = normalizarEmail(request.getEmail());

        if (estudianteRepository.existsByDniAndIdNot(dni, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante con el DNI: " + dni);
        }
        if (estudianteRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante con el correo: " + email);
        }
        if (estudianteRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante con el código: " + codigo);
        }

        estudiante.setCodigo(codigo);
        estudiante.setDni(dni);
        estudiante.setNombres(request.getNombres().trim());
        estudiante.setApellidos(request.getApellidos().trim());
        estudiante.setEmail(email);
        estudiante.setEstado(request.getEstado());
        estudiante.setCarrera(buscarCarreraActiva(request.getCarreraId()));

        Estudiante actualizado = estudianteRepository.save(estudiante);
        log.info("Estudiante id={} actualizado correctamente", id);
        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = buscarEstudiante(id);
        estudianteRepository.delete(estudiante);
        log.info("Estudiante id={} eliminado correctamente", id);
    }

    private Estudiante buscarEstudiante(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + id));
    }

    private Carrera buscarCarreraActiva(Long carreraId) {
        Carrera carrera = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrera no encontrada con id: " + carreraId));
        if (!Boolean.TRUE.equals(carrera.getEstado())) {
            throw new ReglaNegocioException(
                    "No se puede asignar un estudiante a una carrera inactiva");
        }
        return carrera;
    }

    private void validarSolicitud(EstudianteRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud del estudiante es obligatoria");
        }
        if (request.getCodigo() == null || request.getCodigo().isBlank()
                || request.getDni() == null || request.getDni().isBlank()
                || request.getNombres() == null || request.getNombres().isBlank()
                || request.getApellidos() == null || request.getApellidos().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getEstado() == null || request.getCarreraId() == null) {
            throw new IllegalArgumentException("Todos los campos obligatorios del estudiante deben tener valor");
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private EstudianteResponseDTO convertirResponse(Estudiante estudiante) {
        Carrera carrera = estudiante.getCarrera();
        return new EstudianteResponseDTO(
                estudiante.getId(),
                estudiante.getCodigo(),
                estudiante.getDni(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getEmail(),
                estudiante.getEstado(),
                carrera.getId(),
                carrera.getNombre(),
                estudiante.getFechaCreacion(),
                estudiante.getFechaModificacion());
    }
}
