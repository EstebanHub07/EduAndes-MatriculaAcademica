package pe.edu.upeu.eduandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.eduandes.entity.Matricula;

import java.time.LocalDateTime;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @Query("""
            select new pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO(
                c.id,
                c.codigo,
                c.nombre,
                count(distinct m.id))
            from Matricula m
            join m.detalles d
            join d.curso c
            where m.estado = pe.edu.upeu.eduandes.enums.EstadoMatricula.REGISTRADA
              and (:desde is null or m.fecha >= :desde)
              and (:hasta is null or m.fecha <= :hasta)
            group by c.id, c.codigo, c.nombre
            order by count(distinct m.id) desc, c.nombre asc
            """)
    List<MatriculadosPorCursoDTO> reporteMatriculadosPorCurso(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
