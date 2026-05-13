package com.innovatech.backend_analitico.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "empleados")
@Data
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String cargo;

    private String departamento;

    private Double salario;
}