package pe.edu.upeu.eduandes.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaRequestDTO {

    @NotBlank(message = "El periodo es obligatorio")
    @Pattern(regexp = "^\\d{4}-[12]$", message = "El periodo debe tener el formato YYYY-1 o YYYY-2 (ej. 2026-2)")
    private String periodo;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotEmpty(message = "Debe registrar al menos un curso en la matrícula")
    private List<DetalleMatriculaRequestDTO> detalles;
}