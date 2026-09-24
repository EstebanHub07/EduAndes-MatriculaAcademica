package pe.edu.upeu.eduandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.request.CarreraRequestDTO;
import pe.edu.upeu.eduandes.dto.response.CarreraResponseDTO;
import pe.edu.upeu.eduandes.entity.Carrera;
import pe.edu.upeu.eduandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CarreraRepository;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.service.service.CarreraService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> readAll() {
        return carreraRepository
                .findAll(Sort.by("nombre").ascending())
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO read(Long id) {
        return convertirResponse(buscarEntidad(id));
    }

    @Override
    public CarreraResponseDTO create(CarreraRequestDTO request) {
        String nombre = normalizarNombre(request.getNombre());

        if (carreraRepository.contarPorNombreNormalizado(nombre) > 0) {
            log.warn("Intento de registrar carrera duplicada: {}", nombre);
            throw new ReglaNegocioException(
                    "Ya existe una carrera con el nombre indicado"
            );
        }

        Carrera carrera = new Carrera();
        carrera.setNombre(nombre);
        carrera.setDescripcion(request.getDescripcion());
        carrera.setEstado(request.getEstado());

        Carrera guardada = carreraRepository.save(carrera);

        log.info("Carrera registrada con id {}", guardada.getId());

        return convertirResponse(guardada);
    }

    @Override
    public CarreraResponseDTO update(
            Long id,
            CarreraRequestDTO request
    ) {
        Carrera carrera = buscarEntidad(id);
        String nombre = normalizarNombre(request.getNombre());

        if (carreraRepository
                .contarPorNombreNormalizadoExcluyendoId(nombre, id) > 0) {
            log.warn("Intento de actualizar carrera con nombre duplicado: {}", nombre);
            throw new ReglaNegocioException(
                    "Ya existe otra carrera con el nombre indicado"
            );
        }

        carrera.setNombre(nombre);
        carrera.setDescripcion(request.getDescripcion());

        if (request.getEstado() != null) {
            carrera.setEstado(request.getEstado());
        }

        Carrera actualizada = carreraRepository.save(carrera);

        log.info("Carrera actualizada con id {}", id);

        return convertirResponse(actualizada);
    }

    @Override
    public void delete(Long id) {
        Carrera carrera = buscarEntidad(id);

        if (cursoRepository.countByCarreraId(id) > 0) {
            log.warn("No se puede eliminar la carrera {} porque tiene cursos", id);
            throw new ReglaNegocioException(
                    "No se puede eliminar una carrera que tiene cursos"
            );
        }

        carreraRepository.delete(carrera);
        log.info("Carrera eliminada con id {}", id);
    }

    private Carrera buscarEntidad(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la carrera con id " + id
                ));
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    private CarreraResponseDTO convertirResponse(Carrera carrera) {
        return new CarreraResponseDTO(
                carrera.getId(),
                carrera.getNombre(),
                carrera.getDescripcion(),
                carrera.getEstado(),
                carrera.getFechaCreacion(),
                carrera.getFechaModificacion()
        );
    }
}
