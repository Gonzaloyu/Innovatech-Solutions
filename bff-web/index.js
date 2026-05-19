const express = require('express');
const cors = require('cors');
const axios = require('axios');
const CircuitBreaker = require('opossum');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const GESTION_URL   = process.env.GESTION_URL   || 'http://localhost:8081/api';
const ANALITICO_URL = process.env.ANALITICO_URL || 'http://localhost:8082/api';
const REPORTES_URL  = process.env.REPORTES_URL  || 'http://localhost:8083/api';

const breakerOptions = {
  timeout: 5000,
  errorThresholdPercentage: 50,
  resetTimeout: 10000
};

const fetchProyectos      = ()     => axios.get(`${GESTION_URL}/proyectos`).then(r => r.data);
const saveProyecto        = (data) => axios.post(`${GESTION_URL}/proyectos`, data).then(r => r.data);
const fetchTareas         = ()     => axios.get(`${GESTION_URL}/tareas`).then(r => r.data);
const fetchTareaPorId     = (id)   => axios.get(`${GESTION_URL}/tareas/${id}`).then(r => r.data);
const saveTarea           = (data) => axios.post(`${GESTION_URL}/tareas`, data).then(r => r.data);
const fetchEmpleados      = ()     => axios.get(`${ANALITICO_URL}/empleados`).then(r => r.data);
const fetchEmpleadoPorId  = (id)   => axios.get(`${ANALITICO_URL}/empleados/${id}`).then(r => r.data);
const saveEmpleado        = (data) => axios.post(`${ANALITICO_URL}/empleados`, data).then(r => r.data);
const saveAsignacion      = (data) => axios.post(`${ANALITICO_URL}/asignaciones`, data).then(r => r.data);
const fetchKpis           = ()     => axios.get(`${REPORTES_URL}/reportes/kpis`).then(r => r.data);
const fetchEmpPorDepto    = ()     => axios.get(`${REPORTES_URL}/reportes/empleados-por-departamento`).then(r => r.data);
const fetchProjPorEstado  = ()     => axios.get(`${REPORTES_URL}/reportes/proyectos-por-estado`).then(r => r.data);
const fetchProjPorCat     = ()     => axios.get(`${REPORTES_URL}/reportes/proyectos-por-categoria`).then(r => r.data);

const proyectosGetBreaker   = new CircuitBreaker(fetchProyectos,     breakerOptions);
const proyectosPostBreaker  = new CircuitBreaker(saveProyecto,       breakerOptions);
const tareasGetBreaker      = new CircuitBreaker(fetchTareas,        breakerOptions);
const tareasPostBreaker     = new CircuitBreaker(saveTarea,          breakerOptions);
const empleadosGetBreaker   = new CircuitBreaker(fetchEmpleados,     breakerOptions);
const empleadosPostBreaker  = new CircuitBreaker(saveEmpleado,       breakerOptions);
const asignacionPostBreaker = new CircuitBreaker(saveAsignacion,     breakerOptions);
const kpisBreaker           = new CircuitBreaker(fetchKpis,          breakerOptions);
const empPorDeptoBreaker    = new CircuitBreaker(fetchEmpPorDepto,   breakerOptions);
const projPorEstadoBreaker  = new CircuitBreaker(fetchProjPorEstado, breakerOptions);
const projPorCatBreaker     = new CircuitBreaker(fetchProjPorCat,    breakerOptions);

const fallback = (msg) => ({ error: msg });
proyectosGetBreaker.fallback(()   => fallback('Servicio de Proyectos no disponible'));
proyectosPostBreaker.fallback(()  => fallback('No se pudo guardar el Proyecto'));
tareasGetBreaker.fallback(()      => fallback('Servicio de Tareas no disponible'));
tareasPostBreaker.fallback(()     => fallback('No se pudo guardar la Tarea'));
empleadosGetBreaker.fallback(()   => fallback('Servicio de Empleados no disponible'));
empleadosPostBreaker.fallback(()  => fallback('No se pudo guardar el Empleado'));
asignacionPostBreaker.fallback(() => fallback('No se pudo guardar la Asignación'));
kpisBreaker.fallback(() => ({
  totalProyectos: 0, totalEmpleados: 0,
  totalAsignaciones: 0, proyectosEnEjecucion: 0,
  proyectosAtrasados: 0
}));
empPorDeptoBreaker.fallback(()   => ({}));
projPorEstadoBreaker.fallback(() => ({}));
projPorCatBreaker.fallback(()    => ({}));

const logCircuit = (name, breaker) => {
  breaker.on('open',     () => console.warn(`[${name}] Circuit ABIERTO - servicio caído`));
  breaker.on('halfOpen', () => console.log(`[${name}] Circuit SEMI-ABIERTO - reintentando`));
  breaker.on('close',    () => console.log(`[${name}] Circuit CERRADO - servicio recuperado`));
};

logCircuit('proyectos-GET',       proyectosGetBreaker);
logCircuit('proyectos-POST',      proyectosPostBreaker);
logCircuit('tareas-GET',          tareasGetBreaker);
logCircuit('tareas-POST',         tareasPostBreaker);
logCircuit('empleados-GET',       empleadosGetBreaker);
logCircuit('empleados-POST',      empleadosPostBreaker);
logCircuit('asignacion-POST',     asignacionPostBreaker);
logCircuit('kpis-GET',            kpisBreaker);
logCircuit('emp-por-depto-GET',   empPorDeptoBreaker);
logCircuit('proj-por-estado-GET', projPorEstadoBreaker);
logCircuit('proj-por-cat-GET',    projPorCatBreaker);

// Health Check
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    circuits: {
      proyectosGet: proyectosGetBreaker.opened ? 'OPEN' : 'CLOSED',
      tareasGet:    tareasGetBreaker.opened    ? 'OPEN' : 'CLOSED',
      empleadosGet: empleadosGetBreaker.opened ? 'OPEN' : 'CLOSED',
      kpisGet:      kpisBreaker.opened         ? 'OPEN' : 'CLOSED',
    }
  });
});

// Proyectos
app.get('/api/bff/proyectos', async (req, res) => {
  try {
    const data = await proyectosGetBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error proyectos GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener proyectos' });
  }
});

app.post('/api/bff/proyectos', async (req, res) => {
  try {
    const data = await proyectosPostBreaker.fire(req.body);
    if (data && data.id) return res.status(201).json(data);
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error proyectos POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear proyecto' });
  }
});

// Proyectos por empleado
app.get('/api/bff/proyectos/empleado/:empleadoId', async (req, res) => {
  try {
    const data = await axios.get(
      `${GESTION_URL}/proyectos/empleado/${req.params.empleadoId}`
    );
    res.json(data.data);
  } catch (error) {
    console.error('BFF Error proyectos por empleado:', error.message);
    res.status(500).json({ error: 'Error al obtener proyectos del empleado' });
  }
});

// Actualizar estado de proyecto
app.patch('/api/bff/proyectos/:id/estado', async (req, res) => {
  try {
    const data = await axios.patch(
      `${GESTION_URL}/proyectos/${req.params.id}/estado`,
      req.body
    );
    res.json(data.data);
  } catch (error) {
    console.error('BFF Error actualizar estado:', error.message);
    res.status(500).json({ error: 'Error al actualizar estado' });
  }
});

// Tareas
app.get('/api/bff/tareas', async (req, res) => {
  try {
    const data = await tareasGetBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error tareas GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener tareas' });
  }
});

app.post('/api/bff/tareas', async (req, res) => {
  try {
    const data = await tareasPostBreaker.fire(req.body);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error tareas POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear tarea' });
  }
});

// Empleados
app.get('/api/bff/empleados', async (req, res) => {
  try {
    const data = await empleadosGetBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error empleados GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener empleados' });
  }
});

app.post('/api/bff/empleados', async (req, res) => {
  try {
    const data = await empleadosPostBreaker.fire(req.body);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error empleados POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear empleado' });
  }
});

// Asignaciones
app.post('/api/bff/asignaciones', async (req, res) => {
  try {
    const data = await asignacionPostBreaker.fire(req.body);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error asignaciones POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear asignación' });
  }
});

// KPIs
app.get('/api/bff/kpis', async (req, res) => {
  try {
    const data = await kpisBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error kpis GET:', error.message);
    res.status(500).json({ error: 'Error al obtener KPIs' });
  }
});

app.get('/api/bff/reportes/empleados-por-departamento', async (req, res) => {
  try {
    const data = await empPorDeptoBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error emp-por-depto:', error.message);
    res.status(500).json({ error: 'Error al obtener reporte' });
  }
});

app.get('/api/bff/reportes/proyectos-por-estado', async (req, res) => {
  try {
    const data = await projPorEstadoBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error proj-por-estado:', error.message);
    res.status(500).json({ error: 'Error al obtener reporte' });
  }
});

app.get('/api/bff/reportes/proyectos-por-categoria', async (req, res) => {
  try {
    const data = await projPorCatBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error proj-por-cat:', error.message);
    res.status(500).json({ error: 'Error al obtener reporte' });
  }
});

// API Composition
app.get('/api/bff/tareas-con-empleado', async (req, res) => {
  try {
    const tareas = await tareasGetBreaker.fire();
    const empleadoIds = [...new Set(
      tareas.map(t => t.empleadoId).filter(id => id != null)
    )];
    const empleadosData = await Promise.all(
      empleadoIds.map(id => {
        const breaker = new CircuitBreaker(
          () => fetchEmpleadoPorId(id), breakerOptions
        );
        breaker.fallback(() => null);
        return breaker.fire();
      })
    );
    const empleadosMap = {};
    empleadosData.filter(e => e !== null).forEach(e => { empleadosMap[e.id] = e; });
    const tareasDetalladas = tareas.map(t => ({
      ...t, empleado: empleadosMap[t.empleadoId] || null
    }));
    res.json(tareasDetalladas);
  } catch (error) {
    console.error('BFF Composition Error:', error.message);
    res.status(500).json({ error: 'Error al consolidar tareas con empleados' });
  }
});

app.get('/api/bff/tareas-con-empleado/:id', async (req, res) => {
  try {
    const tareaBreaker = new CircuitBreaker(
      () => fetchTareaPorId(req.params.id), breakerOptions
    );
    tareaBreaker.fallback(() => null);
    const tarea = await tareaBreaker.fire();
    if (!tarea) return res.status(404).json({ error: 'Tarea no encontrada' });
    let empleado = null;
    if (tarea.empleadoId) {
      const empBreaker = new CircuitBreaker(
        () => fetchEmpleadoPorId(tarea.empleadoId), breakerOptions
      );
      empBreaker.fallback(() => null);
      empleado = await empBreaker.fire();
    }
    res.json({ ...tarea, empleado });
  } catch (error) {
    console.error('BFF Error tarea detalle:', error.message);
    res.status(500).json({ error: 'Error al obtener detalle de tarea' });
  }
});

app.get('/api/bff/proyectos-detalle', async (req, res) => {
  try {
    const proyectos = await proyectosGetBreaker.fire();
    res.json(proyectos);
  } catch (error) {
    console.error('BFF Error proyectos-detalle:', error.message);
    res.status(500).json({ error: 'Error al obtener detalle de proyectos' });
  }
});

app.listen(PORT, () => {
  console.log(`BFF escuchando en http://localhost:${PORT}`);
});