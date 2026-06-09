    package com.innovatech.backend_analitico.model;

    import jakarta.persistence.*;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.AllArgsConstructor;

    @Entity
    @Table(name = "cargos")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Cargo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String nombre;

        @Column(nullable = false)
        private String nivel; // "Junior", "Semi-Senior", "Senior"
    }