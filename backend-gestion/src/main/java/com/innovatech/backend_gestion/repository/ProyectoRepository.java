package com.innovatech.backend_gestion.repository;

import org.springframework.stereotype.Repository;
import com.innovatech.backend_gestion.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    @Query(value = "SELECT DISTINCT p.* FROM proyectos p " +
                   "LEFT JOIN asignaciones a ON p.id = a.proyecto_id " +
                   "WHERE p.empleado_id = :empleadoId OR a.empleado_id = :empleadoId", 
           nativeQuery = true)
    List<Proyecto> findByEmpleadoId(@Param("empleadoId") Long empleadoId);
}