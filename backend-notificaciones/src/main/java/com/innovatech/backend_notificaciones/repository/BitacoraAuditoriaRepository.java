package com.innovatech.backend_notificaciones.repository;

import com.innovatech.backend_notificaciones.model.BitacoraAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraAuditoriaRepository extends JpaRepository<BitacoraAuditoria, Long> {
}