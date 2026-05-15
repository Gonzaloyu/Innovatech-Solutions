package com.innovatech.backend_gestion.repository;

import com.innovatech.backend_gestion.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Aquí podemos agregar métodos de búsqueda personalizados después
}