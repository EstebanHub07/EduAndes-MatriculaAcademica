package pe.edu.upeu.eduandes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carreras")
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100, unique = true)
    @Size(min = 3, max = 100)
    private String nombre;
    @Size(max = 200)
    private String descripcion;
    @Column(nullable = false)
    private Boolean estado;
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
