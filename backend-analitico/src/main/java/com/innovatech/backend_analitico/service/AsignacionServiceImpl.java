package com.innovatech.backend_analitico.service;

import com.innovatech.backend_analitico.model.Asignacion;
import com.innovatech.backend_analitico.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    public List<Asignacion> obtenerTodas() {
        return asignacionRepository.findAll();
    }

    @Override
    public List<Asignacion> obtenerPorEmpleado(Long empleadoId) {
        return asignacionRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<Asignacion> obtenerPorProyecto(Long proyectoId) {
        return asignacionRepository.findByProyectoId(proyectoId);
    }

    @Override
    public Asignacion actualizarProgreso(Long id, String estado, String comentario, String herramientas, BigDecimal costoHerramientas) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));

        asignacion.setEstado(estado);
        asignacion.setComentario(comentario);
        asignacion.setHerramientasUtilizadas(herramientas);
        asignacion.setCostoHerramientas(costoHerramientas);

        // Si el proyecto se termina realizamos los cálculos analíticos automáticos
        if ("Finalizado".equalsIgnoreCase(estado)) {
            asignacion.setFechaFin(LocalDate.now()); 

            if (asignacion.getFechaInicio() != null) {
                // Calcular días transcurridos entre inicio y fin
                long diasTranscurridos = ChronoUnit.DAYS.between(asignacion.getFechaInicio(), asignacion.getFechaFin());
                if (diasTranscurridos <= 0) {
                    diasTranscurridos = 1; 
                }

                // Convertir a horas estimadas (8 horas laborales por día)
                long horasTrabajadas = diasTranscurridos * 8;

                // Obtener valor por hora del empleado asignado
                BigDecimal valorHoraEmpleado = asignacion.getEmpleado().getValorHora();

                // Calcular costo por tiempo invertido
                BigDecimal costoTiempo = valorHoraEmpleado.multiply(BigDecimal.valueOf(horasTrabajadas));

                // Costo Total = Costo de Tiempo + Costo de Herramientas
                BigDecimal costoTotal = costoTiempo.add(costoHerramientas);
                
                asignacion.setCostoTotalCalculado(costoTotal);
            }
        } else if ("En Ejecución".equalsIgnoreCase(estado) && asignacion.getFechaInicio() == null) {
            asignacion.setFechaInicio(LocalDate.now());
        }

        return asignacionRepository.save(asignacion);
    }
}