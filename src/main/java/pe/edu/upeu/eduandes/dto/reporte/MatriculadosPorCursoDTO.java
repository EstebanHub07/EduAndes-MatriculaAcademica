package pe.edu.upeu.eduandes.dto.reporte;

import java.math.BigDecimal;

public record MatriculadosPorCursoDTO(
        String codigo,
        String curso,
        Long matriculados,
        BigDecimal montoRecaudado
) {
}
