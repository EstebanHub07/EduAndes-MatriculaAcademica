package pe.edu.upeu.eduandes.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.eduandes.dto.request.DetalleMatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import pe.edu.upeu.eduandes.entity.Carrera;
import pe.edu.upeu.eduandes.entity.Curso;
import pe.edu.upeu.eduandes.entity.Estudiante;
import pe.edu.upeu.eduandes.entity.Matricula;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.repository.EstudianteRepository;
import pe.edu.upeu.eduandes.repository.MatriculaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceImplTest {

    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private CursoRepository cursoRepository;

    @Test
    void calculaCostoYDescuentaVacanteAlRegistrar() {
        Carrera carrera = new Carrera();
        carrera.setId(1L);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Ana");
        estudiante.setApellidos("Pérez");
        estudiante.setEstado(true);
        estudiante.setCarrera(carrera);

        Curso curso = new Curso();
        curso.setId(20L);
        curso.setCodigo("LP201");
        curso.setNombre("Lenguaje de Programación");
        curso.setCreditos(3);
        curso.setVacantes(5);
        curso.setEstado(true);
        curso.setCarrera(carrera);

        when(estudianteRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(estudiante));
        when(matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(
                10L, "2026-1", EstadoMatricula.REGISTRADA)).thenReturn(false);
        when(cursoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(curso));
        when(matriculaRepository.save(any(Matricula.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MatriculaServiceImpl service = new MatriculaServiceImpl(
                matriculaRepository,
                estudianteRepository,
                cursoRepository,
                new BigDecimal("120.00"));

        MatriculaResponseDTO response = service.registrar(new MatriculaRequestDTO(
                "2026-1",
                10L,
                List.of(new DetalleMatriculaRequestDTO(20L))));

        assertEquals(3, response.getTotalCreditos());
        assertEquals(new BigDecimal("360.00"), response.getMontoTotal());
        assertEquals(4, curso.getVacantes());
    }

    @Test
    void rechazaMatriculaQueSuperaVeinteCreditos() {
        Carrera carrera = carrera(1L);
        Estudiante estudiante = estudiante(10L, carrera);
        Curso curso1 = curso(20L, "IS401", 6, 3, carrera);
        Curso curso2 = curso(21L, "IS402", 6, 3, carrera);
        Curso curso3 = curso(22L, "IS403", 6, 3, carrera);
        Curso curso4 = curso(23L, "IS404", 3, 3, carrera);

        when(estudianteRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(curso1));
        when(cursoRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(curso2));
        when(cursoRepository.findByIdForUpdate(22L)).thenReturn(Optional.of(curso3));
        when(cursoRepository.findByIdForUpdate(23L)).thenReturn(Optional.of(curso4));

        MatriculaServiceImpl service = service();
        MatriculaRequestDTO request = new MatriculaRequestDTO(
                "2026-1",
                10L,
                List.of(
                        new DetalleMatriculaRequestDTO(20L),
                        new DetalleMatriculaRequestDTO(21L),
                        new DetalleMatriculaRequestDTO(22L),
                        new DetalleMatriculaRequestDTO(23L)));

        assertThrows(ReglaNegocioException.class, () -> service.registrar(request));
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void rechazaCursoSinVacantes() {
        Carrera carrera = carrera(1L);
        Estudiante estudiante = estudiante(10L, carrera);
        Curso curso = curso(20L, "IS401", 4, 0, carrera);

        when(estudianteRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(curso));

        MatriculaServiceImpl service = service();
        MatriculaRequestDTO request = new MatriculaRequestDTO(
                "2026-1", 10L, List.of(new DetalleMatriculaRequestDTO(20L)));

        assertThrows(ReglaNegocioException.class, () -> service.registrar(request));
        verify(matriculaRepository, never()).save(any());
        assertEquals(0, curso.getVacantes());
    }

    private MatriculaServiceImpl service() {
        return new MatriculaServiceImpl(
                matriculaRepository,
                estudianteRepository,
                cursoRepository,
                new BigDecimal("120.00"));
    }

    private Carrera carrera(Long id) {
        Carrera carrera = new Carrera();
        carrera.setId(id);
        return carrera;
    }

    private Estudiante estudiante(Long id, Carrera carrera) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setNombres("Ana");
        estudiante.setApellidos("Pérez");
        estudiante.setEstado(true);
        estudiante.setCarrera(carrera);
        return estudiante;
    }

    private Curso curso(
            Long id,
            String codigo,
            int creditos,
            int vacantes,
            Carrera carrera
    ) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setCodigo(codigo);
        curso.setNombre(codigo);
        curso.setCreditos(creditos);
        curso.setVacantes(vacantes);
        curso.setEstado(true);
        curso.setCarrera(carrera);
        return curso;
    }
}
