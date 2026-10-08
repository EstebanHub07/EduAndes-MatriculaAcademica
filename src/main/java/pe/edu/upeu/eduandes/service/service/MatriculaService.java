package pe.edu.upeu.eduandes.service.service;

import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import java.util.List;

public interface MatriculaService  {
    MatriculaResponseDTO registrar(MatriculaRequestDTO request);
    MatriculaResponseDTO buscar(Long id);
    MatriculaResponseDTO anular(Long id);
    List<MatriculaResponseDTO> listar();
}
