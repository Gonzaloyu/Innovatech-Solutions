package com.innovatech.backend_gestion;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // <--- CAMBIO AQUÍ

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Usamos Mockito
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Test
    void testGuardarProyecto() {
        // Prepararemos los datos
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Sistema de Gestión");
        proyecto.setEstado("Activo");

        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);

        // Ejecutar
        Proyecto guardado = proyectoRepository.save(proyecto);

        // Verifica
        assertNotNull(guardado);
        assertEquals("Sistema de Gestión", guardado.getNombre());
        verify(proyectoRepository, times(1)).save(proyecto);
    }
}