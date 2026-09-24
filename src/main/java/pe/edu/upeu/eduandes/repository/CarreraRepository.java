package pe.edu.upeu.eduandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.eduandes.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    @Query("""
        SELECT COUNT(c)
        FROM Carrera c
        WHERE LOWER(REPLACE(c.nombre, ' ', '')) =
              LOWER(REPLACE(:nombre, ' ', ''))
        """)
    long contarPorNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
        SELECT COUNT(c)
        FROM Carrera c
        WHERE LOWER(REPLACE(c.nombre, ' ', '')) =
              LOWER(REPLACE(:nombre, ' ', ''))
          AND c.id <> :id
        """)
    long contarPorNombreNormalizadoExcluyendoId(
            @Param("nombre") String nombre,
            @Param("id") Long id
    );
}