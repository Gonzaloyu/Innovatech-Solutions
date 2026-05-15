package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.Cliente; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Aquí podemos agregar métodos de búsqueda personalizados después
}