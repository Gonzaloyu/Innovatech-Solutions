package com.innovatech.backend_gestion.service;


import com.innovatech.backend_gestion.model.Tarea;
import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.repository.TareaRepository;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class TareaServiceImpl implements TareaService {


    @Autowired
    private TareaRepository tareaRepository;


    @Autowired
    private ProyectoRepository proyectoRepository;


    @Override
    public List<Tarea> obtenerTodas() {
        return tareaRepository.findAll();
    }


    @Override
    public Tarea obtenerPorId(Long id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }


    @Override
    public List<Tarea> obtenerPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId);
    }


    @Override
    public Tarea crearTarea(Tarea tarea) {
        // Cargamos el Proyecto real desde la BD para evitar "detached entity passed to persist"
        if (tarea.getProyecto() != null && tarea.getProyecto().getId() != null) {
            Proyecto proyectoReal = proyectoRepository.findById(tarea.getProyecto().getId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + tarea.getProyecto().getId()));
            tarea.setProyecto(proyectoReal);
        }


        if (tarea.getEstado() == null || tarea.getEstado().isEmpty()) {
            tarea.setEstado("Pendiente");
        }
        return tareaRepository.save(tarea);
    }


    @Override
    public Tarea actualizarTarea(Long id, Tarea tareaActualizada) {
        Tarea tareaExistente = obtenerPorId(id);


        tareaExistente.setNombre(tareaActualizada.getNombre());
        tareaExistente.setDescripcion(tareaActualizada.getDescripcion());
        tareaExistente.setEstado(tareaActualizada.getEstado());
        tareaExistente.setAsignacionId(tareaActualizada.getAsignacionId());
        tareaExistente.setEmpleadoId(tareaActualizada.getEmpleadoId());
        tareaExistente.setFechaInicio(tareaActualizada.getFechaInicio());
        tareaExistente.setFechaLimite(tareaActualizada.getFechaLimite());


        return tareaRepository.save(tareaExistente);
    }


    @Override
    public void eliminarTarea(Long id) {
        Tarea tarea = obtenerPorId(id);
        tareaRepository.delete(tarea);
    }
}
