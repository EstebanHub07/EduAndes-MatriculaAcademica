package pe.edu.upeu.eduandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.eduandes.repository.MatriculaRepository;
import pe.edu.upeu.eduandes.service.service.ReporteService;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {
    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final MatriculaRepository matriculaRepository;

    public ReporteServiceImpl(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculadosPorCursoDTO> matriculadosPorCurso(String periodo, Long carreraId) {
        if (periodo == null || !periodo.matches("^\\d{4}-[12]$")) {
            throw new IllegalArgumentException("El periodo debe tener el formato YYYY-1 o YYYY-2");
        }
        long inicio = System.currentTimeMillis();

        log.info("Inicio reporte de matriculados por curso | periodo={} | carreraId={}",
                periodo, carreraId);

        List<MatriculadosPorCursoDTO> resultado =
                matriculaRepository.reporteMatriculadosPorCurso(periodo, carreraId);

        log.info("Fin reporte de matriculados por curso | periodo={} | carreraId={} | filas={} | duracionMs={}",
                periodo, carreraId, resultado.size(), System.currentTimeMillis() - inicio);
        return resultado;
    }
}
