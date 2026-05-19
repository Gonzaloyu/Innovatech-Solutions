package com.innovatech.backend_gestion;

import com.innovatech.backend_gestion.model.EstadoProyecto;
import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Test
    void testGuardarProyecto() {
        // Preparar datos
        EstadoProyecto estado = new EstadoProyecto(1L, "Activo");

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Sistema de Gestión");
        proyecto.setEstado(estado);

        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);

        // Ejecutar
        Proyecto guardado = proyectoRepository.save(proyecto);

        // Verificar
        assertNotNull(guardado);
        assertEquals("Sistema de Gestión", guardado.getNombre());
        assertEquals("Activo", guardado.getEstado().getNombre());
        verify(proyectoRepository, times(1)).save(proyecto);
    }
}