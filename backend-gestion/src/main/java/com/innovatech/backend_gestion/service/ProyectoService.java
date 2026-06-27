package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.ProyectoLog;

import java.util.List;
import java.util.Map;

public interface ProyectoService {
    List<Proyecto> obtenerTodos();
    List<Proyecto> obtenerPorEmpleado(Long empleadoId);
    Proyecto actualizarEstado(Long id, Map<String, Object> body);
    Proyecto obtenerPorId(Long id);
    Proyecto crearProyecto(Proyecto proyecto);
    Proyecto actualizarProyecto(Long id, Map<String, Object> body);
    List<ProyectoLog> obtenerLogs(Long proyectoId);
    ProyectoLog crearLog(Long proyectoId, ProyectoLog log);
}