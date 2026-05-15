import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_BFF_URL || 'http://localhost:3000/api/bff',
  headers: { 'Content-Type': 'application/json' }
});

export default {
  getProyectos()         { return apiClient.get('/proyectos'); },
  createProyecto(data)   { return apiClient.post('/proyectos', data); },
  getEmpleados()         { return apiClient.get('/empleados'); },
  createEmpleado(data)   { return apiClient.post('/empleados', data); },
  getTareasConEmpleado() { return apiClient.get('/tareas-con-empleado'); },
  createAsignacion(data) { return apiClient.post('/asignaciones', data); }
};