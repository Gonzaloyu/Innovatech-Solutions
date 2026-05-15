package com.innovatech.backend_analitico.repository;

import com.innovatech.backend_analitico.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByDepartamentoId(Long departamentoId);
}