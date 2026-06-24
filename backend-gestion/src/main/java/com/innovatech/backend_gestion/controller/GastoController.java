package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.Gasto;
import com.innovatech.backend_gestion.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*")
public class GastoController {

    @Autowired
    private GastoRepository gastoRepository;

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Gasto>> obtenerPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(gastoRepository.findByProyectoId(proyectoId));
    }

    @PostMapping
    public ResponseEntity<Gasto> crearGasto(@RequestBody Gasto gasto) {
        if(gasto.getFechaRegistro() == null) {
            gasto.setFechaRegistro(LocalDate.now());
        }
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }
}