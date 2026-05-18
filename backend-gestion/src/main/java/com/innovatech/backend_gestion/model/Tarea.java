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

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    //llama a empleado de backend-analitico
    private Long empleadoId;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoProyecto estado;
}