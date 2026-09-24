package pe.edu.upeu.eduandes.service.service;

import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;

import java.time.LocalDate;
import java.util.List;

public interface MatriculaService  {
    MatriculaResponseDTO registrar(MatriculaRequestDTO request);
    MatriculaResponseDTO buscar(Long id);
    List<MatriculaResponseDTO> listar();
    List<MatriculaResponseDTO> buscarMatriculas(
            Long estudianteId,
            EstadoMatricula estado,
            LocalDate desde,
            LocalDate hasta,
            String ordenarPor,
            String direccion);
}