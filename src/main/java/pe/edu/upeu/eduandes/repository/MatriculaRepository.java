package pe.edu.upeu.eduandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.eduandes.entity.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
}
