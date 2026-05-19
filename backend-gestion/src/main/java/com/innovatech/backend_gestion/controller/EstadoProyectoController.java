package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.EstadoProyecto;
import com.innovatech.backend_gestion.repository.EstadoProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = "http://localhost:3000")
public class EstadoProyectoController {

    @Autowired
    private EstadoProyectoRepository estadoRepository;

    @GetMapping
    public ResponseEntity<List<EstadoProyecto>> obtenerTodos() {
        return ResponseEntity.ok(estadoRepository.findAll());
    }
}