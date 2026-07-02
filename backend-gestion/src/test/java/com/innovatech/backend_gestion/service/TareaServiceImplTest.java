package com.innovatech.backend_gestion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import com.innovatech.backend_gestion.model.Proyecto;
import com.innovatech.backend_gestion.model.Tarea;
import com.innovatech.backend_gestion.repository.ProyectoRepository;
import com.innovatech.backend_gestion.repository.TareaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TareaServiceImplTest {

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private ProyectoRepository proyectoRepository;
    @InjectMocks
    private TareaServiceImpl tareaService;

    @Test
    void obtenerTodas_DebeRetornarTodasLasTareas() {
        when(tareaRepository.findAll()).thenReturn(List.of(new Tarea(), new Tarea()));

        List<Tarea> resultado = tareaService.obtenerTodas();

        assertEquals(2, resultado.size());
        verify(tareaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarTarea() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        Tarea resultado = tareaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tareaService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void obtenerPorProyecto_DebeRetornarTareasFiltradas() {
        when(tareaRepository.findByProyectoId(3L)).thenReturn(List.of(new Tarea(), new Tarea()));

        List<Tarea> resultado = tareaService.obtenerPorProyecto(3L);

        assertEquals(2, resultado.size());
        verify(tareaRepository).findByProyectoId(3L);
    }

    @Test
    void crearTarea_ConProyectoValidoYEstadoDefinido_NoDebesobreescribirEstado() {
        Proyecto proyectoRef = new Proyecto();
        proyectoRef.setId(1L);

        Proyecto proyectoReal = new Proyecto();
        proyectoReal.setId(1L);

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyectoRef);
        tarea.setEstado("En progreso");

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoReal));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Tarea resultado = tareaService.crearTarea(tarea);

        assertEquals("En progreso", resultado.getEstado());
        verify(proyectoRepository, times(1)).findById(1L);
        verify(tareaRepository, times(1)).save(tarea);
    }

    @Test
    void crearTarea_ConEstadoNull_DebeAsignarPendiente() {
        Proyecto proyectoRef = new Proyecto();
        proyectoRef.setId(2L);

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyectoRef);
        tarea.setEstado(null); // <-- estado nulo

        when(proyectoRepository.findById(2L)).thenReturn(Optional.of(new Proyecto()));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Tarea resultado = tareaService.crearTarea(tarea);

        assertEquals("Pendiente", resultado.getEstado());
    }

    @Test
    void crearTarea_ConEstadoVacio_DebeAsignarPendiente() {
        Proyecto proyectoRef = new Proyecto();
        proyectoRef.setId(2L);

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyectoRef);
        tarea.setEstado(""); // <-- estado vacío — la otra rama del isEmpty()

        when(proyectoRepository.findById(2L)).thenReturn(Optional.of(new Proyecto()));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Tarea resultado = tareaService.crearTarea(tarea);

        assertEquals("Pendiente", resultado.getEstado());
    }

    @Test
    void crearTarea_ConProyectoInexistente_DebeLanzarExcepcion() {
        Proyecto proyectoRef = new Proyecto();
        proyectoRef.setId(99L);

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyectoRef);

        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tareaService.crearTarea(tarea));
        verify(tareaRepository, never()).save(any()); // nunca debe guardar
    }

    @Test
    void crearTarea_SinProyecto_DebeGuardarDirectamente() {
        Tarea tarea = new Tarea();
        tarea.setProyecto(null); // sin proyecto
        tarea.setEstado("Pendiente");

        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Tarea resultado = tareaService.crearTarea(tarea);

        assertNotNull(resultado);
        // no debe consultar el repositorio de proyectos
        verify(proyectoRepository, never()).findById(any());
        verify(tareaRepository, times(1)).save(tarea);
    }

    @Test
    void actualizarTarea_CuandoExiste_DebeActualizarTodosLosCampos() {
        Tarea existente = new Tarea();
        existente.setId(1L);
        existente.setNombre("Vieja");

        Tarea actualizada = new Tarea();
        actualizada.setNombre("Nueva");
        actualizada.setDescripcion("Descripción nueva");
        actualizada.setEstado("Completado");
        actualizada.setAsignacionId(5L);
        actualizada.setEmpleadoId(10L);
        actualizada.setFechaInicio(LocalDate.of(2026, Month.JANUARY, 1));
        actualizada.setFechaLimite(LocalDate.of(2026, Month.JUNE, 30));

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Tarea resultado = tareaService.actualizarTarea(1L, actualizada);

        assertEquals("Nueva", resultado.getNombre());
        assertEquals("Descripción nueva", resultado.getDescripcion());
        assertEquals("Completado", resultado.getEstado());
        assertEquals(5L, resultado.getAsignacionId());
        assertEquals(10L, resultado.getEmpleadoId());
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), resultado.getFechaInicio());
        assertEquals(LocalDate.of(2026, Month.JUNE, 30), resultado.getFechaLimite());
        verify(tareaRepository, times(1)).save(existente);
    }

    @Test
    void actualizarTarea_CuandoNoExiste_DebeLanzarExcepcion() {

        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        Tarea nuevaTarea = new Tarea();

        assertThrows(RuntimeException.class, () -> {
            tareaService.actualizarTarea(99L, nuevaTarea);
        });
    }
    @Test
    void eliminarTarea_CuandoExiste_DebeEliminarLaTareaCorrecta() {

        Long tareaId = 1L;
        Tarea tareaExistente = new Tarea();
        when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaExistente));

        tareaService.eliminarTarea(tareaId);

        verify(tareaRepository, times(1)).delete(tareaExistente);

    }
    @Test
    void eliminarTarea_CuandoNoExiste_DebeLanzarExcepcion() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tareaService.eliminarTarea(99L));
        verify(tareaRepository, never()).delete(any());
    }
}