package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import com.innovatech.backend_gestion.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Override
    public Proyecto obtenerPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    }

    @Override
    public Proyecto crearProyecto(Proyecto proyecto) {
        Proyecto nuevo = proyectoRepository.save(proyecto);
        kafkaProducerService.enviarMensajeProyectoCreado(nuevo.getNombre());
        return nuevo;
    }
}