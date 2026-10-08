package pe.edu.upeu.eduandes.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatriculaRequestDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;
}
