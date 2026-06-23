package com.innovatech.backend_gestion.controller;


import com.innovatech.backend_gestion.model.Tarea;
import com.innovatech.backend_gestion.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "*") // Permite la comunicación abierta con el puerto de Vue
public class TareaController {


    @Autowired
    private TareaService tareaService;


    @GetMapping
    public ResponseEntity<List<Tarea>> obtenerTodas() {
        return ResponseEntity.ok(tareaService.obtenerTodas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Tarea> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }


    // NUEVO: GET /api/tareas/proyecto/{proyectoId}
    // Sirve para cargar únicamente las tareas del proyecto seleccionado en el frontend
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Tarea>> obtenerPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(tareaService.obtenerPorProyecto(proyectoId));
    }


    @PostMapping
    public ResponseEntity<Tarea> crearTarea(@RequestBody Tarea tarea) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tareaService.crearTarea(tarea));
    }


    // NUEVO: PUT /api/tareas/{id}
    // Responde al cambio manual de estado ('Pendiente', 'Listo', 'Atrasado') en el selector de Vue
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizarTarea(@PathVariable Long id, @RequestBody Tarea tarea) {
        return ResponseEntity.ok(tareaService.actualizarTarea(id, tarea));
    }


    // NUEVO: DELETE /api/tareas/{id}
    // Se ejecuta al presionar el botón ✕ en la tabla de tareas de Vue
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }
}
