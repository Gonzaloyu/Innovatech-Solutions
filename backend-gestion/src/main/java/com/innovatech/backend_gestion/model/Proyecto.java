package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Un proyecto pertenece a UN cliente 
    //  (Muchos proyectos -> Un cliente)
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Un proyecto tiene UN estado 
    // (Muchos proyectos -> Un estado)
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoProyecto estado;

    // Un proyecto pertenece a UNA categoría 
    // (Muchos proyectos -> Una categoría)
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}