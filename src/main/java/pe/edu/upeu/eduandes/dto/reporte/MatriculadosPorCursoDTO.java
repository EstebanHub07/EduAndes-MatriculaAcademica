package pe.edu.upeu.eduandes.dto.reporte;

public record MatriculadosPorCursoDTO(
        Long cursoId,
        String cursoCodigo,
        String cursoNombre,
        Long totalMatriculados) {
}
