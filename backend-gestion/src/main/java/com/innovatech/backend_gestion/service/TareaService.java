package com.innovatech.backend_gestion.service;


import com.innovatech.backend_gestion.model.Tarea;
import java.util.List;


public interface TareaService {
   
    List<Tarea> obtenerTodas();
   
    Tarea obtenerPorId(Long id);
   
    // Buscar todas las tareas asociadas a un proyecto en específico
    List<Tarea> obtenerPorProyecto(Long proyectoId);
   
    Tarea crearTarea(Tarea tarea);
   
    // Para actualizar el estado ('Pendiente', 'Listo', 'Atrasado') u otros campos
    Tarea actualizarTarea(Long id, Tarea tarea);
   
    // Para borrar la tarea usando el botón ✕ en Vue
    void eliminarTarea(Long id);
}
