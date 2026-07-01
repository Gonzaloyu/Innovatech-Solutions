package com.innovatech.backend_gestion.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovatech.backend_gestion.model.Tarea;
import com.innovatech.backend_gestion.service.TareaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TareaController.class)
class TareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TareaService tareaService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void obtenerTodas_DebeRetornar200ConListaDeTareas() throws Exception {
        Tarea t1 = new Tarea();
        t1.setId(1L);
        t1.setNombre("Diseñar base de datos");

        Tarea t2 = new Tarea();
        t2.setId(2L);
        t2.setNombre("Implementar API");

        when(tareaService.obtenerTodas()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Diseñar base de datos"))
                .andExpect(jsonPath("$[1].nombre").value("Implementar API"));
    }

    @Test
    void obtenerTodas_CuandoNoHayTareas_DebeRetornarListaVacia() throws Exception {
        when(tareaService.obtenerTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void obtenerPorId_CuandoExiste_DebeRetornar200() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setNombre("Diseñar base de datos");
        tarea.setEstado("Pendiente");

        when(tareaService.obtenerPorId(1L)).thenReturn(tarea);

        mockMvc.perform(get("/api/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Diseñar base de datos"))
                .andExpect(jsonPath("$.estado").value("Pendiente"));
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeRetornar404() throws Exception {
        when(tareaService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Tarea no encontrada con id: 99"));

        mockMvc.perform(get("/api/tareas/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tarea no encontrada con id: 99"));
    }

    @Test
    void obtenerPorProyecto_DebeRetornar200ConTareasDelProyecto() throws Exception {
        Tarea t = new Tarea();
        t.setId(1L);
        t.setNombre("Tarea del proyecto");

        when(tareaService.obtenerPorProyecto(3L)).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tareas/proyecto/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Tarea del proyecto"));
    }

    @Test
    void obtenerPorProyecto_CuandoNoHayTareas_DebeRetornarListaVacia() throws Exception {
        when(tareaService.obtenerPorProyecto(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/tareas/proyecto/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void crearTarea_DebeRetornar201ConTareaCreada() throws Exception {
        Tarea entrada = new Tarea();
        entrada.setNombre("Nueva Tarea");

        Tarea guardada = new Tarea();
        guardada.setId(10L);
        guardada.setNombre("Nueva Tarea");
        guardada.setEstado("Pendiente");

        when(tareaService.crearTarea(any(Tarea.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Nueva Tarea"))
                .andExpect(jsonPath("$.estado").value("Pendiente"));
    }

    @Test
    void crearTarea_CuandoProyectoNoExiste_DebeRetornar404() throws Exception {
        when(tareaService.crearTarea(any(Tarea.class)))
                .thenThrow(new RuntimeException("Proyecto no encontrado con id: 99"));

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Tarea sin proyecto\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado con id: 99"));
    }

    @Test
    void actualizarTarea_CuandoExiste_DebeRetornar200ConTareaActualizada() throws Exception {
        Tarea tareaActualizada = new Tarea();
        tareaActualizada.setId(1L);
        tareaActualizada.setNombre("Tarea Modificada");
        tareaActualizada.setEstado("Listo");

        when(tareaService.actualizarTarea(eq(1L), any(Tarea.class))).thenReturn(tareaActualizada);

        mockMvc.perform(put("/api/tareas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tareaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tarea Modificada"))
                .andExpect(jsonPath("$.estado").value("Listo"));
    }

    @Test
    void actualizarTarea_CuandoNoExiste_DebeRetornar404() throws Exception {
        when(tareaService.actualizarTarea(eq(99L), any(Tarea.class)))
                .thenThrow(new RuntimeException("Tarea no encontrada con id: 99"));

        Tarea tarea = new Tarea();
        tarea.setNombre("No importa");

        mockMvc.perform(put("/api/tareas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tarea)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tarea no encontrada con id: 99"));
    }

    @Test
    void eliminarTarea_CuandoExiste_DebeRetornar204() throws Exception {
        doNothing().when(tareaService).eliminarTarea(1L);

        mockMvc.perform(delete("/api/tareas/1"))
                .andExpect(status().isNoContent());

        verify(tareaService, times(1)).eliminarTarea(1L);
    }

    @Test
    void eliminarTarea_CuandoNoExiste_DebeRetornar404() throws Exception {
        doThrow(new RuntimeException("Tarea no encontrada con id: 99"))
                .when(tareaService).eliminarTarea(99L);

        mockMvc.perform(delete("/api/tareas/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tarea no encontrada con id: 99"));
    }
}