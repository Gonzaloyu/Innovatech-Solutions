package com.innovatech.backend_analitico.controller;

import com.innovatech.backend_analitico.model.Empleado;
import com.innovatech.backend_analitico.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Obtener todos los empleados 
    @GetMapping
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    // Guardar un nuevo empleado en PostgreSQL
    @PostMapping
    public Empleado crearEmpleado(@RequestBody Empleado empleado) {
        return empleadoRepository.save(empleado);
    }
}