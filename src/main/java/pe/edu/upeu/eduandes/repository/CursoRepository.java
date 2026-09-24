package pe.edu.upeu.eduandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.eduandes.entity.Curso;

import java.util.List;

public interface CursoRepository
        extends JpaRepository<Curso, Long>,
        JpaSpecificationExecutor<Curso> {

    long countByCodigoIgnoreCase(String codigo);

    long countByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    long countByCarreraId(Long carreraId);

    List<Curso> findByCarreraIdOrderByNombreAsc(Long carreraId);

    @Query("""
        SELECT COUNT(d)
        FROM DetalleMatricula d
        WHERE d.curso.id = :cursoId
        """)
    long contarMatriculasPorCurso(@Param("cursoId") Long cursoId);
}