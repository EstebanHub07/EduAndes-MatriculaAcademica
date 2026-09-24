package pe.edu.upeu.eduandes.service.impl;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.request.CursoRequestDTO;
import pe.edu.upeu.eduandes.dto.response.CursoResponseDTO;
import pe.edu.upeu.eduandes.entity.Carrera;
import pe.edu.upeu.eduandes.entity.Curso;
import pe.edu.upeu.eduandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CarreraRepository;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.service.service.CursoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CursoServiceImpl implements CursoService {

    private static final Set<String> CAMPOS_ORDEN =
            Set.of("nombre", "creditos", "vacantes");

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;

    @Override
    public CursoResponseDTO create(CursoRequestDTO request) {
        String codigo = normalizarCodigo(request.getCodigo());

        if (cursoRepository.countByCodigoIgnoreCase(codigo) > 0) {
            log.warn("Intento de registrar curso con código duplicado: {}", codigo);
            throw new ReglaNegocioException(
                    "Ya existe un curso con el código indicado"
            );
        }

        Carrera carrera = buscarCarrera(request.getCarreraId());
        Curso curso = new Curso();
        copiarRequest(request, curso, carrera, codigo);

        Curso guardado = cursoRepository.save(curso);
        log.info("Curso registrado con id {}", guardado.getId());

        return convertirResponse(guardado);
    }

    @Override
    public CursoResponseDTO update(Long id, CursoRequestDTO request) {
        Curso curso = buscarEntidad(id);
        String codigo = normalizarCodigo(request.getCodigo());

        if (cursoRepository.countByCodigoIgnoreCaseAndIdNot(codigo, id) > 0) {
            log.warn("Intento de actualizar curso con código duplicado: {}", codigo);
            throw new ReglaNegocioException(
                    "Ya existe otro curso con el código indicado"
            );
        }

        Carrera carrera = buscarCarrera(request.getCarreraId());
        copiarRequest(request, curso, carrera, codigo);

        Curso actualizado = cursoRepository.save(curso);
        log.info("Curso actualizado con id {}", id);

        return convertirResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO read(Long id) {
        return convertirResponse(buscarEntidad(id));
    }

    @Override
    public void delete(Long id) {
        Curso curso = buscarEntidad(id);

        if (cursoRepository.contarMatriculasPorCurso(id) > 0) {
            log.warn("No se puede eliminar el curso {} porque tiene matrículas", id);
            throw new ReglaNegocioException(
                    "No se puede eliminar un curso que tiene matrículas"
            );
        }

        cursoRepository.delete(curso);
        log.info("Curso eliminado con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> readAll() {
        return cursoRepository
                .findAll(Sort.by("nombre").ascending())
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarPorCarrera(Long carreraId) {
        buscarCarrera(carreraId);

        return cursoRepository
                .findByCarreraIdOrderByNombreAsc(carreraId)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscar(
            String nombre,
            Long carreraId,
            Integer ciclo,
            Boolean conVacantes,
            String orden,
            String direccion
    ) {
        String campoOrden = validarCampoOrden(orden);
        boolean descendente = validarDireccion(direccion);

        Specification<Curso> filtros = (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();

            if (nombre != null && !nombre.isBlank()) {
                condiciones.add(
                        cb.like(
                                cb.lower(root.get("nombre")),
                                "%" + nombre.trim().toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (carreraId != null) {
                condiciones.add(
                        cb.equal(root.get("carrera").get("id"), carreraId)
                );
            }

            if (ciclo != null) {
                condiciones.add(
                        cb.equal(root.get("ciclo"), ciclo.toString())
                );
            }

            if (conVacantes != null) {
                Expression<Integer> vacantes = root.get("vacantes").as(Integer.class);
                condiciones.add(
                        conVacantes
                                ? cb.greaterThan(vacantes, 0)
                                : cb.equal(vacantes, 0)
                );
            }

            Expression<?> expresionOrden = campoOrden.equals("nombre")
                    ? root.get(campoOrden)
                    : root.get(campoOrden).as(Integer.class);

            query.orderBy(
                    descendente
                            ? cb.desc(expresionOrden)
                            : cb.asc(expresionOrden)
            );

            return cb.and(condiciones.toArray(Predicate[]::new));
        };

        return cursoRepository.findAll(filtros)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private void copiarRequest(
            CursoRequestDTO request,
            Curso curso,
            Carrera carrera,
            String codigo
    ) {
        curso.setCodigo(codigo);
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos().toString());
        curso.setCiclo(request.getCiclo().toString());
        curso.setVacantes(request.getVacantes().toString());
        curso.setCarrera(carrera);

        if (request.getEstado() != null) {
            curso.setEstado(request.getEstado());
        }
    }

    private Curso buscarEntidad(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el curso con id " + id
                ));
    }

    private Carrera buscarCarrera(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la carrera con id " + id
                ));
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private String validarCampoOrden(String orden) {
        String campo = orden == null
                ? "nombre"
                : orden.toLowerCase(Locale.ROOT);

        if (!CAMPOS_ORDEN.contains(campo)) {
            throw new IllegalArgumentException(
                    "El orden debe ser nombre, creditos o vacantes"
            );
        }

        return campo;
    }

    private boolean validarDireccion(String direccion) {
        String valor = direccion == null
                ? "asc"
                : direccion.toLowerCase(Locale.ROOT);

        return switch (valor) {
            case "asc" -> false;
            case "desc" -> true;
            default -> throw new IllegalArgumentException(
                    "La dirección debe ser asc o desc"
            );
        };
    }

    private CursoResponseDTO convertirResponse(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getCodigo(),
                curso.getNombre(),
                Integer.valueOf(curso.getCreditos()),
                Integer.valueOf(curso.getCiclo()),
                Integer.valueOf(curso.getVacantes()),
                curso.getEstado(),
                curso.getCarrera().getId(),
                curso.getCarrera().getNombre(),
                curso.getFechaCreacion(),
                curso.getFechaModificacion()
        );
    }
}
