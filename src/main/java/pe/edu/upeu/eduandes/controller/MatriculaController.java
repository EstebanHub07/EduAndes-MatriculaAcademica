package pe.edu.upeu.eduandes.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.eduandes.dto.request.MatriculaRequestDTO;
import pe.edu.upeu.eduandes.dto.response.MatriculaResponseDTO;
import pe.edu.upeu.eduandes.enums.EstadoMatricula;
import pe.edu.upeu.eduandes.service.service.MatriculaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matricula")
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

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> listar() {

        return ResponseEntity.ok(
                matriculaService.listar()
        );
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<MatriculaResponseDTO>> buscar(

            @RequestParam(required = false)
            Long estudianteId,

            @RequestParam(required = false)
            EstadoMatricula estado,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(required = false, defaultValue = "fecha")
            String ordenarPor,

            @RequestParam(required = false, defaultValue = "desc")
            String direccion) {

        return ResponseEntity.ok(
                matriculaService.buscarMatriculas(
                        estudianteId,
                        estado,
                        desde,
                        hasta,
                        ordenarPor,
                        direccion
                )
        );
    }
}