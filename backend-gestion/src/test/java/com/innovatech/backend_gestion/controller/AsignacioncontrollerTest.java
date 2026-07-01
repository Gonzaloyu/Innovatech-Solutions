package com.innovatech.backend_gestion.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovatech.backend_gestion.model.Asignacion;
import com.innovatech.backend_gestion.repository.AsignacionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AsignacionController.class)
class AsignacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsignacionRepository asignacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearAsignacion_DebeRetornar200ConAsignacionCreada() throws Exception {
        Asignacion entrada = new Asignacion();
        entrada.setId(1L);

        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(entrada);

        mockMvc.perform(post("/api/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void obtenerPorProyecto_DebeRetornar200ConLista() throws Exception {
        Asignacion a1 = new Asignacion();
        a1.setId(1L);
        Asignacion a2 = new Asignacion();
        a2.setId(2L);

        when(asignacionRepository.findByProyectoId(10L)).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/asignaciones/proyecto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void obtenerPorProyecto_CuandoNoHayAsignaciones_DebeRetornarListaVacia() throws Exception {
        when(asignacionRepository.findByProyectoId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/asignaciones/proyecto/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void eliminarAsignacion_DebeRetornar204() throws Exception {
        doNothing().when(asignacionRepository).deleteById(1L);

        mockMvc.perform(delete("/api/asignaciones/1"))
                .andExpect(status().isNoContent());

        verify(asignacionRepository, times(1)).deleteById(1L);
    }
}