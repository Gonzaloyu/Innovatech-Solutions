package com.innovatech.backend_analitico.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "horas_asignadas", nullable = false)
    private Integer horasAsignadas;

    @Column(name = "estado")
    private String estado = "Asignado"; // Asignado, En Ejecución, Finalizado

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "herramientas_utilizadas", columnDefinition = "TEXT")
    private String herramientasUtilizadas; // Guardará la lista formateada

    @Column(name = "costo_herramientas", precision = 10, scale = 2)
    private BigDecimal costoHerramientas = BigDecimal.ZERO;

    @Column(name = "costo_total_calculado", precision = 12, scale = 2)
    private BigDecimal costoTotalCalculado = BigDecimal.ZERO;
}