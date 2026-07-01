package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.EstadoProyecto;
import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.ProyectoLog;
import com.innovatech.backend_gestion.repository.ProyectoLogRepository;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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
        try {
            kafkaProducerService.enviarMensajeProyectoCreado(nuevo.getNombre());
        } catch (Exception e) {
            System.err.println("Kafka no disponible, proyecto guardado igual: " + e.getMessage());
        }
        return nuevo;
    }

    @Override
    public List<Proyecto> obtenerPorEmpleado(Long empleadoId) {
        return proyectoRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public Proyecto actualizarEstado(Long id, Map<String, Object> body) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        Long estadoId = Long.valueOf(body.get("estadoId").toString());
        EstadoProyecto estado = new EstadoProyecto();
        estado.setId(estadoId);
        proyecto.setEstado(estado);
    
        if (body.containsKey("fechaFin")) {
            proyecto.setFechaFin(LocalDate.parse(body.get("fechaFin").toString()));
        }
    
        if (body.containsKey("empleadoCompletaId")) {
            proyecto.setEmpleadoId(Long.valueOf(body.get("empleadoCompletaId").toString()));
        }
    
        return proyectoRepository.save(proyecto);
    }
    @Autowired
private ProyectoLogRepository proyectoLogRepository;

@Override
public Proyecto actualizarProyecto(Long id, Map<String, Object> body) {
    Proyecto proyecto = proyectoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

    if (body.containsKey("nombreEquipo")) {
        proyecto.setNombreEquipo(body.get("nombreEquipo").toString());
    }
    if (body.containsKey("nombre")) {
        proyecto.setNombre(body.get("nombre").toString());
    }
    if (body.containsKey("descripcion")) {
        proyecto.setDescripcion(body.get("descripcion").toString());
    }

    return proyectoRepository.save(proyecto);
    }

    @Override
    public List<ProyectoLog> obtenerLogs(Long proyectoId) {
        return proyectoLogRepository.findByProyectoIdOrderByIdDesc(proyectoId);
    }

    @Override
    public ProyectoLog crearLog(Long proyectoId, ProyectoLog log) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        log.setProyecto(proyecto);
        return proyectoLogRepository.save(log);
    }
    
}