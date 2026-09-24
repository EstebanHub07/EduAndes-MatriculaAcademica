package pe.edu.upeu.eduandes.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatriculaRequestDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    private Integer creditos;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    private BigDecimal costo;
}