package pe.edu.upeu.eduandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.eduandes.dto.request.CursoRequestDTO;
import pe.edu.upeu.eduandes.dto.response.CursoResponseDTO;
import pe.edu.upeu.eduandes.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    @Operation(summary = "Listar cursos")
    public ResponseEntity<List<CursoResponseDTO>> listar() {
        return ResponseEntity.ok(cursoService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un curso")
    public ResponseEntity<CursoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.read(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar cursos mediante filtros")
    public ResponseEntity<List<CursoResponseDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long carreraId,
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) Boolean conVacantes,
            @RequestParam(defaultValue = "nombre") String orden,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        return ResponseEntity.ok(
                cursoService.buscar(
                        nombre,
                        carreraId,
                        ciclo,
                        conVacantes,
                        orden,
                        dir
                )
        );
    }

    @PostMapping
    @Operation(summary = "Registrar un curso")
    public ResponseEntity<CursoResponseDTO> registrar(
            @Valid @RequestBody CursoRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cursoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un curso")
    public ResponseEntity<CursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequestDTO request
    ) {
        return ResponseEntity.ok(cursoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un curso")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
