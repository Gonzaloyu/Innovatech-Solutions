package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.ProyectoLog;
import com.innovatech.backend_gestion.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<List<Proyecto>> obtenerTodos() {
        return ResponseEntity.ok(proyectoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Proyecto> crearProyecto(@RequestBody Proyecto proyecto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.crearProyecto(proyecto));
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Proyecto>> obtenerPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(proyectoService.obtenerPorEmpleado(empleadoId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Proyecto> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(proyectoService.actualizarEstado(id, body));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizarProyecto(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(proyectoService.actualizarProyecto(id, body));
    }
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<ProyectoLog>> obtenerLogs(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.obtenerLogs(id));
    }
    @PostMapping("/{id}/logs")
    public ResponseEntity<ProyectoLog> crearLog(
            @PathVariable Long id,
            @RequestBody ProyectoLog log) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.crearLog(id, log));
    }
}