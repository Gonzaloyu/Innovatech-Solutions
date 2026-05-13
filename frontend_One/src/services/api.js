import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:3000/api/bff', // La URL de BFF
  headers: {
    'Content-Type': 'application/json'
  }
});

export default {
  // PROYECTOS 
  getProjects() {
    return apiClient.get('/projects');
  },
  createProject(proyecto) {
    return apiClient.post('/projects', proyecto); 
  },

  //EMPLEADOS 
  getEmployees() {
    return apiClient.get('/employees');
  },
  createEmployee(empleado) {
    return apiClient.post('/employees', empleado); 
  }
};