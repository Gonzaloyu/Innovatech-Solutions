package com.innovatech.backend_analitico.service;

import com.innovatech.backend_analitico.model.Asignacion;
import com.innovatech.backend_analitico.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AsignacionServiceImpl implements AsignacionService {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Override
    public Asignacion crearAsignacion(Asignacion asignacion) {
        return asignacionRepository.save(asignacion);
    }

    @Override
    public List<Asignacion> obtenerPorEmpleado(Long empleadoId) {
        return asignacionRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<Asignacion> obtenerPorProyecto(Long proyectoId) {
        return asignacionRepository.findByProyectoId(proyectoId);
    }
}