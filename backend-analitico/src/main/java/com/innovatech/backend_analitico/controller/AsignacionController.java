package com.innovatech.backend_analitico.controller;

import com.innovatech.backend_analitico.model.Asignacion;
import com.innovatech.backend_analitico.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "http://localhost:3000")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping
    public ResponseEntity<List<Asignacion>> obtenerTodas() {
        return ResponseEntity.ok(asignacionService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<Asignacion> crearAsignacion(@RequestBody Asignacion asignacion) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(asignacionService.crearAsignacion(asignacion));
    }

    @GetMapping("/empleado/{id}")
    public ResponseEntity<List<Asignacion>> porEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorEmpleado(id));
    }

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<List<Asignacion>> porProyecto(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorProyecto(id));
    }

    // Recibe el reporte del empleado y actualiza las métricas financieras
    @PutMapping("/{id}/reportar")
    public ResponseEntity<Asignacion> reportarProgreso(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        
        String estado = (String) payload.get("estado");
        String comentario = (String) payload.get("comentario");
        String herramientas = (String) payload.get("herramientas");
        
        // Conversión segura de valores numéricos de JSON a BigDecimal
        Number costoRaw = (Number) payload.getOrDefault("costoHerramientas", 0);
        BigDecimal costoHerramientas = BigDecimal.valueOf(costoRaw.doubleValue());

        Asignacion asignacionActualizada = asignacionService.actualizarProgreso(
                id, estado, comentario, herramientas, costoHerramientas
        );
        
        return ResponseEntity.ok(asignacionActualizada);
    }
}