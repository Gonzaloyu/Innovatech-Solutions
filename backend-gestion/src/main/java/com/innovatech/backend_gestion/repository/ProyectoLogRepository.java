package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.ProyectoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoLogRepository extends JpaRepository<ProyectoLog, Long> {
    List<ProyectoLog> findByProyectoIdOrderByIdDesc(Long proyectoId);
}