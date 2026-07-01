package com.innovatech.backend_gestion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Month;


import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.ProyectoLog;
import com.innovatech.backend_gestion.repository.ProyectoLogRepository;
import com.innovatech.backend_gestion.repository.ProyectoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceImplTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ProyectoLogRepository proyectoLogRepository;

    @InjectMocks
    private ProyectoServiceImpl proyectoService;

    @Test
    void obtenerTodos_DebeRetornarListaDeProyectos() {
        when(proyectoRepository.findAll()).thenReturn(List.of(new Proyecto(), new Proyecto()));

        List<Proyecto> resultado = proyectoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(proyectoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarProyecto() {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        Proyecto resultado = proyectoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> proyectoService.obtenerPorId(99L));
    }

    @Test
    void crearProyecto_ConKafkaExitoso_DebeGuardarYNotificar() {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Innovatech Web");
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);

        Proyecto resultado = proyectoService.crearProyecto(proyecto);

        assertNotNull(resultado);
        assertEquals("Innovatech Web", resultado.getNombre());
        verify(kafkaProducerService, times(1)).enviarMensajeProyectoCreado("Innovatech Web");
    }

    @Test
    void crearProyecto_CuandoKafkaFalla_DebeGuardarProyectoIgualmente() {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Caido");
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);
        
        doThrow(new RuntimeException("Error de conexión")).when(kafkaProducerService).enviarMensajeProyectoCreado(anyString());

        Proyecto resultado = proyectoService.crearProyecto(proyecto);

        assertNotNull(resultado);
        verify(proyectoRepository, times(1)).save(proyecto);
    }

    @Test
    void obtenerPorEmpleado_DebeRetornarListaAsignada() {
        when(proyectoRepository.findByEmpleadoId(5L)).thenReturn(List.of(new Proyecto()));

        List<Proyecto> resultado = proyectoService.obtenerPorEmpleado(5L);

        assertFalse(resultado.isEmpty());
    }

    @Test
    void actualizarEstado_ConTodosLosCampos_DebeActualizarProyecto() {
        Proyecto proyectoMock = new Proyecto();
        proyectoMock.setId(1L);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoMock));
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> body = new HashMap<>();
        body.put("estadoId", 2);
        body.put("fechaFin", "2026-12-31");
        body.put("empleadoCompletaId", 10);

        Proyecto resultado = proyectoService.actualizarEstado(1L, body);

        assertEquals(2L, resultado.getEstado().getId());
        assertEquals(LocalDate.of(2026, Month.DECEMBER, 31), resultado.getFechaFin());
        assertEquals(10L, resultado.getEmpleadoId());
    }

    @Test
    void actualizarEstado_SoloConEstado_NoDebeAlterarCamposOpcionales() {
        Proyecto proyectoMock = new Proyecto();
        proyectoMock.setId(1L);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoMock));
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> body = Map.of("estadoId", "3");

        Proyecto resultado = proyectoService.actualizarEstado(1L, body);

        assertEquals(3L, resultado.getEstado().getId());
        assertNull(resultado.getFechaFin());
        assertNull(resultado.getEmpleadoId());
    }

    @Test
    void actualizarEstado_CuandoNoExisteProyecto_DebeLanzarExcepcion() {
    when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        Map<String, Object> datosActualizacion = Map.of("estadoId", 1);

        assertThrows(RuntimeException.class, () -> 
            proyectoService.actualizarEstado(1L, datosActualizacion)
        );
    }

    @Test
    void actualizarProyecto_ConTodosLosDatosOpcionales_DebeModificarValores() {
        Proyecto proyectoMock = new Proyecto();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoMock));
        when(proyectoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> body = Map.of(
            "nombreEquipo", "Desarrollo Backend",
            "nombre", "SIDIST V2",
            "descripcion", "Logística moderna"
        );

        Proyecto resultado = proyectoService.actualizarProyecto(1L, body);

        assertEquals("Desarrollo Backend", resultado.getNombreEquipo());
        assertEquals("SIDIST V2", resultado.getNombre());
        assertEquals("Logística moderna", resultado.getDescripcion());
    }

    @Test
    void actualizarProyecto_ConMapVacio_DebeMantenerObjetoIntacto() {
        Proyecto proyectoMock = new Proyecto();
        proyectoMock.setNombre("Original");
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoMock));
        when(proyectoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Proyecto resultado = proyectoService.actualizarProyecto(1L, Map.of());

        assertEquals("Original", resultado.getNombre());
    }

    @Test
    void obtenerLogs_DebeConsultarAlRepositorioPorOrden() {
        when(proyectoLogRepository.findByProyectoIdOrderByIdDesc(1L)).thenReturn(List.of(new ProyectoLog()));

        List<ProyectoLog> logs = proyectoService.obtenerLogs(1L);

        assertEquals(1, logs.size());
        verify(proyectoLogRepository).findByProyectoIdOrderByIdDesc(1L);
    }

    @Test
    void crearLog_ProyectoExistente_AsignaProyectoYGuarda() {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        ProyectoLog logEntrada = new ProyectoLog();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(proyectoLogRepository.save(logEntrada)).thenReturn(logEntrada);

        ProyectoLog resultado = proyectoService.crearLog(1L, logEntrada);

        assertNotNull(resultado);
        assertEquals(proyecto, logEntrada.getProyecto());
    }
    @Test
    void crearLog_ProyectoInexistente_LanzaExcepcion() {

    when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

    ProyectoLog nuevoLog = new ProyectoLog();

    assertThrows(RuntimeException.class, () -> 
        proyectoService.crearLog(1L, nuevoLog)
    );
    }
}