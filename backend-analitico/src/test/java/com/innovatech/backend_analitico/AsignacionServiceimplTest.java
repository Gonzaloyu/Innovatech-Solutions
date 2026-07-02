package com.innovatech.backend_analitico;

import com.innovatech.backend_analitico.model.Asignacion;
import com.innovatech.backend_analitico.model.Cargo;
import com.innovatech.backend_analitico.model.Departamento;
import com.innovatech.backend_analitico.model.Empleado;
import com.innovatech.backend_analitico.repository.AsignacionRepository;
import com.innovatech.backend_analitico.service.AsignacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    private Asignacion asignacion;
    private Empleado empleado;

    @BeforeEach
    void setUp() {
        Departamento dep = new Departamento(1L, "Tecnología");
        Cargo cargo = new Cargo(1L, "Desarrollador", "Senior");

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
    void crearAsignacion_guardaYRetorna() {
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(asignacion);

        Asignacion resultado = asignacionService.crearAsignacion(asignacion);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Asignado", resultado.getEstado());
        verify(asignacionRepository, times(1)).save(asignacion);
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(asignacionRepository.findAll()).thenReturn(List.of(asignacion));

        List<Asignacion> resultado = asignacionService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(asignacionRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodas_listaVacia() {
        when(asignacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Asignacion> resultado = asignacionService.obtenerTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEmpleado_retornaAsignaciones() {
        when(asignacionRepository.findByEmpleadoId(1L)).thenReturn(List.of(asignacion));

        List<Asignacion> resultado = asignacionService.obtenerPorEmpleado(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(asignacionRepository, times(1)).findByEmpleadoId(1L);
    }

    @Test
    void obtenerPorEmpleado_sinAsignaciones_retornaVacio() {
        when(asignacionRepository.findByEmpleadoId(99L)).thenReturn(Collections.emptyList());

        List<Asignacion> resultado = asignacionService.obtenerPorEmpleado(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorProyecto_retornaAsignaciones() {
        when(asignacionRepository.findByProyectoId(1L)).thenReturn(List.of(asignacion));

        List<Asignacion> resultado = asignacionService.obtenerPorProyecto(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(asignacionRepository, times(1)).findByProyectoId(1L);
    }

    @Test
    void actualizarProgreso_noExiste_lanzaExcepcion() {
        when(asignacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> asignacionService.actualizarProgreso(99L, "En Ejecución", "", "", BigDecimal.ZERO));
    }

    @Test
    void actualizarProgreso_enEjecucion_sinFechaInicio_asignaFechaInicio() {
        asignacion.setFechaInicio(null);
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        Asignacion resultado = asignacionService.actualizarProgreso(
                1L, "En Ejecución", "Iniciando trabajo", "", BigDecimal.ZERO);

        assertNotNull(resultado.getFechaInicio());
        assertEquals("En Ejecución", resultado.getEstado());
        verify(asignacionRepository).save(asignacion);
    }

    @Test
    void actualizarProgreso_enEjecucion_conFechaInicio_noReemplazaFecha() {
        LocalDate fechaOriginal = LocalDate.of(2026, Month.JANUARY, 1);
        asignacion.setFechaInicio(fechaOriginal);
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        asignacionService.actualizarProgreso(
                1L, "En Ejecución", "Continuando", "", BigDecimal.ZERO);

        assertEquals(fechaOriginal, asignacion.getFechaInicio());
    }

    @Test
    void actualizarProgreso_finalizado_conFechaInicio_calculaCostoTotal() {
        asignacion.setFechaInicio(LocalDate.of(2026, Month.JANUARY, 1));
        BigDecimal costoHerramientas = new BigDecimal("500.00");

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        Asignacion resultado = asignacionService.actualizarProgreso(
                1L, "Finalizado", "Completado", "Laptop, Monitor", costoHerramientas);

        // Verificar que se calculó un costo total mayor a las herramientas solas
        assertNotNull(resultado.getCostoTotalCalculado());
        assertTrue(resultado.getCostoTotalCalculado().compareTo(costoHerramientas) > 0);
        assertEquals("Finalizado", resultado.getEstado());
        assertEquals("Completado", resultado.getComentario());
        assertEquals("Laptop, Monitor", resultado.getHerramientasUtilizadas());
    }

    @Test
    void actualizarProgreso_finalizado_sinFechaInicio_noCalculaCosto() {
        asignacion.setFechaInicio(null);
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        Asignacion resultado = asignacionService.actualizarProgreso(
                1L, "Finalizado", "Sin inicio registrado", "", BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, resultado.getCostoTotalCalculado());
        assertEquals("Finalizado", resultado.getEstado());
        assertNotNull(resultado.getFechaFin());
    }

    @Test
    void actualizarProgreso_finalizado_calculaCostoTiempoCorrectamente() {

        LocalDate inicio = LocalDate.of(2026, Month.JUNE, 1);
        asignacion.setFechaInicio(inicio);
        BigDecimal costoHerramientas = new BigDecimal("500.00");

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Asignacion resultado = asignacionService.actualizarProgreso(
                1L, "Finalizado", "Listo", "IDE", costoHerramientas);

        assertTrue(resultado.getCostoTotalCalculado().compareTo(BigDecimal.ZERO) > 0);
    }
    @Test
    void actualizarProgreso_persisteComentarioYHerramientas() {
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        asignacionService.actualizarProgreso(
                1L, "En Ejecución", "Avanzando bien", "VSCode, Docker", BigDecimal.ZERO);

        assertEquals("Avanzando bien", asignacion.getComentario());
        assertEquals("VSCode, Docker", asignacion.getHerramientasUtilizadas());
        assertEquals(BigDecimal.ZERO, asignacion.getCostoHerramientas());
    }

    @Test
    void actualizarProgreso_estadoAsignado_noModificaFechas() {
        asignacion.setFechaInicio(null);
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        asignacionService.actualizarProgreso(
                1L, "Asignado", "Pendiente de inicio", "", BigDecimal.ZERO);

        assertNull(asignacion.getFechaInicio());
        assertNull(asignacion.getFechaFin());
    }
}