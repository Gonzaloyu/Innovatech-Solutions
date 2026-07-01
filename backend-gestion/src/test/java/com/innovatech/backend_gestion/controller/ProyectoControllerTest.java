package com.innovatech.backend_gestion.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.ProyectoLog;
import com.innovatech.backend_gestion.service.ProyectoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProyectoController.class)
class ProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProyectoService proyectoService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void obtenerTodos_DebeRetornar200ConListaDeProyectos() throws Exception {
        Proyecto p1 = new Proyecto();
        p1.setId(1L);
        p1.setNombre("Proyecto Alpha");

        Proyecto p2 = new Proyecto();
        p2.setId(2L);
        p2.setNombre("Proyecto Beta");

        when(proyectoService.obtenerTodos()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Proyecto Alpha"))
                .andExpect(jsonPath("$[1].nombre").value("Proyecto Beta"));
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornar200() throws Exception {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Alpha");

        when(proyectoService.obtenerPorId(1L)).thenReturn(proyecto);

        mockMvc.perform(get("/api/proyectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proyecto Alpha"));
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeRetornar404() throws Exception {
        when(proyectoService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Proyecto no encontrado con id: 99"));

        mockMvc.perform(get("/api/proyectos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado con id: 99"));
    }

    @Test
    void crearProyecto_DebeRetornar201ConProyectoCreado() throws Exception {
        Proyecto entrada = new Proyecto();
        entrada.setNombre("Nuevo Proyecto");

        Proyecto guardado = new Proyecto();
        guardado.setId(10L);
        guardado.setNombre("Nuevo Proyecto");

        when(proyectoService.crearProyecto(any(Proyecto.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Nuevo Proyecto"));
    }

    @Test
    void obtenerPorEmpleado_DebeRetornar200ConProyectosAsignados() throws Exception {
        Proyecto p = new Proyecto();
        p.setId(1L);
        p.setNombre("Proyecto del Empleado");

        when(proyectoService.obtenerPorEmpleado(5L)).thenReturn(List.of(p));

        mockMvc.perform(get("/api/proyectos/empleado/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Proyecto del Empleado"));
    }


    @Test
    void actualizarEstado_DebeRetornar200ConProyectoActualizado() throws Exception {
        Proyecto actualizado = new Proyecto();
        actualizado.setId(1L);
        actualizado.setNombre("Proyecto Alpha");

        Map<String, Object> body = Map.of("estadoId", 2);

        when(proyectoService.actualizarEstado(eq(1L), anyMap())).thenReturn(actualizado);

        mockMvc.perform(patch("/api/proyectos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarEstado_CuandoNoExiste_DebeRetornar404() throws Exception {
        when(proyectoService.actualizarEstado(eq(99L), anyMap()))
                .thenThrow(new RuntimeException("Proyecto no encontrado"));

        mockMvc.perform(patch("/api/proyectos/99/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoId\": 2}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado"));
    }


    @Test
    void actualizarProyecto_DebeRetornar200ConDatosActualizados() throws Exception {
        Proyecto actualizado = new Proyecto();
        actualizado.setId(1L);
        actualizado.setNombre("Nombre Actualizado");

        Map<String, Object> body = Map.of("nombre", "Nombre Actualizado");

        when(proyectoService.actualizarProyecto(eq(1L), anyMap())).thenReturn(actualizado);

        mockMvc.perform(put("/api/proyectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Actualizado"));
    }

    @Test
    void actualizarProyecto_CuandoNoExiste_DebeRetornar404() throws Exception {
        when(proyectoService.actualizarProyecto(eq(99L), anyMap()))
                .thenThrow(new RuntimeException("Proyecto no encontrado"));

        mockMvc.perform(put("/api/proyectos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Test\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado"));
    }

    @Test
    void obtenerLogs_DebeRetornar200ConListaDeLogs() throws Exception {
        ProyectoLog log = new ProyectoLog();
        log.setId(1L);

        when(proyectoService.obtenerLogs(1L)).thenReturn(List.of(log));

        mockMvc.perform(get("/api/proyectos/1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void crearLog_DebeRetornar201ConLogCreado() throws Exception {
        ProyectoLog logEntrada = new ProyectoLog();

        ProyectoLog logGuardado = new ProyectoLog();
        logGuardado.setId(5L);

        when(proyectoService.crearLog(eq(1L), any(ProyectoLog.class))).thenReturn(logGuardado);

        mockMvc.perform(post("/api/proyectos/1/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void crearLog_CuandoProyectoNoExiste_DebeRetornar404() throws Exception {
        when(proyectoService.crearLog(eq(99L), any(ProyectoLog.class)))
                .thenThrow(new RuntimeException("Proyecto no encontrado"));

        mockMvc.perform(post("/api/proyectos/99/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado"));
    }
}