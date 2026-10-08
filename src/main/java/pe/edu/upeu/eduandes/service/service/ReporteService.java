package pe.edu.upeu.eduandes.service.service;

import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;

import java.util.List;

public interface ReporteService {
    List<MatriculadosPorCursoDTO> matriculadosPorCurso(
            String periodo,
            Long carreraId);
}
