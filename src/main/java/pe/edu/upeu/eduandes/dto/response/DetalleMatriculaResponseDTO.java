package pe.edu.upeu.eduandes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatriculaResponseDTO {

    private Long id;
    private Long cursoId;
    private String cursoNombre;
    private String cursoCodigo;
    private Integer creditos;
    private BigDecimal costo;
}