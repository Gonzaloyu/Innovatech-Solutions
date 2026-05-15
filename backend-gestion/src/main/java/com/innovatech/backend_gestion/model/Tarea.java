package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tareas")
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Una tarea pertenece a un Proyecto
    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;
    
    private Long empleadoId; 
}