package com.innovatech.backend_gestion.repository;

import org.springframework.stereotype.Repository;
import com.innovatech.backend_gestion.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEmpleadoId(Long empleadoId);
}