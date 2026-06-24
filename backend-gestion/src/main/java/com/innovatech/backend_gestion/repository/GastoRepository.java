package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByProyectoId(Long proyectoId);
}