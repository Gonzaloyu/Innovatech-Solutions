package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "proyectos")
@Data 
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private LocalDate fechaInicio;

    private String estado; //"Activo, Finalizado, En Pausa"
}