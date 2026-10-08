package pe.edu.upeu.eduandes.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.eduandes.entity.Matricula;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @Query("""
            select new pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO(
                c.codigo,
                c.nombre,
                count(distinct m.id),
                sum(d.costo))
            from Matricula m
            join m.detalles d
            join d.curso c
            where m.estado = pe.edu.upeu.eduandes.enums.EstadoMatricula.REGISTRADA
              and m.periodo = :periodo
              and (:carreraId is null or c.carrera.id = :carreraId)
            group by c.codigo, c.nombre
            order by c.nombre asc
            """)
    List<MatriculadosPorCursoDTO> reporteMatriculadosPorCurso(
            @Param("periodo") String periodo,
            @Param("carreraId") Long carreraId);

    boolean existsByEstudianteIdAndPeriodoAndEstado(
            Long estudianteId,
            String periodo,
            EstadoMatricula estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Matricula m WHERE m.id = :id")
    Optional<Matricula> findByIdForUpdate(@Param("id") Long id);
}
