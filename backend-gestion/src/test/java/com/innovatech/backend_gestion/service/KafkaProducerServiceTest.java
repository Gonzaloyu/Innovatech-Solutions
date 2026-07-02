package com.innovatech.backend_gestion.service;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void enviarMensajeProyectoCreado_DebeEnviarAlTopicCorrecto() {
        kafkaProducerService.enviarMensajeProyectoCreado("Innovatech Web");

        verify(kafkaTemplate, times(1)).send(
            "proyectos-topic",
            "Se ha creado un nuevo proyecto en el sistema: Innovatech Web"
        );
    }

    @Test
    void enviarMensajeProyectoCreado_DebeIncluirNombreEnElMensaje() {
        String nombre = "Sistema de Logística";

        kafkaProducerService.enviarMensajeProyectoCreado(nombre);

        verify(kafkaTemplate).send(
            eq("proyectos-topic"),
            contains("Sistema de Logística")
        );
    }
    @Test
    void enviarMensajeProyectoCreado_ConNombreVacio_DebeEnviarIgualmente() {
        kafkaProducerService.enviarMensajeProyectoCreado("");

        verify(kafkaTemplate, times(1)).send(
            "proyectos-topic",
            "Se ha creado un nuevo proyecto en el sistema: "
        );
    }
}