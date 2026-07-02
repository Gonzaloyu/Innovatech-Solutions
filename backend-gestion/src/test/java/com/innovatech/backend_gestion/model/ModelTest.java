package com.innovatech.backend_gestion.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void cliente_ConstructorVacio_DebeCrearInstancia() {
        Cliente c = new Cliente();
        assertNotNull(c);
    }

    @Test
    void cliente_ConstructorCompleto_DebeAsignarTodosLosCampos() {
        Cliente c = new Cliente(1L, "Empresa ABC", "12345678-9", "Juan Perez", "juan@empresa.com");
        assertEquals(1L, c.getId());
        assertEquals("Empresa ABC", c.getNombreEmpresa());
        assertEquals("12345678-9", c.getRutEmpresa());
        assertEquals("Juan Perez", c.getContacto());
        assertEquals("juan@empresa.com", c.getEmailContacto());
    }

    @Test
    void cliente_SettersYGetters_DebenFuncionar() {
        Cliente c = new Cliente();
        c.setId(2L);
        c.setNombreEmpresa("Tech S.A.");
        c.setRutEmpresa("98765432-1");
        c.setContacto("Maria Lopez");
        c.setEmailContacto("maria@tech.com");

        assertEquals(2L, c.getId());
        assertEquals("Tech S.A.", c.getNombreEmpresa());
        assertEquals("98765432-1", c.getRutEmpresa());
        assertEquals("Maria Lopez", c.getContacto());
        assertEquals("maria@tech.com", c.getEmailContacto());
    }

    @Test
    void cliente_Equals_DosInstanciasConMismoCampos_DebenSerIguales() {
        Cliente c1 = new Cliente(1L, "Empresa", "12345678-9", "Juan", "juan@a.com");
        Cliente c2 = new Cliente(1L, "Empresa", "12345678-9", "Juan", "juan@a.com");
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void cliente_ToString_DebeContenerDatos() {
        Cliente c = new Cliente(1L, "Empresa ABC", "12345678-9", "Juan", "juan@a.com");
        String toString = c.toString();
        assertTrue(toString.contains("Empresa ABC"));
    }

    @Test
    void categoria_ConstructorVacio_DebeCrearInstancia() {
        Categoria c = new Categoria();
        assertNotNull(c);
    }

    @Test
    void categoria_ConstructorCompleto_DebeAsignarCampos() {
        Categoria c = new Categoria(1L, "Software");
        assertEquals(1L, c.getId());
        assertEquals("Software", c.getNombre());
    }

    @Test
    void categoria_SettersYGetters_DebenFuncionar() {
        Categoria c = new Categoria();
        c.setId(3L);
        c.setNombre("Infraestructura");
        assertEquals(3L, c.getId());
        assertEquals("Infraestructura", c.getNombre());
    }

    @Test
    void categoria_Equals_MismosDatos_DebenSerIguales() {
        Categoria c1 = new Categoria(1L, "Software");
        Categoria c2 = new Categoria(1L, "Software");
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void categoria_ToString_DebeContenerNombre() {
        Categoria c = new Categoria(1L, "Consultoría");
        assertTrue(c.toString().contains("Consultoría"));
    }

    @Test
    void estadoProyecto_ConstructorVacio_DebeCrearInstancia() {
        EstadoProyecto e = new EstadoProyecto();
        assertNotNull(e);
    }

    @Test
    void estadoProyecto_ConstructorCompleto_DebeAsignarCampos() {
        EstadoProyecto e = new EstadoProyecto(1L, "En Ejecución");
        assertEquals(1L, e.getId());
        assertEquals("En Ejecución", e.getNombre());
    }

    @Test
    void estadoProyecto_SettersYGetters_DebenFuncionar() {
        EstadoProyecto e = new EstadoProyecto();
        e.setId(2L);
        e.setNombre("Finalizado");
        assertEquals(2L, e.getId());
        assertEquals("Finalizado", e.getNombre());
    }

    @Test
    void estadoProyecto_Equals_MismosDatos_DebenSerIguales() {
        EstadoProyecto e1 = new EstadoProyecto(1L, "Finalizado");
        EstadoProyecto e2 = new EstadoProyecto(1L, "Finalizado");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void asignacion_SettersYGetters_DebenFuncionar() {
        Asignacion a = new Asignacion();
        a.setId(1L);
        a.setProyectoId(10L);
        a.setEmpleadoId(5L);
        a.setRol("Desarrollador");

        assertEquals(1L, a.getId());
        assertEquals(10L, a.getProyectoId());
        assertEquals(5L, a.getEmpleadoId());
        assertEquals("Desarrollador", a.getRol());
    }

    @Test
    void asignacion_Equals_MismosDatos_DebenSerIguales() {
        Asignacion a1 = new Asignacion();
        a1.setId(1L);
        a1.setProyectoId(10L);
        a1.setEmpleadoId(5L);
        a1.setRol("Desarrollador");

        Asignacion a2 = new Asignacion();
        a2.setId(1L);
        a2.setProyectoId(10L);
        a2.setEmpleadoId(5L);
        a2.setRol("Desarrollador");

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void asignacion_ToString_DebeContenerRol() {
        Asignacion a = new Asignacion();
        a.setRol("Tester");
        assertTrue(a.toString().contains("Tester"));
    }

    @Test
    void tarea_SettersYGetters_DebenFuncionar() {
        Tarea t = new Tarea();
        t.setId(1L);
        t.setNombre("Implementar login");
        t.setDescripcion("Crear el módulo de autenticación");
        t.setEstado("En progreso");
        t.setAsignacionId(3L);
        t.setEmpleadoId(7L);
        t.setFechaInicio(LocalDate.of(2026, Month.JANUARY, 1));
        t.setFechaLimite(LocalDate.of(2026, Month.JUNE, 30));

        assertEquals(1L, t.getId());
        assertEquals("Implementar login", t.getNombre());
        assertEquals("Crear el módulo de autenticación", t.getDescripcion());
        assertEquals("En progreso", t.getEstado());
        assertEquals(3L, t.getAsignacionId());
        assertEquals(7L, t.getEmpleadoId());
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), t.getFechaInicio());
        assertEquals(LocalDate.of(2026, Month.JUNE, 30), t.getFechaLimite());
    }

    @Test
    void tarea_SetProyecto_DebeAsignarProyecto() {
        Tarea t = new Tarea();
        Proyecto p = new Proyecto();
        p.setId(5L);
        t.setProyecto(p);
        assertEquals(5L, t.getProyecto().getId());
    }

    @Test
    void tarea_Equals_MismosDatos_DebenSerIguales() {
        Tarea t1 = new Tarea();
        t1.setId(1L);
        t1.setNombre("Login");

        Tarea t2 = new Tarea();
        t2.setId(1L);
        t2.setNombre("Login");

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void tarea_ToString_DebeContenerNombre() {
        Tarea t = new Tarea();
        t.setNombre("Diseñar BD");
        assertTrue(t.toString().contains("Diseñar BD"));
    }

    @Test
    void proyectoLog_ConstructorVacio_DebeCrearInstancia() {
        ProyectoLog log = new ProyectoLog();
        assertNotNull(log);
    }

    @Test
    void proyectoLog_ConstructorCompleto_DebeAsignarCampos() {
        Proyecto p = new Proyecto();
        ProyectoLog log = new ProyectoLog(1L, "10:00", "Proyecto creado", "2026-07-01", p);
        assertEquals(1L, log.getId());
        assertEquals("10:00", log.getHora());
        assertEquals("Proyecto creado", log.getMensaje());
        assertEquals("2026-07-01", log.getFecha());
        assertEquals(p, log.getProyecto());
    }

    @Test
    void proyectoLog_SettersYGetters_DebenFuncionar() {
        ProyectoLog log = new ProyectoLog();
        log.setId(2L);
        log.setHora("14:30");
        log.setMensaje("Estado actualizado");
        log.setFecha("2026-07-01");

        assertEquals(2L, log.getId());
        assertEquals("14:30", log.getHora());
        assertEquals("Estado actualizado", log.getMensaje());
        assertEquals("2026-07-01", log.getFecha());
    }

    @Test
    void proyectoLog_SetProyecto_DebeAsignarProyecto() {
        ProyectoLog log = new ProyectoLog();
        Proyecto p = new Proyecto();
        p.setId(10L);
        log.setProyecto(p);
        assertEquals(10L, log.getProyecto().getId());
    }

    @Test
    void comentario_SettersYGetters_DebenFuncionar() {
        Comentario c = new Comentario();
        c.setId(1L);
        c.setTexto("Buen avance en el proyecto");
        c.setAutor("Carlos");
        c.setFechaCreacion(LocalDateTime.of(2026, Month.JULY, 1, 10, 0));

        assertEquals(1L, c.getId());
        assertEquals("Buen avance en el proyecto", c.getTexto());
        assertEquals("Carlos", c.getAutor());
        assertNotNull(c.getFechaCreacion());
    }

    @Test
    void comentario_SetProyecto_DebeAsignarProyecto() {
        Comentario c = new Comentario();
        Proyecto p = new Proyecto();
        p.setId(3L);
        c.setProyecto(p);
        assertEquals(3L, c.getProyecto().getId());
    }

    @Test
    void comentario_PrePersist_DebeAsignarFechaCreacion() {
        Comentario c = new Comentario();
        assertNull(c.getFechaCreacion());
        c.prePersist();
        assertNotNull(c.getFechaCreacion());
    }

    @Test
    void comentario_Equals_MismosDatos_DebenSerIguales() {
        Comentario c1 = new Comentario();
        c1.setId(1L);
        c1.setTexto("Hola");
        c1.setAutor("Juan");

        Comentario c2 = new Comentario();
        c2.setId(1L);
        c2.setTexto("Hola");
        c2.setAutor("Juan");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void proyecto_ConstructorVacio_DebeCrearInstancia() {
        Proyecto p = new Proyecto();
        assertNotNull(p);
    }

    @Test
    void proyecto_SettersYGetters_DebenFuncionar() {
        Proyecto p = new Proyecto();
        p.setId(1L);
        p.setNombre("SIDIST");
        p.setDescripcion("Sistema de distribución");
        p.setFechaInicio(LocalDate.of(2026, Month.JANUARY, 1));
        p.setFechaFin(LocalDate.of(2026, Month.DECEMBER, 31));
        p.setNombreEquipo("Equipo Alpha");
        p.setEmpleadoId(5L);

        assertEquals(1L, p.getId());
        assertEquals("SIDIST", p.getNombre());
        assertEquals("Sistema de distribución", p.getDescripcion());
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), p.getFechaInicio());
        assertEquals(LocalDate.of(2026, Month.DECEMBER, 31), p.getFechaFin());
        assertEquals("Equipo Alpha", p.getNombreEquipo());
        assertEquals(5L, p.getEmpleadoId());
    }

    @Test
    void proyecto_SetRelaciones_DebenAsignarse() {
        Proyecto p = new Proyecto();

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        p.setCliente(cliente);

        EstadoProyecto estado = new EstadoProyecto();
        estado.setId(2L);
        p.setEstado(estado);

        Categoria categoria = new Categoria();
        categoria.setId(3L);
        p.setCategoria(categoria);

        p.setAsignaciones(new HashSet<>());
        p.setTareas(new HashSet<>());
        p.setLogs(new ArrayList<>());

        assertEquals(1L, p.getCliente().getId());
        assertEquals(2L, p.getEstado().getId());
        assertEquals(3L, p.getCategoria().getId());
        assertNotNull(p.getAsignaciones());
        assertNotNull(p.getTareas());
        assertNotNull(p.getLogs());
    }

    @Test
    void proyecto_Equals_MismosDatos_DebenSerIguales() {
        Proyecto p1 = new Proyecto();
        p1.setId(1L);
        p1.setNombre("SIDIST");

        Proyecto p2 = new Proyecto();
        p2.setId(1L);
        p2.setNombre("SIDIST");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void proyecto_ToString_DebeContenerNombre() {
        Proyecto p = new Proyecto();
        p.setNombre("Innovatech Web");
        assertTrue(p.toString().contains("Innovatech Web"));
    }
}