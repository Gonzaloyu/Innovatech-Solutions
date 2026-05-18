package com.innovatech.backend_gestion.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarMensajeProyectoCreado(String nombreProyecto) {
        String mensaje = "Se ha creado un nuevo proyecto en el sistema: " + nombreProyecto;
        kafkaTemplate.send("proyectos-topic", mensaje);
        System.out.println("Mensaje enviado a Kafka: " + mensaje);
    }
}