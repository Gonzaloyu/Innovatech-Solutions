package com.innovatech.backend_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_empresa", nullable = false)
    private String nombreEmpresa;

    @Column(name = "rut_empresa", nullable = false, unique = true)
    private String rutEmpresa;

    @Column(nullable = false)
    private String contacto;

    @Column(name = "email_contacto")
    private String emailContacto;
}