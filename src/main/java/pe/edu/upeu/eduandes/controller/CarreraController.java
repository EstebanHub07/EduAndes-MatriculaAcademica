package pe.edu.upeu.eduandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.eduandes.dto.request.CarreraRequestDTO;
import pe.edu.upeu.eduandes.dto.response.CarreraResponseDTO;
import pe.edu.upeu.eduandes.dto.response.CursoResponseDTO;
import pe.edu.upeu.eduandes.service.service.CarreraService;
import pe.edu.upeu.eduandes.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carreras")
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    @GetMapping
    @Operation(summary = "Listar carreras")
    public ResponseEntity<List<CarreraResponseDTO>> listar() {
        return ResponseEntity.ok(carreraService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una carrera")
    public ResponseEntity<CarreraResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.read(id));
    }

    @GetMapping("/{id}/cursos")
    @Operation(summary = "Listar los cursos de una carrera")
    public ResponseEntity<List<CursoResponseDTO>> listarCursos(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(cursoService.listarPorCarrera(id));
    }

    @PostMapping
    @Operation(summary = "Registrar una carrera")
    public ResponseEntity<CarreraResponseDTO> registrar(
            @Valid @RequestBody CarreraRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carreraService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una carrera")
    public ResponseEntity<CarreraResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CarreraRequestDTO request
    ) {
        return ResponseEntity.ok(carreraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una carrera")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
