package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.Asignacion;
import com.innovatech.backend_gestion.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "*")
public class AsignacionController {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @PostMapping
    public ResponseEntity<Asignacion> crearAsignacion(@RequestBody Asignacion asignacion) {
        Asignacion nuevaAsignacion = asignacionRepository.save(asignacion);
        return ResponseEntity.ok(nuevaAsignacion);
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Asignacion>> obtenerPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(asignacionRepository.findByProyectoId(proyectoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        asignacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}