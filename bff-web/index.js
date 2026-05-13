const express = require('express');
const cors = require('cors');
const axios = require('axios');
const CircuitBreaker = require('opossum');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// URLs de los microservicios Java
const PROJECTS_SERVICE_URL = 'http://localhost:8081/api/proyectos';
const EMPLOYEES_SERVICE_URL = 'http://localhost:8082/api/empleados';

// Configuración Circuit Breaker
const breakerOptions = {
  timeout: 3000,
  errorThresholdPercentage: 50,
  resetTimeout: 10000
};

// comunicación 
const fetchProjects = () => axios.get(PROJECTS_SERVICE_URL).then(res => res.data);
const saveProject = (data) => axios.post(PROJECTS_SERVICE_URL, data).then(res => res.data);

const fetchEmployees = () => axios.get(EMPLOYEES_SERVICE_URL).then(res => res.data);
const saveEmployee = (data) => axios.post(EMPLOYEES_SERVICE_URL, data).then(res => res.data);

//Circuit Breakers
const projectsGetBreaker = new CircuitBreaker(fetchProjects, breakerOptions);
const projectsPostBreaker = new CircuitBreaker(saveProject, breakerOptions);

const employeesGetBreaker = new CircuitBreaker(fetchEmployees, breakerOptions);
const employeesPostBreaker = new CircuitBreaker(saveEmployee, breakerOptions);

// Fallbacks de emergencia
const fallbackMsg = (msg) => ({ error: msg });
projectsGetBreaker.fallback(() => fallbackMsg('Servicio de Proyectos no disponible (GET)'));
projectsPostBreaker.fallback(() => fallbackMsg('No se pudo guardar el Proyecto (Servicio caído)'));
employeesGetBreaker.fallback(() => fallbackMsg('Servicio de Empleados no disponible (GET)'));
employeesPostBreaker.fallback(() => fallbackMsg('No se pudo guardar el Empleado (Servicio caído)'));

// ENDPOINTS DEL BFF

// Rutas de proyecto
app.get('/api/bff/projects', async (req, res) => {
  try {
    const data = await projectsGetBreaker.fire();
    res.json(data);
  } catch (error) { res.status(500).json({ error: 'Error en BFF' }); }
});

app.post('/api/bff/projects', async (req, res) => {
  try {
    const data = await projectsPostBreaker.fire(req.body);
    res.status(201).json(data);
  } catch (error) { res.status(500).json({ error: 'Error al crear proyecto en BFF' }); }
});

// Ruta de empleados
app.get('/api/bff/employees', async (req, res) => {
  try {
    const data = await employeesGetBreaker.fire();
    res.json(data);
  } catch (error) { res.status(500).json({ error: 'Error en BFF' }); }
});

app.post('/api/bff/employees', async (req, res) => {
  try {
    const data = await employeesPostBreaker.fire(req.body);
    res.status(201).json(data);
  } catch (error) { res.status(500).json({ error: 'Error al crear empleado en BFF' }); }
});

app.listen(PORT, () => {
  console.log(`BFF escuchando en http://localhost:${PORT}`);
});