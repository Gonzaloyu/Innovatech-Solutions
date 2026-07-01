package com.innovatech.backend_analitico;

import com.innovatech.backend_analitico.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ModelosAnaliticoTest {

    @Test
    void departamento_constructorCompleto() {
        Departamento d = new Departamento(1L, "Tecnología");
        assertEquals(1L, d.getId());
        assertEquals("Tecnología", d.getNombre());
    }

    @Test
    void departamento_gettersSetters() {
        Departamento d = new Departamento();
        d.setId(2L);
        d.setNombre("RRHH");
        assertEquals(2L, d.getId());
        assertEquals("RRHH", d.getNombre());
    }

    @Test
    void departamento_constructorVacio() {
        Departamento d = new Departamento();
        assertNull(d.getId());
        assertNull(d.getNombre());
    }

    @Test
    void cargo_constructorCompleto() {
        Cargo c = new Cargo(1L, "Desarrollador", "Senior");
        assertEquals(1L, c.getId());
        assertEquals("Desarrollador", c.getNombre());
        assertEquals("Senior", c.getNivel());
    }

    @Test
    void cargo_gettersSetters() {
        Cargo c = new Cargo();
        c.setId(3L);
        c.setNombre("Tester");
        c.setNivel("Junior");
        assertEquals(3L, c.getId());
        assertEquals("Tester", c.getNombre());
        assertEquals("Junior", c.getNivel());
    }

    @Test
    void cargo_nivelesDistintos() {
        Cargo junior    = new Cargo(1L, "Dev", "Junior");
        Cargo semiSenior = new Cargo(2L, "Dev", "Semi-Senior");
        Cargo senior    = new Cargo(3L, "Dev", "Senior");

        assertEquals("Junior",      junior.getNivel());
        assertEquals("Semi-Senior", semiSenior.getNivel());
        assertEquals("Senior",      senior.getNivel());
    }

    @Test
    void empleado_constructorCompleto() {
        Departamento dep = new Departamento(1L, "IT");
        Cargo cargo = new Cargo(1L, "Dev", "Senior");
        Empleado e = new Empleado(1L, "Ana", "ana@empresa.com", dep, cargo, new BigDecimal("30.00"));

        assertEquals(1L, e.getId());
        assertEquals("Ana", e.getNombre());
        assertEquals("ana@empresa.com", e.getEmail());
        assertEquals("IT", e.getDepartamento().getNombre());
        assertEquals("Senior", e.getCargo().getNivel());
        assertEquals(new BigDecimal("30.00"), e.getValorHora());
    }

    @Test
    void empleado_gettersSetters() {
        Empleado e = new Empleado();
        e.setId(5L);
        e.setNombre("Pedro García");
        e.setEmail("pedro@empresa.com");
        e.setValorHora(new BigDecimal("20.50"));

        assertEquals(5L, e.getId());
        assertEquals("Pedro García", e.getNombre());
        assertEquals("pedro@empresa.com", e.getEmail());
        assertEquals(new BigDecimal("20.50"), e.getValorHora());
    }

    @Test
    void empleado_setDepartamentoYCargo() {
        Empleado e = new Empleado();
        Departamento dep = new Departamento(2L, "Finanzas");
        Cargo cargo = new Cargo(2L, "Analista", "Semi-Senior");

        e.setDepartamento(dep);
        e.setCargo(cargo);

        assertEquals("Finanzas", e.getDepartamento().getNombre());
        assertEquals("Analista", e.getCargo().getNombre());
    }

    @Test
    void empleado_valorHoraDistintos() {
        Empleado junior = new Empleado();
        junior.setValorHora(new BigDecimal("15.00"));

        Empleado senior = new Empleado();
        senior.setValorHora(new BigDecimal("45.00"));

        assertTrue(senior.getValorHora().compareTo(junior.getValorHora()) > 0);
    }

    @Test
    void asignacion_gettersSetters() {
        Asignacion a = new Asignacion();
        a.setId(1L);
        a.setProyectoId(2L);
        a.setHorasAsignadas(80);
        a.setEstado("En Ejecución");
        a.setComentario("Avanzando bien");
        a.setHerramientasUtilizadas("VSCode, Docker");
        a.setCostoHerramientas(new BigDecimal("300.00"));
        a.setCostoTotalCalculado(new BigDecimal("2300.00"));
        a.setFechaInicio(LocalDate.of(2026, Month.JANUARY, 1));
        a.setFechaFin(LocalDate.of(2026, Month.JUNE, 30));

        assertEquals(1L, a.getId());
        assertEquals(2L, a.getProyectoId());
        assertEquals(80, a.getHorasAsignadas());
        assertEquals("En Ejecución", a.getEstado());
        assertEquals("Avanzando bien", a.getComentario());
        assertEquals("VSCode, Docker", a.getHerramientasUtilizadas());
        assertEquals(new BigDecimal("300.00"), a.getCostoHerramientas());
        assertEquals(new BigDecimal("2300.00"), a.getCostoTotalCalculado());
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), a.getFechaInicio());
        assertEquals(LocalDate.of(2026, Month.JUNE, 30), a.getFechaFin());
    }

    @Test
    void asignacion_estadoPorDefecto() {
        Asignacion a = new Asignacion();
        a.setEstado("Asignado");
        assertEquals("Asignado", a.getEstado());
    }

    @Test
    void asignacion_setEmpleado() {
        Asignacion a = new Asignacion();
        Empleado emp = new Empleado();
        emp.setId(1L);
        emp.setNombre("Luis");
        emp.setValorHora(new BigDecimal("25.00"));

        a.setEmpleado(emp);

        assertEquals("Luis", a.getEmpleado().getNombre());
        assertEquals(new BigDecimal("25.00"), a.getEmpleado().getValorHora());
    }

    @Test
    void asignacion_costosTotales() {
        Asignacion a = new Asignacion();
        a.setCostoHerramientas(new BigDecimal("500.00"));
        a.setCostoTotalCalculado(new BigDecimal("2500.00"));

        assertTrue(a.getCostoTotalCalculado().compareTo(a.getCostoHerramientas()) > 0);
    }

    @Test
    void asignacion_constructorCompleto() {
        Empleado emp = new Empleado();
        emp.setId(1L);

        Asignacion a = new Asignacion(
                1L, emp, 1L, 80, "Asignado",
                LocalDate.of(2026, Month.JANUARY, 1),
                LocalDate.of(2026, Month.DECEMBER, 31),
                "Comentario", "Herramientas",
                new BigDecimal("100.00"), new BigDecimal("2100.00")
        );

        assertEquals(1L, a.getId());
        assertEquals(80, a.getHorasAsignadas());
        assertEquals("Asignado", a.getEstado());
        assertEquals(new BigDecimal("100.00"), a.getCostoHerramientas());
        assertEquals(new BigDecimal("2100.00"), a.getCostoTotalCalculado());
    }
}