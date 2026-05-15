package com.innovatech.backend_gestion.service;

import com.innovatech.backend_gestion.model.Proyecto;
import java.util.List;

public interface ProyectoService {
    List<Proyecto> obtenerTodos();
    Proyecto obtenerPorId(Long id);
    Proyecto crearProyecto(Proyecto proyecto);
}