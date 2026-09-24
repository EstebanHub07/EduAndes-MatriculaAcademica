package pe.edu.upeu.eduandes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @Column(nullable = false,length = 6)
    @Size(min = 1, max = 6)
    private String creditos;
    @Column(nullable = false, length = 10)
    @Size(min = 1, max = 10)
    private String ciclo;
    @Column(nullable = false)
    @Size(min = 0)
    private String vacantes;
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
