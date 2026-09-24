package pe.edu.upeu.eduandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.eduandes.repository.MatriculaRepository;
import pe.edu.upeu.eduandes.service.service.ReporteService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public List<MatriculadosPorCursoDTO> matriculadosPorCurso(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        LocalDateTime desdeFechaHora = desde == null ? null : desde.atStartOfDay();
        LocalDateTime hastaFechaHora = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        long inicio = System.currentTimeMillis();

        log.info("Inicio reporte de matriculados por curso | desde={} | hasta={}", desde, hasta);

        List<MatriculadosPorCursoDTO> resultado =
                matriculaRepository.reporteMatriculadosPorCurso(desdeFechaHora, hastaFechaHora);

        log.info("Fin reporte de matriculados por curso | desde={} | hasta={} | filas={} | duracionMs={}",
                desde, hasta, resultado.size(), System.currentTimeMillis() - inicio);
        return resultado;
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "El rango de fechas es inválido: 'desde' (" + desde
                            + ") es posterior a 'hasta' (" + hasta + ")");
        }
    }
}
