package com.innovatech.backend_notificaciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mensaje;
    private LocalDateTime fechaEnvio;

    public Notificacion() {}
    public Notificacion(String mensaje) {
        this.mensaje = mensaje;
        this.fechaEnvio = LocalDateTime.now();
    }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
}