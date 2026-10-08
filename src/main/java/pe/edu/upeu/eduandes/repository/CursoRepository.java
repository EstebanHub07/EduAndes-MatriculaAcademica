package pe.edu.upeu.eduandes.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.eduandes.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository
        extends JpaRepository<Curso, Long>,
        JpaSpecificationExecutor<Curso> {

    long countByCodigoIgnoreCase(String codigo);

    long countByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    long countByCarreraId(Long carreraId);

    List<Curso> findByCarreraIdOrderByNombreAsc(Long carreraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Curso c WHERE c.id = :id")
    Optional<Curso> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        SELECT COUNT(d)
        FROM DetalleMatricula d
        WHERE d.curso.id = :cursoId
        """)
    long contarMatriculasPorCurso(@Param("cursoId") Long cursoId);
}
