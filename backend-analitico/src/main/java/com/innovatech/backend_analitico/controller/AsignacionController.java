package com.innovatech.backend_analitico.controller;

import com.innovatech.backend_analitico.model.Asignacion;
import com.innovatech.backend_analitico.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "http://localhost:3000")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<Asignacion> crearAsignacion(@RequestBody Asignacion asignacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
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
}