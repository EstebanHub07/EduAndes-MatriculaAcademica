package pe.edu.upeu.eduandes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matriculas")
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,updatable = false)
    private LocalDateTime fecha;
    @Column(nullable = false,length = 6)
    @Pattern(regexp = "^\\d{4}-[12]$")
    private String periodo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id",nullable = false)
    private Estudiante estudiante;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado;
    @Column(nullable = false)
    private Integer totalCreditos;
    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal montoTotal;
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DetalleMatricula> detalles = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate () {
        this.fechaModificacion = LocalDateTime.now();
    }
}
