package com.innovatech.backend_notificaciones.service;

import com.innovatech.backend_notificaciones.model.Notificacion;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;

interface NotificacionRepository extends JpaRepository<Notificacion, Long> {}

@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "proyectos-topic", groupId = "grupo-final-test-100")
    public void escucharNuevoProyecto(String mensajeKafka) {
        System.out.println("[NUEVA NOTIFICACIÓN RECIBIDA]: " + mensajeKafka);
        
        Notificacion nuevaNotificacion = new Notificacion("Correo enviado por: " + mensajeKafka);
        repository.save(nuevaNotificacion);
        
        System.out.println("Registro de notificación guardado en BD.");
    }
}