package com.innovatech.backend_analitico.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    // Soft link al backend-gestion (MySQL)
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "horas_asignadas", nullable = false)
    private Integer horasAsignadas;
}