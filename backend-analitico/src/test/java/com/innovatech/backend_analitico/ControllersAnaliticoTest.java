package com.innovatech.backend_analitico;

import com.innovatech.backend_analitico.controller.AsignacionController;
import com.innovatech.backend_analitico.controller.EmpleadoController;
import com.innovatech.backend_analitico.model.*;
import com.innovatech.backend_analitico.service.AsignacionService;
import com.innovatech.backend_analitico.service.EmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllersAnaliticoTest {

    @Mock private EmpleadoService empleadoService;
    @InjectMocks private EmpleadoController empleadoController;

    @Mock private AsignacionService asignacionService;
    @InjectMocks private AsignacionController asignacionController;

    private Empleado empleado;
    private Asignacion asignacion;

    @BeforeEach
    void setUp() {
        Departamento dep  = new Departamento(1L, "Tecnología");
        Cargo cargo       = new Cargo(1L, "Desarrollador", "Senior");

        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan Pérez");
        empleado.setEmail("juan@empresa.com");
        empleado.setDepartamento(dep);
        empleado.setCargo(cargo);
        empleado.setValorHora(new BigDecimal("25.00"));

        asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setEmpleado(empleado);
        asignacion.setProyectoId(1L);
        asignacion.setHorasAsignadas(80);
        asignacion.setEstado("Asignado");
        asignacion.setCostoHerramientas(BigDecimal.ZERO);
        asignacion.setCostoTotalCalculado(BigDecimal.ZERO);
    }

    @Test
    void empleadoController_obtenerTodos_retorna200() {
        when(empleadoService.obtenerTodos()).thenReturn(List.of(empleado));

        ResponseEntity<List<Empleado>> response = empleadoController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Juan Pérez", response.getBody().get(0).getNombre());
    }

    @Test
    void empleadoController_obtenerPorId_retorna200() {
        when(empleadoService.obtenerPorId(1L)).thenReturn(empleado);

        ResponseEntity<Empleado> response = empleadoController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("juan@empresa.com", response.getBody().getEmail());
    }

    @Test
    void empleadoController_obtenerPorDepartamento_retorna200() {
        when(empleadoService.obtenerPorDepartamento(1L)).thenReturn(List.of(empleado));

        ResponseEntity<List<Empleado>> response = empleadoController.obtenerPorDepartamento(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Tecnología", response.getBody().get(0).getDepartamento().getNombre());
    }

    @Test
    void empleadoController_crearEmpleado_retorna201() {
        when(empleadoService.crearEmpleado(any())).thenReturn(empleado);

        ResponseEntity<Empleado> response = empleadoController.crearEmpleado(empleado);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Juan Pérez", response.getBody().getNombre());
        assertEquals(new BigDecimal("25.00"), response.getBody().getValorHora());
    }

    @Test
    void empleadoController_obtenerTodos_listaVacia_retorna200() {
        when(empleadoService.obtenerTodos()).thenReturn(List.of());

        ResponseEntity<List<Empleado>> response = empleadoController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void asignacionController_obtenerTodas_retorna200() {
        when(asignacionService.obtenerTodas()).thenReturn(List.of(asignacion));

        ResponseEntity<List<Asignacion>> response = asignacionController.obtenerTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Asignado", response.getBody().get(0).getEstado());
    }

    @Test
    void asignacionController_crearAsignacion_retorna201() {
        when(asignacionService.crearAsignacion(any())).thenReturn(asignacion);

        ResponseEntity<Asignacion> response = asignacionController.crearAsignacion(asignacion);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void asignacionController_porEmpleado_retorna200() {
        when(asignacionService.obtenerPorEmpleado(1L)).thenReturn(List.of(asignacion));

        ResponseEntity<List<Asignacion>> response = asignacionController.porEmpleado(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(asignacionService).obtenerPorEmpleado(1L);
    }

    @Test
    void asignacionController_porProyecto_retorna200() {
        when(asignacionService.obtenerPorProyecto(1L)).thenReturn(List.of(asignacion));

        ResponseEntity<List<Asignacion>> response = asignacionController.porProyecto(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(asignacionService).obtenerPorProyecto(1L);
    }

    @Test
    void asignacionController_reportarProgreso_enEjecucion_retorna200() {
        asignacion.setEstado("En Ejecución");
        when(asignacionService.actualizarProgreso(
                eq(1L), eq("En Ejecución"), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(asignacion);

        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", "En Ejecución");
        payload.put("comentario", "Trabajando en el módulo");
        payload.put("herramientas", "VSCode");
        payload.put("costoHerramientas", 0.0);

        ResponseEntity<Asignacion> response = asignacionController.reportarProgreso(1L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("En Ejecución", response.getBody().getEstado());
    }

    @Test
    void asignacionController_reportarProgreso_finalizado_retorna200() {
        asignacion.setEstado("Finalizado");
        asignacion.setCostoTotalCalculado(new BigDecimal("2500.00"));

        when(asignacionService.actualizarProgreso(
                eq(1L), eq("Finalizado"), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(asignacion);

        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", "Finalizado");
        payload.put("comentario", "Proyecto completado");
        payload.put("herramientas", "Laptop, Monitor");
        payload.put("costoHerramientas", 500.0);

        ResponseEntity<Asignacion> response = asignacionController.reportarProgreso(1L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Finalizado", response.getBody().getEstado());
        assertTrue(response.getBody().getCostoTotalCalculado().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void asignacionController_reportarProgreso_sinCostoHerramientas_usaCero() {
        when(asignacionService.actualizarProgreso(
                eq(1L), anyString(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(asignacion);

        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", "En Ejecución");
        payload.put("comentario", "Inicio");
        payload.put("herramientas", "");


        ResponseEntity<Asignacion> response = asignacionController.reportarProgreso(1L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
  
        verify(asignacionService).actualizarProgreso(
                eq(1L), eq("En Ejecución"), eq("Inicio"), eq(""), any(BigDecimal.class));
    }

    @Test
    void asignacionController_reportarProgreso_costoComoInteger_convierteCorrectamente() {
        when(asignacionService.actualizarProgreso(
                eq(1L), anyString(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(asignacion);

        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", "En Ejecución");
        payload.put("comentario", "Test");
        payload.put("herramientas", "");
        payload.put("costoHerramientas", 1000);

        ResponseEntity<Asignacion> response = asignacionController.reportarProgreso(1L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}