package pe.edu.upeu.eduandes.service.service;

import pe.edu.upeu.eduandes.dto.request.CursoRequestDTO;
import pe.edu.upeu.eduandes.dto.response.CursoResponseDTO;
import pe.edu.upeu.eduandes.service.generic.CrudService;

import java.util.List;

public interface CursoService extends CrudService<CursoRequestDTO, CursoResponseDTO, Long> {

    List<CursoResponseDTO> listarPorCarrera(Long carreraId);

    List<CursoResponseDTO> buscar(
            String nombre,
            Long carreraId,
            Integer ciclo,
            Boolean conVacantes,
            String orden,
            String direccion
    );
}