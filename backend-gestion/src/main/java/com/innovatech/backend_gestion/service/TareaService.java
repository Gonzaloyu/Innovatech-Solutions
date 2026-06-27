package com.innovatech.backend_gestion.service;


import com.innovatech.backend_gestion.model.Tarea;
import java.util.List;


public interface TareaService {
   
    List<Tarea> obtenerTodas();
   
    Tarea obtenerPorId(Long id);

    List<Tarea> obtenerPorProyecto(Long proyectoId);
   
    Tarea crearTarea(Tarea tarea);
   
    Tarea actualizarTarea(Long id, Tarea tarea);

    void eliminarTarea(Long id);
}
