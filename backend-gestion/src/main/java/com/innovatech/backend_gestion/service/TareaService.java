package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.Tarea;
import java.util.List;

public interface TareaService {
    List<Tarea> obtenerTodas();
    Tarea obtenerPorId(Long id);
    Tarea crearTarea(Tarea tarea);
}