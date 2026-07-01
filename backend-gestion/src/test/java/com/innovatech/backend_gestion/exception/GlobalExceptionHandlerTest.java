package com.innovatech.backend_gestion.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.innovatech.backend_gestion.controller.ProyectoController;
import com.innovatech.backend_gestion.service.ProyectoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

@WebMvcTest(ProyectoController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProyectoService proyectoService;


    @Test
    void cuandoRuntimeException_DebeRetornar404ConMensaje() throws Exception {
        when(proyectoService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Proyecto no encontrado con id: 99"));

        mockMvc.perform(get("/api/proyectos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Proyecto no encontrado con id: 99"));
    }


    @Test
    void cuandoExceptionGenerica_DebeRetornar500ConMensaje() throws Exception {
        when(proyectoService.obtenerTodos())
                .thenThrow(new RuntimeException("fallo inesperado"));

        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoRuntimeException_ElCuerpoDeRespuestaContieneElMensajeOriginal() throws Exception {
        when(proyectoService.obtenerPorId(1L))
                .thenThrow(new RuntimeException("Mensaje de error específico"));

        mockMvc.perform(get("/api/proyectos/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Mensaje de error específico"));
    }
}