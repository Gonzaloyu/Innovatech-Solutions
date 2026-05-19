package com.innovatech.backend_reportes.service;

import com.innovatech.backend_reportes.model.AsignacionDTO;
import com.innovatech.backend_reportes.model.EmpleadoDTO;
import com.innovatech.backend_reportes.model.ProyectoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${backend.gestion.url}")
    private String gestionUrl;

    @Value("${backend.analitico.url}")
    private String analiticoUrl;

    // Obtener todos los proyectos
    public List<ProyectoDTO> getProyectos() {
        ProyectoDTO[] proyectos = restTemplate.getForObject(
            gestionUrl + "/proyectos", ProyectoDTO[].class
        );
        return proyectos != null ? Arrays.asList(proyectos) : new ArrayList<>();
    }

    // Obtener todos los empleados
    public List<EmpleadoDTO> getEmpleados() {
        EmpleadoDTO[] empleados = restTemplate.getForObject(
            analiticoUrl + "/empleados", EmpleadoDTO[].class
        );
        return empleados != null ? Arrays.asList(empleados) : new ArrayList<>();
    }

    // Obtener todas las asignaciones
    public List<AsignacionDTO> getAsignaciones() {
        AsignacionDTO[] asignaciones = restTemplate.getForObject(
            analiticoUrl + "/asignaciones", AsignacionDTO[].class
        );
        return asignaciones != null ? Arrays.asList(asignaciones) : new ArrayList<>();
    }

    // KPI: Resumen general
    public Map<String, Object> getResumenKpis() {
        List<ProyectoDTO> proyectos = getProyectos();
        List<EmpleadoDTO> empleados = getEmpleados();
        List<AsignacionDTO> asignaciones = getAsignaciones();

        long enEjecucion = proyectos.stream()
            .filter(p -> p.getEstado() != null && 
                    "En Ejecución".equals(p.getEstado().getNombre()))
            .count();

        long atrasados = proyectos.stream()
            .filter(p -> p.getFechaFin() != null &&
                    p.getFechaFin().isBefore(LocalDate.now()) &&
                    p.getEstado() != null &&
                    !"Finalizado".equals(p.getEstado().getNombre()))
            .count();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalProyectos", proyectos.size());
        kpis.put("totalEmpleados", empleados.size());
        kpis.put("totalAsignaciones", asignaciones.size());
        kpis.put("proyectosEnEjecucion", enEjecucion);
        kpis.put("proyectosAtrasados", atrasados);
        return kpis;
    }

    // KPI: Empleados por departamento
    public Map<String, Long> getEmpleadosPorDepartamento() {
        return getEmpleados().stream()
            .filter(e -> e.getDepartamento() != null)
            .collect(Collectors.groupingBy(
                e -> e.getDepartamento().getNombre(),
                Collectors.counting()
            ));
    }

    // KPI: Proyectos por estado
    public Map<String, Long> getProyectosPorEstado() {
        return getProyectos().stream()
            .filter(p -> p.getEstado() != null)
            .collect(Collectors.groupingBy(
                p -> p.getEstado().getNombre(),
                Collectors.counting()
            ));
    }

    // KPI: Proyectos por categoría
    public Map<String, Long> getProyectosPorCategoria() {
        return getProyectos().stream()
            .filter(p -> p.getCategoria() != null)
            .collect(Collectors.groupingBy(
                p -> p.getCategoria().getNombre(),
                Collectors.counting()
            ));
    }

    // KPI: Horas asignadas por proyecto
    public Map<Long, Integer> getHorasPorProyecto() {
        return getAsignaciones().stream()
            .collect(Collectors.groupingBy(
                AsignacionDTO::getProyectoId,
                Collectors.summingInt(AsignacionDTO::getHorasAsignadas)
            ));
    }
}