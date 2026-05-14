package com.innovatech.backend_notificaciones;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic proyectosTopic() {
        return TopicBuilder.name("proyectos-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}