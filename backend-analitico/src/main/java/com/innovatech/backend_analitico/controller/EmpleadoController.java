package com.innovatech.backend_analitico.controller;

import com.innovatech.backend_analitico.model.Empleado;
import com.innovatech.backend_analitico.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> obtenerTodos() {
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }

    @GetMapping("/departamento/{id}")
    public ResponseEntity<List<Empleado>> obtenerPorDepartamento(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerPorDepartamento(id));
    }

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado empleado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empleadoService.crearEmpleado(empleado));
    }
}