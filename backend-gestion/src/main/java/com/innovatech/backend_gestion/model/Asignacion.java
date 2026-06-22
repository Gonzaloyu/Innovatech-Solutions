package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "asignaciones")
@Data
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    private String rol;
}