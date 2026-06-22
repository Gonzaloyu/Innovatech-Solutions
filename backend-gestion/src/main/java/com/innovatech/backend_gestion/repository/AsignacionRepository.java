package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByProyectoId(Long proyectoId);
}