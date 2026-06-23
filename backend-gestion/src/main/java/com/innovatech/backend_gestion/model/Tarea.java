package com.innovatech.backend_gestion.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;


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
    @JsonIgnoreProperties({"tareas", "hibernateLazyInitializer", "handler"})
    private Proyecto proyecto;


    @Column(name = "asignacion_id")
    private Long asignacionId;


    @Column(name = "empleado_id")
    private Long empleadoId;


    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;


    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;


    @Column(length = 50)
    private String estado;
}
