package pe.edu.upeu.eduandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.eduandes.dto.request.EstudianteRequestDTO;
import pe.edu.upeu.eduandes.dto.response.EstudianteResponseDTO;
import pe.edu.upeu.eduandes.service.service.EstudianteService;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(
            EstudianteService estudianteService) {

        this.estudianteService = estudianteService;
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> create(
            @Valid
            @RequestBody EstudianteRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(estudianteService.create(request));
    }

    @GetMapping
    public ResponseEntity<Iterable<EstudianteResponseDTO>> readAll() {

        return ResponseEntity.ok(
                estudianteService.readAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> read(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                estudianteService.read(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> update(
            @PathVariable Long id,
            @Valid
            @RequestBody EstudianteRequestDTO request) {

        return ResponseEntity.ok(
                estudianteService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        estudianteService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}