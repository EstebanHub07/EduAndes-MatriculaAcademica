package pe.edu.upeu.eduandes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponseDTO {

    private Long id;
    private LocalDateTime fecha;
    private String periodo;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estado;
    private Integer totalCreditos;
    private BigDecimal montoTotal;
    private List<DetalleMatriculaResponseDTO> detalles;
}