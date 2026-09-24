package pe.edu.upeu.eduandes.dto.reporte;

public record MatriculadosPorCursoDTO(
        Long cursoId,
        String codigo,
        String curso,
        Long matriculados
) {
}
