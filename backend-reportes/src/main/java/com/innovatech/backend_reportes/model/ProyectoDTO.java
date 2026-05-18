package com.innovatech.backend_reportes.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProyectoDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoDTO estado;
    private CategoriaDTO categoria;
    private ClienteDTO cliente;

    @Data
    public static class EstadoDTO {
        private String nombre;
    }

    @Data
    public static class CategoriaDTO {
        private String nombre;
    }

    @Data
    public static class ClienteDTO {
        private String nombreEmpresa;
    }
}