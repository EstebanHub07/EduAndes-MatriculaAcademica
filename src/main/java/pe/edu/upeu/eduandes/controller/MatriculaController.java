package pe.edu.upeu.eduandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import pe.edu.upeu.eduandes.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(
            MatriculaService matriculaService) {

        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> registrar(
            @Valid
            @RequestBody MatriculaRequestDTO request) {

        MatriculaResponseDTO response =
                matriculaService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                matriculaService.buscar(id)
        );
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MatriculaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.anular(id));
    }

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> listar() {

        return ResponseEntity.ok(
                matriculaService.listar()
        );
    }
}
