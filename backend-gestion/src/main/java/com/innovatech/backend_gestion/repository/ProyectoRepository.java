package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
}