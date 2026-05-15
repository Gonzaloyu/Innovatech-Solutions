package com.innovatech.backend_notificaciones.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora_auditoria")
@Data
@NoArgsConstructor
public class BitacoraAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: "CREAR_PROYECTO", "ASIGNAR_EMPLEADO"
    @Column(nullable = false)
    private String accion;

    // Ej: "Proyecto", "Tarea", "Empleado"
    @Column(nullable = false)
    private String entidad;

    // Quién ejecutó la acción
    @Column(nullable = false)
    private String usuario;

    private LocalDateTime fecha;

    // JSON con detalles adicionales del evento
    @Column(columnDefinition = "TEXT")
    private String detalleJson;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    public BitacoraAuditoria(String accion, String entidad, 
                              String usuario, String detalleJson) {
        this.accion = accion;
        this.entidad = entidad;
        this.usuario = usuario;
        this.detalleJson = detalleJson;
    }
}