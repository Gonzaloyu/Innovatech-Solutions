package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoProyectoRepository extends JpaRepository<EstadoProyecto, Long> {
}