package com.innovatech.backend_reportes.model;

import lombok.Data;

@Data
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String email;
    private DepartamentoDTO departamento;
    private CargoDTO cargo;

    @Data
    public static class DepartamentoDTO {
        private String nombre;
    }

    @Data
    public static class CargoDTO {
        private String nombre;
        private String nivel;
    }
}