package com.innovatech.backend_analitico;

import com.innovatech.backend_analitico.model.Cargo;
import com.innovatech.backend_analitico.model.Departamento;
import com.innovatech.backend_analitico.model.Empleado;
import com.innovatech.backend_analitico.repository.EmpleadoRepository;
import com.innovatech.backend_analitico.service.EmpleadoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceImplTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleado;
    private Departamento departamento;
    private Cargo cargo;

    @BeforeEach
    void setUp() {
        departamento = new Departamento(1L, "Tecnología");
        cargo = new Cargo(1L, "Desarrollador", "Senior");

        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan Pérez");
        empleado.setEmail("juan@empresa.com");
        empleado.setDepartamento(departamento);
        empleado.setCargo(cargo);
        empleado.setValorHora(new BigDecimal("25.00"));
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(empleadoRepository.findAll()).thenReturn(List.of(empleado));

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodos_listaVacia() {
        when(empleadoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorId_existente_retornaEmpleado() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));

        Empleado resultado = empleadoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@empresa.com", resultado.getEmail());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> empleadoService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void crearEmpleado_guardaYRetorna() {
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        Empleado resultado = empleadoService.crearEmpleado(empleado);

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals(new BigDecimal("25.00"), resultado.getValorHora());
        verify(empleadoRepository, times(1)).save(empleado);
    }

    @Test
    void crearEmpleado_distintosCargos() {
        Empleado junior = new Empleado();
        junior.setNombre("Ana García");
        junior.setEmail("ana@empresa.com");
        junior.setCargo(new Cargo(2L, "Tester", "Junior"));
        junior.setDepartamento(departamento);
        junior.setValorHora(new BigDecimal("15.00"));

        when(empleadoRepository.save(any())).thenReturn(junior);

        Empleado resultado = empleadoService.crearEmpleado(junior);

        assertEquals("Junior", resultado.getCargo().getNivel());
        assertEquals(new BigDecimal("15.00"), resultado.getValorHora());
    }

    @Test
    void obtenerPorDepartamento_retornaEmpleados() {
        when(empleadoRepository.findByDepartamentoId(1L)).thenReturn(List.of(empleado));

        List<Empleado> resultado = empleadoService.obtenerPorDepartamento(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tecnología", resultado.get(0).getDepartamento().getNombre());
        verify(empleadoRepository, times(1)).findByDepartamentoId(1L);
    }

    @Test
    void obtenerPorDepartamento_sinEmpleados_retornaVacio() {
        when(empleadoRepository.findByDepartamentoId(99L)).thenReturn(Collections.emptyList());

        List<Empleado> resultado = empleadoService.obtenerPorDepartamento(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTodos_multiplesEmpleados() {
        Empleado e2 = new Empleado();
        e2.setId(2L); e2.setNombre("María López");
        e2.setEmail("maria@empresa.com");
        e2.setValorHora(new BigDecimal("30.00"));

        when(empleadoRepository.findAll()).thenReturn(List.of(empleado, e2));

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("María López", resultado.get(1).getNombre());
    }
}