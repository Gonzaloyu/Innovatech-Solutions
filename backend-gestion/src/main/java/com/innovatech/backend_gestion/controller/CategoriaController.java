package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.Categoria;
import com.innovatech.backend_gestion.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodos() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }
}