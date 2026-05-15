package com.innovatech.backend_analitico.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "asignaciones")
@Data
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    // Solo guardamos el ID, sin FK real (microservicio separado)
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "horas_asignadas", nullable = false)
    private Integer horasAsignadas;
}