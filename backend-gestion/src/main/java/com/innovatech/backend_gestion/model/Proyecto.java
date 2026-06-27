package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List; 
import java.util.Set;

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
    
    @Column(name = "nombre_equipo")
    private String nombreEquipo;


    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoProyecto estado;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "empleado_id")
    private Long empleadoId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "proyecto_id", insertable = false, updatable = false)
    @JsonIgnoreProperties("proyecto")
    private Set<Asignacion> asignaciones;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "proyecto_id", insertable = false, updatable = false)
    @JsonIgnoreProperties("proyecto")
    private Set<Tarea> tareas;
    
    // historial
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("proyecto")
    @OrderBy("id DESC")
    private List<ProyectoLog> logs;
}