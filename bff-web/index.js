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

// ==================== FUNCIONES CORE ====================
const fetchProyectos                 = ()            => axios.get(`${GESTION_URL}/proyectos`).then(r => r.data);
const saveProyecto                   = (data)        => axios.post(`${GESTION_URL}/proyectos`, data).then(r => r.data);
const fetchProyectosPorEmpleado      = (empleadoId)  => axios.get(`${GESTION_URL}/proyectos/empleado/${empleadoId}`).then(r => r.data);
const patchProyectoEstado            = ({ id, body }) => axios.patch(`${GESTION_URL}/proyectos/${id}/estado`, body).then(r => r.data);

const fetchTareas                    = ()            => axios.get(`${GESTION_URL}/tareas`).then(r => r.data);
const fetchTareaPorId                = (id)          => axios.get(`${GESTION_URL}/tareas/${id}`).then(r => r.data);
const fetchTareasPorProyecto         = (proyectoId)  => axios.get(`${GESTION_URL}/tareas/proyecto/${proyectoId}`).then(r => r.data);
const saveTarea                      = (data)        => axios.post(`${GESTION_URL}/tareas`, data).then(r => r.data);
const updateTarea                    = ({ id, data }) => axios.put(`${GESTION_URL}/tareas/${id}`, data).then(r => r.data);       
const deleteTarea                    = (id)          => axios.delete(`${GESTION_URL}/tareas/${id}`).then(r => r.data);           

const fetchEmpleados                 = ()            => axios.get(`${ANALITICO_URL}/empleados`).then(r => r.data);
const fetchEmpleadoPorId             = (id)          => axios.get(`${ANALITICO_URL}/empleados/${id}`).then(r => r.data);
const saveEmpleado                   = (data)        => axios.post(`${ANALITICO_URL}/empleados`, data).then(r => r.data);

const fetchAsignacionesPorProyecto   = (proyectoId)  => axios.get(`${GESTION_URL}/asignaciones/proyecto/${proyectoId}`).then(r => r.data);
const saveAsignacion                 = (data)        => axios.post(`${GESTION_URL}/asignaciones`, data).then(r => r.data);
const deleteAsignacion               = (id)          => axios.delete(`${GESTION_URL}/asignaciones/${id}`).then(r => r.data);

const fetchKpis                      = ()            => axios.get(`${REPORTES_URL}/reportes/kpis`).then(r => r.data);
const fetchEmpPorDepto               = ()            => axios.get(`${REPORTES_URL}/reportes/empleados-por-departamento`).then(r => r.data);
const fetchProjPorEstado             = ()            => axios.get(`${REPORTES_URL}/reportes/proyectos-por-estado`).then(r => r.data);
const fetchProjPorCat                = ()            => axios.get(`${REPORTES_URL}/reportes/proyectos-por-categoria`).then(r => r.data);

// =================NOMBRE DE EQUIPO ==================
const putProyecto = ({ id, data }) => axios.put(`${GESTION_URL}/proyectos/${id}`, data).then(r => r.data);
const fetchLogs   = (proyectoId)   => axios.get(`${GESTION_URL}/proyectos/${proyectoId}/logs`).then(r => r.data);
const saveLog     = ({ id, data }) => axios.post(`${GESTION_URL}/proyectos/${id}/logs`, data).then(r => r.data);
// ==================== CIRCUIT BREAKERS ====================
const proyectosGetBreaker              = new CircuitBreaker(fetchProyectos,                breakerOptions);
const proyectosPostBreaker             = new CircuitBreaker(saveProyecto,                  breakerOptions);
const proyectosPorEmpleadoBreaker      = new CircuitBreaker(fetchProyectosPorEmpleado,     breakerOptions);
const proyectoPatchEstadoBreaker       = new CircuitBreaker(patchProyectoEstado,           breakerOptions);
const proyectoPutBreaker               = new CircuitBreaker(putProyecto, breakerOptions);
const logsGetBreaker                   = new CircuitBreaker(fetchLogs,   breakerOptions);
const logsPostBreaker                  = new CircuitBreaker(saveLog,     breakerOptions);

// --- Breakers de Tareas ---
const tareasGetBreaker                 = new CircuitBreaker(fetchTareas,                   breakerOptions);
const tareaPorIdBreaker                = new CircuitBreaker(fetchTareaPorId,               breakerOptions);
const tareasPorProyectoBreaker         = new CircuitBreaker(fetchTareasPorProyecto,        breakerOptions);
const tareasPostBreaker                = new CircuitBreaker(saveTarea,                     breakerOptions);
const tareaPutBreaker                  = new CircuitBreaker(updateTarea,                   breakerOptions); 
const tareaDeleteBreaker               = new CircuitBreaker(deleteTarea,                   breakerOptions); 

const empleadosGetBreaker              = new CircuitBreaker(fetchEmpleados,                breakerOptions);
const empleadosPostBreaker             = new CircuitBreaker(saveEmpleado,                  breakerOptions);
const empleadoPorIdBreaker             = new CircuitBreaker(fetchEmpleadoPorId,            breakerOptions);

const asignacionesGetBreaker           = new CircuitBreaker(fetchAsignacionesPorProyecto,  breakerOptions);
const asignacionPostBreaker            = new CircuitBreaker(saveAsignacion,                breakerOptions);
const asignacionDeleteBreaker          = new CircuitBreaker(deleteAsignacion,              breakerOptions);

const kpisBreaker                      = new CircuitBreaker(fetchKpis,                     breakerOptions);
const empPorDeptoBreaker               = new CircuitBreaker(fetchEmpPorDepto,              breakerOptions);
const projPorEstadoBreaker             = new CircuitBreaker(fetchProjPorEstado,            breakerOptions);
const projPorCatBreaker                = new CircuitBreaker(fetchProjPorCat,               breakerOptions);

// ==================== FALLBACKS ====================
const fallback = (msg) => ({ error: msg });


proyectosGetBreaker.fallback(()            => fallback('Servicio de Proyectos no disponible'));
proyectosPostBreaker.fallback(()           => fallback('No se pudo guardar el Proyecto'));
proyectosPorEmpleadoBreaker.fallback(()    => []);
proyectoPatchEstadoBreaker.fallback(()     => fallback('No se pudo actualizar el estado del Proyecto'));

tareasGetBreaker.fallback(()               => fallback('Servicio de Tareas no disponible'));
tareaPorIdBreaker.fallback(()              => null);
tareasPorProyectoBreaker.fallback(()       => []); 
tareasPostBreaker.fallback(()              => fallback('No se pudo guardar la Tarea'));
tareaPutBreaker.fallback(()                => fallback('No se pudo actualizar la Tarea')); 
tareaDeleteBreaker.fallback(()             => fallback('No se pudo eliminar la Tarea'));   

empleadosGetBreaker.fallback(()            => fallback('Servicio de Empleados no disponible'));
empleadosPostBreaker.fallback(()           => fallback('No se pudo guardar el Empleado'));
empleadoPorIdBreaker.fallback(()           => null);

asignacionesGetBreaker.fallback(()         => []);
asignacionPostBreaker.fallback((data)      => ({ ...data, error: 'Circuit Breaker: No se pudo procesar la asignación.' }));
asignacionDeleteBreaker.fallback(()        => fallback('No se pudo eliminar la Asignación'));

proyectoPutBreaker.fallback(() => fallback('No se pudo actualizar el Proyecto'));
logsGetBreaker.fallback(()     => []);
logsPostBreaker.fallback(()    => fallback('No se pudo guardar el log'));

kpisBreaker.fallback(() => ({
  totalProyectos: 0, totalEmpleados: 0,
  totalAsignaciones: 0, proyectosEnEjecucion: 0,
  proyectosAtrasados: 0
}));
empPorDeptoBreaker.fallback(()             => ({}));
projPorEstadoBreaker.fallback(()           => ({}));
projPorCatBreaker.fallback(()              => ({}));

// ==================== LOGS DE MONITOREO ====================
const logCircuit = (name, breaker) => {
  breaker.on('open',     () => console.warn(`[${name}] Circuit ABIERTO - servicio caído`));
  breaker.on('halfOpen', () => console.log(`[${name}] Circuit SEMI-ABIERTO - reintentando`));
  breaker.on('close',    () => console.log(`[${name}] Circuit CERRADO - servicio recuperado`));
};

logCircuit('proyectos-GET',              proyectosGetBreaker);
logCircuit('proyectos-POST',             proyectosPostBreaker);
logCircuit('proyectos-por-empleado-GET', proyectosPorEmpleadoBreaker);
logCircuit('proyectos-patch-estado',     proyectoPatchEstadoBreaker);

logCircuit('tareas-GET',                 tareasGetBreaker);
logCircuit('tarea-por-id-GET',           tareaPorIdBreaker);
logCircuit('tareas-por-proyecto-GET',    tareasPorProyectoBreaker);
logCircuit('tareas-POST',                tareasPostBreaker);
logCircuit('tareas-PUT',                 tareaPutBreaker);          
logCircuit('tareas-DELETE',              tareaDeleteBreaker);       

logCircuit('empleados-GET',              empleadosGetBreaker);
logCircuit('empleados-POST',             empleadosPostBreaker);
logCircuit('empleado-por-id-GET',        empleadoPorIdBreaker);
logCircuit('asignaciones-proyecto-GET',  asignacionesGetBreaker);
logCircuit('asignacion-POST',            asignacionPostBreaker);
logCircuit('asignacion-DELETE',          asignacionDeleteBreaker);
logCircuit('kpis-GET',                   kpisBreaker);
logCircuit('emp-por-depto-GET',          empPorDeptoBreaker);
logCircuit('proj-por-estado-GET',        projPorEstadoBreaker);
logCircuit('proj-por-cat-GET',           projPorCatBreaker);

logCircuit('proyectos-PUT', proyectoPutBreaker);
logCircuit('logs-GET',      logsGetBreaker);
logCircuit('logs-POST',     logsPostBreaker);

// ==================== HEALTH CHECK ====================
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    circuits: {
      proyectosGet:          proyectosGetBreaker.opened          ? 'OPEN' : 'CLOSED',
      proyectosPorEmpleado:  proyectosPorEmpleadoBreaker.opened  ? 'OPEN' : 'CLOSED',
      tareasGet:             tareasGetBreaker.opened             ? 'OPEN' : 'CLOSED',
      tareasPorProyectoGet:  tareasPorProyectoBreaker.opened     ? 'OPEN' : 'CLOSED', 
      empleadosGet:          empleadosGetBreaker.opened          ? 'OPEN' : 'CLOSED',
      asignacionesGet:       asignacionesGetBreaker.opened       ? 'OPEN' : 'CLOSED',
      kpisGet:               kpisBreaker.opened                  ? 'OPEN' : 'CLOSED',
      proyectosPut:          proyectoPutBreaker.opened           ? 'OPEN' : 'CLOSED',
      logsGet:               logsGetBreaker.opened               ? 'OPEN' : 'CLOSED',
      logsPost:              logsPostBreaker.opened              ? 'OPEN' : 'CLOSED',
    }
  });
});

// ==================== ENDPOINTS API ====================

// --- Proyectos ---
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
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error proyectos POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear proyecto' });
  }
});

app.get('/api/bff/proyectos/empleado/:empleadoId', async (req, res) => {
  try {

    const proyectos = await proyectosPorEmpleadoBreaker.fire(req.params.empleadoId);
    
    if (!Array.isArray(proyectos)) {
      return res.json(proyectos);
    }
    const proyectosConTareas = await Promise.all(
      proyectos.map(async (proyecto) => {
        let tareasDelProyecto = [];
        try {
          const resultadoTareas = await tareasPorProyectoBreaker.fire(proyecto.id);
          if (Array.isArray(resultadoTareas)) {
            tareasDelProyecto = resultadoTareas;
          }
        } catch (err) {
          console.warn(`No se pudieron cargar tareas para el proyecto ${proyecto.id}`);
        }
        
        return {
          ...proyecto,
          tareas: tareasDelProyecto
        };
      })
    );

    res.json(proyectosConTareas);
  } catch (error) {
    console.error('BFF Error proyectos por empleado:', error.message);
    res.status(500).json({ error: 'Error al obtener proyectos del empleado' });
  }
});

app.patch('/api/bff/proyectos/:id/estado', async (req, res) => {
  try {
    const data = await proyectoPatchEstadoBreaker.fire({ id: req.params.id, body: req.body });
    if (data && data.error) return res.status(503).json(data);
    res.json(data);
  } catch (error) {
    console.error('BFF Error actualizar estado:', error.message);
    res.status(500).json({ error: 'Error al actualizar estado' });
  }
});

// --- Tareas ---
app.get('/api/bff/tareas', async (req, res) => {
  try {
    const data = await tareasGetBreaker.fire();
    res.json(data);
  } catch (error) {
    console.error('BFF Error tareas GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener tareas' });
  }
});

app.get('/api/bff/tareas/proyecto/:proyectoId', async (req, res) => {
  try {
    const data = await tareasPorProyectoBreaker.fire(req.params.proyectoId);
    res.json(data);
  } catch (error) {
    console.error('BFF Error tareas por proyecto GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener tareas del proyecto' });
  }
});

app.post('/api/bff/tareas', async (req, res) => {
  try {
    const data = await tareasPostBreaker.fire(req.body);
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error tareas POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear tarea' });
  }
});

app.put('/api/bff/tareas/:id', async (req, res) => {
  try {
    const data = await tareaPutBreaker.fire({ id: req.params.id, data: req.body });
    if (data && data.error) return res.status(503).json(data);
    res.json(data);
  } catch (error) {
    console.error('BFF Error tareas PUT:', error.message);
    res.status(500).json({ error: 'Error en BFF al actualizar tarea' });
  }
});

app.delete('/api/bff/tareas/:id', async (req, res) => {
  try {
    const data = await tareaDeleteBreaker.fire(req.params.id);
    if (data && data.error) return res.status(503).json(data);
    res.json({ message: 'Tarea eliminada con éxito' });
  } catch (error) {
    console.error('BFF Error tareas DELETE:', error.message);
    res.status(500).json({ error: 'Error en BFF al eliminar tarea' });
  }
});

// --- Empleados ---
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
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error empleados POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear empleado' });
  }
});

app.get('/api/bff/empleados/:id', async (req, res) => {
  try {
    const data = await empleadoPorIdBreaker.fire(req.params.id);
    if (!data) return res.status(404).json({ error: 'Empleado no encontrado' });
    res.json(data);
  } catch (error) {
    console.error('BFF Error empleado por ID GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener empleado' });
  }
});

// --- Asignaciones ---
app.get('/api/bff/asignaciones/proyecto/:proyectoId', async (req, res) => {
  try {
    const data = await asignacionesGetBreaker.fire(req.params.proyectoId);
    res.json(data);
  } catch (error) {
    console.error('BFF Error asignaciones de proyecto GET:', error.message);
    res.status(500).json({ error: 'Error en BFF al obtener las asignaciones del proyecto' });
  }
});

app.post('/api/bff/asignaciones', async (req, res) => {
  try {
    const data = await asignacionPostBreaker.fire(req.body);
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error asignaciones POST:', error.message);
    res.status(500).json({ error: 'Error en BFF al crear asignación' });
  }
});

app.delete('/api/bff/asignaciones/:id', async (req, res) => {
  try {
    const data = await asignacionDeleteBreaker.fire(req.params.id);
    if (data && data.error) return res.status(503).json(data);
    res.json(data);
  } catch (error) {
    console.error('BFF Error asignaciones DELETE:', error.message);
    res.status(500).json({ error: 'Error en BFF al eliminar asignación' });
  }
});

// --- Reportes y KPIs ---
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

// ==================== API COMPOSITION ====================
app.get('/api/bff/tareas-con-empleado', async (req, res) => {
  try {
    const tareas = await tareasGetBreaker.fire();
    if (!tareas || tareas.error || !Array.isArray(tareas)) return res.json([]);

    const empleadoIds = [...new Set(
      tareas.map(t => t.empleadoId).filter(id => id != null)
    )];

    const empleadosData = await Promise.all(
      empleadoIds.map(id => empleadoPorIdBreaker.fire(id))
    );

    const empleadosMap = {};
    empleadosData.filter(e => e !== null).forEach(e => {
      empleadosMap[e.id] = e;
    });

    const tareasDetalladas = tareas.map(t => ({
      ...t,
      empleado: empleadosMap[t.empleadoId] || null
    }));

    res.json(tareasDetalladas);
  } catch (error) {
    console.error('BFF Composition Error:', error.message);
    res.status(500).json({ error: 'Error al consolidar tareas con empleados' });
  }
});

app.get('/api/bff/tareas-con-empleado/:id', async (req, res) => {
  try {
    const tarea = await tareaPorIdBreaker.fire(req.params.id);
    if (!tarea) return res.status(404).json({ error: 'Tarea no encontrada' });

    let empleado = null;
    if (tarea.empleadoId) {
      empleado = await empleadoPorIdBreaker.fire(tarea.empleadoId);
    }
    res.json({ ...tarea, empleado });
  } catch (error) {
    console.error('BFF Error tarea detalle:', error.message);
    res.status(500).json({ error: 'Error al obtener detalle de tarea' });
  }
});

app.get('/api/bff/proyectos/empleado/:empleadoId', async (req, res) => {
  try {
    const empleadoIdNum = Number(req.params.empleadoId);
    const proyectos = await proyectosPorEmpleadoBreaker.fire(req.params.empleadoId);
    
    if (!Array.isArray(proyectos)) {
      return res.json(proyectos);
    }

    const proyectosConTareas = await Promise.all(
      proyectos.map(async (proyecto) => {
        let tareasDelEmpleado = [];
        try {
          const resultadoTareas = await tareasPorProyectoBreaker.fire(proyecto.id);
          if (Array.isArray(resultadoTareas)) {
            tareasDelEmpleado = resultadoTareas.filter(
              tarea => Number(tarea.empleadoId) === empleadoIdNum
            );
          }
        } catch (err) {
          console.warn(`No se pudieron cargar tareas para el proyecto ${proyecto.id}`);
        }
        
        return {
          ...proyecto,
          tareas: tareasDelEmpleado
        };
      })
    );

    res.json(proyectosConTareas);
  } catch (error) {
    console.error('BFF Error proyectos por empleado:', error.message);
    res.status(500).json({ error: 'Error al obtener proyectos del empleado' });
  }
});
app.put('/api/bff/proyectos/:id', async (req, res) => {
  try {
    const data = await proyectoPutBreaker.fire({ id: req.params.id, data: req.body });
    if (data && data.error) return res.status(503).json(data);
    res.json(data);
  } catch (error) {
    console.error('BFF Error proyectos PUT:', error.message);
    res.status(500).json({ error: 'Error al actualizar proyecto' });
  }
});

app.get('/api/bff/proyectos/:id/logs', async (req, res) => {
  try {
    const data = await logsGetBreaker.fire(req.params.id);
    res.json(data);
  } catch (error) {
    console.error('BFF Error logs GET:', error.message);
    res.status(500).json({ error: 'Error al obtener historial' });
  }
});

app.post('/api/bff/proyectos/:id/logs', async (req, res) => {
  try {
    const data = await logsPostBreaker.fire({ id: req.params.id, data: req.body });
    if (data && data.error) return res.status(503).json(data);
    res.status(201).json(data);
  } catch (error) {
    console.error('BFF Error logs POST:', error.message);
    res.status(500).json({ error: 'Error al guardar log' });
  }
});
// ==================== INICIO DEL SERVIDOR ====================
app.listen(PORT, () => {
  console.log(`BFF escuchando en http://localhost:${PORT}`);
});