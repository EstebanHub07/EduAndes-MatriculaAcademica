package pe.edu.upeu.eduandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.eduandes.entity.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    boolean existsByDni(String dni);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDniAndIdNot(String dni, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
