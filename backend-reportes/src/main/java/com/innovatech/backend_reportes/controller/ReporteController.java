package com.innovatech.backend_reportes.controller;

import com.innovatech.backend_reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:3000")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> kpis() {
        return ResponseEntity.ok(reporteService.getResumenKpis());
    }

    @GetMapping("/empleados-por-departamento")
    public ResponseEntity<Map<String, Long>> empleadosPorDepartamento() {
        return ResponseEntity.ok(reporteService.getEmpleadosPorDepartamento());
    }

    @GetMapping("/proyectos-por-estado")
    public ResponseEntity<Map<String, Long>> proyectosPorEstado() {
        return ResponseEntity.ok(reporteService.getProyectosPorEstado());
    }

    @GetMapping("/proyectos-por-categoria")
    public ResponseEntity<Map<String, Long>> proyectosPorCategoria() {
        return ResponseEntity.ok(reporteService.getProyectosPorCategoria());
    }

    @GetMapping("/horas-por-proyecto")
    public ResponseEntity<Map<Long, Integer>> horasPorProyecto() {
        return ResponseEntity.ok(reporteService.getHorasPorProyecto());
    }
}