package pe.edu.upeu.eduandes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarreraRequestDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String nombre;

    @Size(max = 200)
    private String descripcion;

    private Boolean estado;
}