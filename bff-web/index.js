const express = require('express');
const cors = require('cors');
const axios = require('axios');
const CircuitBreaker = require('opossum');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// URLs de los microservicios (variables de entorno para Docker)
const GESTION_URL   = process.env.GESTION_URL   || 'http://localhost:8081/api';
const ANALITICO_URL = process.env.ANALITICO_URL || 'http://localhost:8082/api';

//onfiguración Circuit Breaker
const breakerOptions = {
  timeout: 3000,
  errorThresholdPercentage: 50,
  resetTimeout: 10000
};

//Funciones de comunicación 
// Gestión (MySQL - backend-gestion)
const fetchProyectos      = ()     => axios.get(`${GESTION_URL}/proyectos`).then(r => r.data);
const saveProyecto        = (data) => axios.post(`${GESTION_URL}/proyectos`, data).then(r => r.data);
const fetchTareas         = ()     => axios.get(`${GESTION_URL}/tareas`).then(r => r.data);
const fetchTareaPorId     = (id)   => axios.get(`${GESTION_URL}/tareas/${id}`).then(r => r.data);
const saveTarea           = (data) => axios.post(`${GESTION_URL}/tareas`, data).then(r => r.data);

// Analítico (PostgreSQL - backend-analitico)
const fetchEmpleados      = ()     => axios.get(`${ANALITICO_URL}/empleados`).then(r => r.data);
const fetchEmpleadoPorId  = (id)   => axios.get(`${ANALITICO_URL}/empleados/${id}`).then(r => r.data);
const saveEmpleado        = (data) => axios.post(`${ANALITICO_URL}/empleados`, data).then(r => r.data);
const fetchAsignaciones   = ()     => axios.get(`${ANALITICO_URL}/asignaciones`).then(r => r.data);
const saveAsignacion      = (data) => axios.post(`${ANALITICO_URL}/asignaciones`, data).then(r => r.data);

//Circuit Breakers 
const proyectosGetBreaker   = new CircuitBreaker(fetchProyectos,    breakerOptions);
const proyectosPostBreaker  = new CircuitBreaker(saveProyecto,      breakerOptions);
const tareasGetBreaker      = new CircuitBreaker(fetchTareas,       breakerOptions);
const tareasPostBreaker     = new CircuitBreaker(saveTarea,         breakerOptions);
const empleadosGetBreaker   = new CircuitBreaker(fetchEmpleados,    breakerOptions);
const empleadosPostBreaker  = new CircuitBreaker(saveEmpleado,      breakerOptions);
const asignacionPostBreaker = new CircuitBreaker(saveAsignacion,    breakerOptions);

//Fallbacks 
const fallback = (msg) => ({ error: msg });
proyectosGetBreaker.fallback(()  => fallback('Servicio de Proyectos no disponible'));
proyectosPostBreaker.fallback(() => fallback('No se pudo guardar el Proyecto'));
tareasGetBreaker.fallback(()     => fallback('Servicio de Tareas no disponible'));
tareasPostBreaker.fallback(()    => fallback('No se pudo guardar la Tarea'));
empleadosGetBreaker.fallback(()  => fallback('Servicio de Empleados no disponible'));
empleadosPostBreaker.fallback(() => fallback('No se pudo guardar el Empleado'));
asignacionPostBreaker.fallback(()=> fallback('No se pudo guardar la Asignación'));

// ─── Logging de eventos Circuit Breaker ──────────────────────────────────────
const logCircuit = (name, breaker) => {
  breaker.on('open',     () => console.warn(`⚡ [${name}] Circuit ABIERTO - servicio caído`));
  breaker.on('halfOpen', () => console.log(`🔄 [${name}] Circuit SEMI-ABIERTO - reintentando`));
  breaker.on('close',    () => console.log(`✅ [${name}] Circuit CERRADO - servicio recuperado`));
};

logCircuit('proyectos-GET',  proyectosGetBreaker);
logCircuit('proyectos-POST', proyectosPostBreaker);
logCircuit('tareas-GET',     tareasGetBreaker);
logCircuit('tareas-POST',    tareasPostBreaker);
logCircuit('empleados-GET',  empleadosGetBreaker);
logCircuit('empleados-POST', empleadosPostBreaker);
logCircuit('asignacion-POST',asignacionPostBreaker);


// ENDPOINTS DEL BFF

//Health Check 
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    circuits: {
      proyectosGet:  proyectosGetBreaker.opened  ? 'OPEN' : 'CLOSED',
      tareasGet:     tareasGetBreaker.opened     ? 'OPEN' : 'CLOSED',
      empleadosGet:  empleadosGetBreaker.opened  ? 'OPEN' : 'CLOSED',
    }
  });
});

//Proyectos
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
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error proyectos POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear proyecto' });
  }
});

//tareas
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

// API Composition: Tareas con Empleado
app.get('/api/bff/tareas-con-empleado', async (req, res) => {
  try {
    // 1. Obtener todas las tareas desde backend-gestion
    const tareas = await tareasGetBreaker.fire();

    // 2. IDs únicos de empleados (Set evita duplicados)
    const empleadoIds = [...new Set(
      tareas.map(t => t.empleadoId).filter(id => id != null)
    )];

    // 3. Buscar cada empleado en paralelo con Circuit Breaker individual
    const empleadosData = await Promise.all(
      empleadoIds.map(id => {
        const breaker = new CircuitBreaker(
          () => fetchEmpleadoPorId(id),
          breakerOptions
        );
        breaker.fallback(() => null);
        return breaker.fire();
      })
    );

    // 4. Mapa id -> empleado para cruce O(1)
    const empleadosMap = {};
    empleadosData
      .filter(emp => emp !== null)
      .forEach(emp => { empleadosMap[emp.id] = emp; });

    // 5. Unir tarea + empleado
    const tareasDetalladas = tareas.map(tarea => ({
      ...tarea,
      empleado: empleadosMap[tarea.empleadoId] || null
    }));

    res.json(tareasDetalladas);

  } catch (error) {
    console.error('BFF Composition Error:', error.message);
    res.status(500).json({ error: 'Error al consolidar tareas con empleados' });
  }
});

//API Composition: Proyectos con Cliente y Estado ─
app.get('/api/bff/proyectos-detalle', async (req, res) => {
  try {
    // Los proyectos ya traen cliente y estado por las relaciones JPA
    const proyectos = await proyectosGetBreaker.fire();
    res.json(proyectos);
  } catch (error) {
    console.error('BFF Error proyectos-detalle:', error.message);
    res.status(500).json({ error: 'Error al obtener detalle de proyectos' });
  }
});

//API Composition: Tarea por ID con Empleado 
app.get('/api/bff/tareas-con-empleado/:id', async (req, res) => {
  try {
    // 1. Buscar tarea específica
    const tareaBreaker = new CircuitBreaker(
      () => fetchTareaPorId(req.params.id),
      breakerOptions
    );
    tareaBreaker.fallback(() => null);
    const tarea = await tareaBreaker.fire();

    if (!tarea) {
      return res.status(404).json({ error: 'Tarea no encontrada' });
    }

    // 2. Buscar empleado si tiene asignado
    let empleado = null;
    if (tarea.empleadoId) {
      const empBreaker = new CircuitBreaker(
        () => fetchEmpleadoPorId(tarea.empleadoId),
        breakerOptions
      );
      empBreaker.fallback(() => null);
      empleado = await empBreaker.fire();
    }

    // 3. Unir y responder
    res.json({ ...tarea, empleado });

  } catch (error) {
    console.error('BFF Error tarea detalle:', error.message);
    res.status(500).json({ error: 'Error al obtener detalle de tarea' });
  }
});

// ─────────────────────────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log(`BFF escuchando en http://localhost:${PORT}`);
});