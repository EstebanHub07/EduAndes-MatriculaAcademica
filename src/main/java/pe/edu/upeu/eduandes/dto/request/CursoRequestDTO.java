package pe.edu.upeu.eduandes.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}\\d{3}$")
    private String codigo;

    @NotBlank
    @Size(min = 3, max = 150)
    private String nombre;

    @NotNull
    @Min(1)
    @Max(6)
    private Integer creditos;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer ciclo;

    @NotNull
    @PositiveOrZero
    private Integer vacantes;

    private Boolean estado;

    @NotNull
    private Long carreraId;
}