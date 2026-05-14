package com.innovatech.backend_gestion.controller;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import com.innovatech.backend_gestion.service.KafkaProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin("*")
public class ProyectoController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;
    // Obtener todos los proyectos 
    @GetMapping
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    // Guardar un nuevo proyecto en MySQL
    @PostMapping
    public ResponseEntity<Proyecto> crearProyecto(@RequestBody Proyecto proyecto) {
        Proyecto nuevoProyecto = proyectoRepository.save(proyecto);
        
        // Llama a kafka despues
        // de haber enviado a la bd
        kafkaProducerService.enviarMensajeProyectoCreado(nuevoProyecto.getNombre());
        
        return ResponseEntity.ok(nuevoProyecto);
    }
}