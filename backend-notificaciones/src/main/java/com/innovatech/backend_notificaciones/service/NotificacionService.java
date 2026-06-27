package com.innovatech.backend_notificaciones.service;

import com.innovatech.backend_notificaciones.model.BitacoraAuditoria;
import com.innovatech.backend_notificaciones.model.Notificacion;
import com.innovatech.backend_notificaciones.repository.BitacoraAuditoriaRepository;
import com.innovatech.backend_notificaciones.repository.NotificacionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final BitacoraAuditoriaRepository bitacoraRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               BitacoraAuditoriaRepository bitacoraRepository) {
        this.notificacionRepository = notificacionRepository;
        this.bitacoraRepository = bitacoraRepository;
    }

    @KafkaListener(topics = "proyectos-topic", groupId = "grupo-notificaciones")
    public void escucharNuevoProyecto(String mensajeKafka) {
        System.out.println("[KAFKA] Mensaje recibido: " + mensajeKafka);

        // Guardar notificación
        Notificacion notificacion = new Notificacion(
            "Correo enviado: " + mensajeKafka,
            "administracion@innovatech.cl"
        );
        notificacionRepository.save(notificacion);
        System.out.println("[NOTIFICACIÓN] Guardada en BD.");

        BitacoraAuditoria registro = new BitacoraAuditoria(
            "CREAR_PROYECTO",
            "Proyecto",
            "sistema-kafka",
            "{\"mensaje\": \"" + mensajeKafka + "\"}"
        );
        bitacoraRepository.save(registro);
        System.out.println("[AUDITORÍA] Registro guardado en bitácora.");
    }
}