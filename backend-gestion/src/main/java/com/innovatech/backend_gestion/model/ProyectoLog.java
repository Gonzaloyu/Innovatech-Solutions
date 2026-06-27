package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "proyecto_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hora;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private String fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore  
    private Proyecto proyecto;
}