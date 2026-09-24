package pe.edu.upeu.eduandes.service.service;

import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    @Transactional(readOnly = true)
    List<MatriculadosPorCursoDTO> matriculadosPorCurso(
            LocalDate desde,
            LocalDate hasta);
}
