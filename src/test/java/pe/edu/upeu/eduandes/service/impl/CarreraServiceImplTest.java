package pe.edu.upeu.eduandes.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.eduandes.entity.Carrera;
import pe.edu.upeu.eduandes.exception.ReglaNegocioException;
import pe.edu.upeu.eduandes.repository.CarreraRepository;
import pe.edu.upeu.eduandes.repository.CursoRepository;
import pe.edu.upeu.eduandes.repository.EstudianteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarreraServiceImplTest {

    @Mock
    private CarreraRepository carreraRepository;
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private EstudianteRepository estudianteRepository;

    @Test
    void noEliminaCarreraQueTieneEstudiantes() {
        Carrera carrera = new Carrera();
        carrera.setId(1L);

        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(cursoRepository.countByCarreraId(1L)).thenReturn(0L);
        when(estudianteRepository.existsByCarreraId(1L)).thenReturn(true);

        CarreraServiceImpl service = new CarreraServiceImpl(
                carreraRepository,
                cursoRepository,
                estudianteRepository);

        assertThrows(ReglaNegocioException.class, () -> service.delete(1L));
        verify(carreraRepository, never()).delete(carrera);
    }
}
