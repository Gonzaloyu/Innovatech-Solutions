package com.innovatech.backend_gestion.repository;


import com.innovatech.backend_gestion.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // Aquí podemos agregar métodos de búsqueda personalizados después
}