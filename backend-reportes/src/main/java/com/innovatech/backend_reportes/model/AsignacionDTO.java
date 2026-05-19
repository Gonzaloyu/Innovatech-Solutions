package com.innovatech.backend_reportes.model;

import lombok.Data;

@Data
public class AsignacionDTO {
    private Long id;
    private Long proyectoId;
    private Integer horasAsignadas;
    private EmpleadoDTO empleado;
}