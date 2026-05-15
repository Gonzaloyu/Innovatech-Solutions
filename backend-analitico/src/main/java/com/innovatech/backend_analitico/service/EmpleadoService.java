package com.innovatech.backend_analitico.service;

import com.innovatech.backend_analitico.model.Empleado;
import java.util.List;

public interface EmpleadoService {
    List<Empleado> obtenerTodos();
    Empleado crearEmpleado(Empleado empleado);
    List<Empleado> obtenerPorDepartamento(Long departamentoId);
}