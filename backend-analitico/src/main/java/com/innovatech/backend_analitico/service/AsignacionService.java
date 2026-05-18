package com.innovatech.backend_analitico.service;

import com.innovatech.backend_analitico.model.Asignacion;
import java.util.List;

public interface AsignacionService {
    Asignacion crearAsignacion(Asignacion asignacion);
    List<Asignacion> obtenerTodas();
    List<Asignacion> obtenerPorEmpleado(Long empleadoId);
    List<Asignacion> obtenerPorProyecto(Long proyectoId);
}