import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_BFF_URL || 'http://localhost:3000/api/bff',
  headers: { 'Content-Type': 'application/json' }
});

export default {
  // PROYECTOS
  getProyectos()         { return apiClient.get('/proyectos'); },
  createProyecto(data)   { return apiClient.post('/proyectos', data); },

  // TAREAS
  getTareas()            { return apiClient.get('/tareas'); },
  createTarea(data)      { return apiClient.post('/tareas', data); },
  getTareasConEmpleado() { return apiClient.get('/tareas-con-empleado'); },

  // EMPLEADOS
  getEmpleados()         { return apiClient.get('/empleados'); },
  createEmpleado(data)   { return apiClient.post('/empleados', data); },

  // ASIGNACIONES (¡MÉTODOS FALTANTES AGREGADOS!)
  createAsignacion(data) { return apiClient.post('/asignaciones', data); },
  
  getAsignacionesPorProyecto(proyectoId) { 
    return apiClient.get(`/asignaciones/proyecto/${proyectoId}`); 
  },
  
  deleteAsignacion(id) { 
    return apiClient.delete(`/asignaciones/${id}`); 
  },

  // KPIs y REPORTES (backend-reportes)
  getKpis()                     { return apiClient.get('/kpis'); },
  getEmpleadosPorDepartamento() { return apiClient.get('/reportes/empleados-por-departamento'); },
  getProyectosPorEstado()       { return apiClient.get('/reportes/proyectos-por-estado'); },
  getProyectosPorCategoria()    { return apiClient.get('/reportes/proyectos-por-categoria'); },

  getProyectosPorEmpleado(empleadoId) { 
    return apiClient.get(`/proyectos/empleado/${empleadoId}`); 
  },

  actualizarEstadoProyecto(id, data) { 
    return apiClient.patch(`/proyectos/${id}/estado`, data); 
  },
  
  getEmpleadoPorId(id) { 
    return apiClient.get(`/empleados/${id}`); 
  }
};