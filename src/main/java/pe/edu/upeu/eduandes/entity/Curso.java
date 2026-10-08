package pe.edu.upeu.eduandes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cursos")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$")
    private String codigo;
    @Column(nullable = false, length = 150)
    @Size(min = 3, max = 150)
    private String nombre;
    @Column(nullable = false)
    @Min(1)
    @Max(6)
    private Integer creditos;
    @Column(nullable = false)
    @Min(1)
    @Max(10)
    private Integer ciclo;
    @Column(nullable = false)
    @PositiveOrZero
    private Integer vacantes;
    @Column(nullable = false)
    private Boolean estado;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;


    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if(estado == null) {
            estado = true;
        }
    }

    @PreUpdate
    public void preUpdate () {
        this.fechaModificacion = LocalDateTime.now();
    }
}
