package pe.edu.upeu.eduandes.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.eduandes.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.eduandes.service.service.ReporteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(
            ReporteService reporteService) {

        this.reporteService = reporteService;
    }


    @GetMapping("/matriculados-por-curso")
    public ResponseEntity<List<MatriculadosPorCursoDTO>> matriculadosPorCurso(
            @RequestParam String periodo,
            @RequestParam(required = false) Long carreraId) {

        return ResponseEntity.ok(
                reporteService.matriculadosPorCurso(periodo, carreraId)
        );
    }
}
