package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.Proyecto;
import java.util.List;
import java.util.Map;

public interface ProyectoService {
    List<Proyecto> obtenerTodos();
    List<Proyecto> obtenerPorEmpleado(Long empleadoId);
    Proyecto actualizarEstado(Long id, Map<String, Object> body);
    Proyecto obtenerPorId(Long id);
    Proyecto crearProyecto(Proyecto proyecto);
}