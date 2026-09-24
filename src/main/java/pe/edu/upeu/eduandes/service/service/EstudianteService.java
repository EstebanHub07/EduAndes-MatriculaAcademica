package pe.edu.upeu.eduandes.service.service;

import pe.edu.upeu.eduandes.dto.request.EstudianteRequestDTO;
import pe.edu.upeu.eduandes.dto.response.EstudianteResponseDTO;
import pe.edu.upeu.eduandes.service.generic.CrudService;

public interface EstudianteService extends CrudService<EstudianteRequestDTO, EstudianteResponseDTO, Long> {
}
