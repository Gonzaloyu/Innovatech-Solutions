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

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String entidad;


    @Column(nullable = false)
    private String usuario;

    private LocalDateTime fecha;

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