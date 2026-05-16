package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estados_proyecto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // "En Planificación", "En Ejecución", "Finalizado"
}