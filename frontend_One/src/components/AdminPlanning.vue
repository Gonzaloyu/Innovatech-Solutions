<template>
  <div class="planning-workspace">
    <div class="kpi-grid">
      <div class="kpi-box">
        <span class="kpi-val">{{ proyectosLocales.length }}</span>
        <span class="kpi-lbl">Proyectos activos</span>
      </div>
      <div class="kpi-box text-danger">
        <span class="kpi-val">{{ totalAtrasados }}</span>
        <span class="kpi-lbl">Con atraso</span>
      </div>
      <div class="kpi-box text-success">
        <span class="kpi-val">{{ totalTareasCompletadas }}</span>
        <span class="kpi-lbl">Tareas completadas</span>
      </div>
      <div class="kpi-box">
        <span class="kpi-val">${{ totalCostos.toLocaleString() }}</span>
        <span class="kpi-lbl">Costo total USD</span>
      </div>
    </div>

    <div class="main-layout">

      <div class="projects-sidebar">
        <div class="sidebar-header">
          <h3>Proyectos</h3>
        </div>

        <div class="projects-list">
          <div v-if="proyectosLocales.length === 0" class="empty-state" style="padding: 1rem;">
            No hay proyectos disponibles.
          </div>

          <div
            v-for="proj in proyectosLocales"
            :key="proj.id"
            class="project-card"
            :class="{ 'is-active': proyectoSeleccionado?.id === proj.id }"
            @click="seleccionarProyecto(proj)"
          >
            <div class="card-top">
              <h4>{{ proj.nombre }}</h4>
              <span class="status-pill" :class="(proj.estado || 'en-planificación').toLowerCase().replace(/ /g, '-')">
                {{ proj.estado || 'En Planificación' }}
              </span>
            </div>
            <p class="responsable-text">{{ proj.responsable || 'Sin asignar' }}</p>
            <p class="dates-text" :class="{ 'date-alert': esAtrasado(proj.fechaFin) }">
              {{ formatearFecha(proj.fechaInicio) }} &rarr; {{ formatearFecha(proj.fechaFin) }}
            </p>
          </div>
        </div>
      </div>

      <div class="planning-detail" v-if="proyectoSeleccionado">
        <div class="detail-header">
          <h2>{{ proyectoSeleccionado.nombre }}</h2>
          <span class="status-pill big" :class="(proyectoSeleccionado.estado || 'en-planificación').toLowerCase().replace(/ /g, '-')">
            {{ proyectoSeleccionado.estado || 'En Planificación' }}
          </span>
        </div>

        <div v-if="esAtrasado(proyectoSeleccionado.fechaFin)" class="alert-banner">
          Proyecto atrasado — venció el {{ formatearFecha(proyectoSeleccionado.fechaFin) }}
        </div>

        <div class="tabs-bar">
          <button
            v-for="tab in ['Equipo', 'Tareas', 'Costos', 'Historial']"
            :key="tab"
            class="tab-btn"
            :class="{ 'active-tab': pestañaActiva === tab }"
            @click="pestañaActiva = tab"
          >
            {{ tab }}
          </button>
        </div>

        <div class="tab-content">

          <div v-if="pestañaActiva === 'Equipo'">
            <form @submit.prevent="asignarTrabajador" class="task-inline-form">
              <select v-model.number="nuevoMiembro.empleadoId" class="select-ms" required>
                <option value="" disabled selected>Seleccionar Empleado...</option>
                <option v-for="emp in empleados" :key="emp.id" :value="emp.id">
                  {{ emp.nombre }} ({{ emp.cargo?.nombre || 'Sin Cargo' }})
                </option>
              </select>

              <button type="submit" class="btn-submit-task" :disabled="cargandoAsignaciones">Asignar</button>
            </form>

            <div v-if="cargandoAsignaciones" class="empty-state">
              Cargando equipo...
            </div>
            <div v-else-if="asignacionesActuales.length === 0" class="empty-state">
              Sin personal asignado aún. Agrega el primero usando el formulario superior.
            </div>

            <div class="tasks-table-wrapper" v-else>
              <table class="planning-table">
                <thead>
                  <tr>
                    <th>Colaborador</th>
                    <th>Email</th>
                    <th>Rol asignado</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="member in asignacionesActuales" :key="member.id">
                    <td>
                      <strong>{{ member.empleado?.nombre || member.nombre || 'Colaborador' }}</strong>
                    </td>
                    <td>{{ member.empleado?.email || 'N/A' }}</td>
                    <td><span class="ms-badge">{{ member.rol || 'Desarrollador' }}</span></td>
                    <td>
                      <button @click="removerTrabajador(member.id)" class="btn-delete-task">✕</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="placeholder-tab-content" v-else-if="pestañaActiva === 'Tareas'">
            <form @submit.prevent="agregarTarea" class="task-form-grid">
              <input
                type="text"
                v-model="nuevaTarea.nombre"
                placeholder="Nueva tarea..."
                required
                class="input-task-name"
              />
              <select v-model.number="nuevaTarea.asignacionId" class="select-ms" required :disabled="asignacionesActuales.length === 0">
                <option value="" disabled selected>
                  {{ asignacionesActuales.length === 0 ? 'Sin equipo asignado aún' : 'Trabajador asignado...' }}
                </option>
                <option v-for="member in asignacionesActuales" :key="member.id" :value="member.id">
                  {{ member.empleado?.nombre || member.nombre || 'Colaborador' }}
                </option>
              </select>
              <label class="date-field">
                <span class="date-field-lbl">Inicio</span>
                <input
                  type="date"
                  v-model="nuevaTarea.fechaInicio"
                  required
                  class="input-task-date"
                />
              </label>
              <label class="date-field">
                <span class="date-field-lbl">Fin</span>
                <input
                  type="date"
                  v-model="nuevaTarea.fechaLimite"
                  required
                  class="input-task-date"
                  :min="nuevaTarea.fechaInicio || undefined"
                />
              </label>
              <button type="submit" class="btn-submit-task">Agregar</button>
            </form>
            <p v-if="asignacionesActuales.length === 0" class="form-hint">
              Asigna al menos un trabajador en la pestaña "Equipo" antes de crear tareas.
            </p>

            <div v-if="!proyectoSeleccionado.tareas || proyectoSeleccionado.tareas.length === 0" class="empty-state">
              Sin tareas aún. Agrega la primera.
            </div>
            <div class="tasks-table-wrapper" v-else>
              <table class="planning-table">
                <thead>
                  <tr>
                    <th>Estado</th>
                    <th>Tarea / Componente</th>
                    <th>Trabajador</th>
                    <th>Inicio</th>
                    <th>Fin</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="tarea in proyectoSeleccionado.tareas" :key="tarea.id">
                    <td>
                      <input type="checkbox" v-model="tarea.completada" @change="registrarLog(proyectoSeleccionado, `Cambió estado de tarea: ${tarea.nombre}`)" />
                    </td>
                    <td>
                      <span :class="{ 'line-through': tarea.completada }"><strong>{{ tarea.nombre }}</strong></span>
                    </td>
                    <td>{{ nombreTrabajadorTarea(tarea) }}</td>
                    <td>{{ formatearFecha(tarea.fechaInicio) }}</td>
                    <td :class="{ 'date-alert': esAtrasado(tarea.fechaLimite) && !tarea.completada }">{{ formatearFecha(tarea.fechaLimite) }}</td>
                    <td>
                      <button @click="eliminarTarea(tarea.id)" class="btn-delete-task">✕</button>
                    </td>
                  </tr>
                </tbody>
              </table>

            </div>
          </div>

          <div v-if="pestañaActiva === 'Costos'" class="placeholder-tab-content">
            <h4>Presupuesto de Infraestructura y Desarrollo</h4>
            <div class="cost-row">
              <label>Presupuesto Estimado (USD):</label>
              <input type="number" v-model.number="proyectoSeleccionado.costo" style="width: 150px; padding: 6px;" />
            </div>
            <p style="font-size: 0.9rem; color: #64748b; margin-top: 10px;">
              Costo calculado para contenedores en microservicios y bases de datos Dockerizadas.
            </p>
          </div>

          <div v-else-if="pestañaActiva === 'Historial'" class="placeholder-tab-content">
            <h4>Bitácora de Cambios Recientes</h4>
            <div class="logs-container">
              <p v-if="!proyectoSeleccionado.logs || proyectoSeleccionado.logs.length === 0" class="empty-msg">No hay registros de actividad.</p>
              <div v-for="(log, idx) in proyectoSeleccionado.logs" :key="idx" class="log-entry">
                <span class="log-time">[{{ log.hora }}]</span> {{ log.mensaje }}
              </div>
            </div>
          </div>

        </div>
      </div>

      <div class="planning-detail empty-state" v-else>
        Selecciona un proyecto de la lista izquierda para ver e iniciar su planificación.
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import api from '../services/api';

const props = defineProps({
  proyectos: {
    type: Array,
    required: true,
    default: () => []
  },
  empleados: {
    type: Array,
    required: true,
    default: () => []
  }
});

const emit = defineEmits(['empleado-assigned', 'empleado-asignado']);

const pestañaActiva = ref('Equipo');
const proyectoSeleccionado = ref(null);
const proyectosLocales = ref([]);

// Las asignaciones del proyecto SELECCIONADO viven aparte de proyectosLocales.
// Ya no dependen de lo que venga (o no venga) en la prop "proyectos".
const asignacionesActuales = ref([]);
const cargandoAsignaciones = ref(false);

const nuevaTarea = ref({ nombre: '', asignacionId: '', fechaInicio: '', fechaLimite: '' });
const nuevoMiembro = ref({ empleadoId: '' });

// --- LOGICA DE ASIGNACIONES (EQUIPO) ---
// Se define ANTES del watch de abajo porque ese watch usa { immediate: true }
// y se ejecuta apenas se monta el componente: si cargarAsignaciones estuviera
// declarada más abajo, todavía no existiría como variable inicializada
// (con const/función flecha en <script setup> no hay hoisting de la asignación).

// Trae las asignaciones reales del backend para el proyecto dado,
// y las cruza con la lista de empleados (ya cargada en el padre) para
// mostrar nombre/email/cargo, ya que Asignacion.java solo guarda IDs.
const cargarAsignaciones = async (proyectoId) => {
  if (!proyectoId) return;
  cargandoAsignaciones.value = true;
  try {
    const respuesta = await api.getAsignacionesPorProyecto(proyectoId);
    const lista = respuesta.data || [];

    asignacionesActuales.value = lista.map(a => {
      const emp = props.empleados.find(e => e.id === a.empleadoId);
      return {
        id: a.id,
        rol: a.rol,
        empleado: emp
          ? { id: emp.id, nombre: emp.nombre, email: emp.email }
          : { id: a.empleadoId, nombre: 'Empleado', email: 'N/A' }
      };
    });
  } catch (error) {
    console.error('Error al cargar asignaciones del proyecto:', error);
    asignacionesActuales.value = [];
  } finally {
    cargandoAsignaciones.value = false;
  }
};

// --- Sincroniza la lista de proyectos (sin tocar las asignaciones) ---
watch(() => props.proyectos, (nuevosProyectos) => {
  const idPrevio = proyectoSeleccionado.value?.id;

  proyectosLocales.value = nuevosProyectos.map(p => ({
    ...p,
    costo: p.costo || 0,
    tareas: p.tareas || [],
    logs: p.logs || []
    // Nota: ya no leemos p.asignaciones / p.empleados aquí.
    // El equipo se carga aparte vía cargarAsignaciones().
  }));

  if (proyectosLocales.value.length > 0) {
    if (!proyectoSeleccionado.value) {
      proyectoSeleccionado.value = proyectosLocales.value[0];
      cargarAsignaciones(proyectoSeleccionado.value.id);
    } else {
      const mapeado = proyectosLocales.value.find(p => p.id === idPrevio);
      if (mapeado) {
        // Reemplaza la referencia del objeto seleccionado por la versión actualizada,
        // pero esto YA NO dispara una recarga de asignaciones (mismo id = mismo equipo).
        proyectoSeleccionado.value = mapeado;
      }
    }
  }
}, { immediate: true, deep: true });

// --- COMPUTED PROPERTIES ---
const totalAtrasados = computed(() => {
  return proyectosLocales.value.filter(p => esAtrasado(p.fechaFin)).length;
});

const totalTareasCompletadas = computed(() => {
  return proyectosLocales.value.reduce((acc, p) => {
    return acc + (p.tareas ? p.tareas.filter(t => t.completada).length : 0);
  }, 0);
});

const totalCostos = computed(() => {
  return proyectosLocales.value.reduce((acc, p) => acc + (p.costo || 0), 0);
});

// --- MÉTODOS ---
const seleccionarProyecto = (proyecto) => {
  if (proyectoSeleccionado.value?.id === proyecto.id) return;
  proyectoSeleccionado.value = proyecto;
  cargarAsignaciones(proyecto.id);
};

const esAtrasado = (fechaFinStr) => {
  if (!fechaFinStr) return false;
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  return new Date(fechaFinStr) < hoy;
};

const formatearFecha = (fechaStr) => {
  if (!fechaStr) return '';
  const partes = fechaStr.split('-');
  if (partes.length !== 3) return fechaStr;
  const [year, month, day] = partes;
  return `${day}-${month}-${year}`;
};

const registrarLog = (proyecto, mensaje) => {
  const ahora = new Date();
  const horaStr = `${String(ahora.getHours()).padStart(2, '0')}:${String(ahora.getMinutes()).padStart(2, '0')}`;
  if (!proyecto.logs) proyecto.logs = [];
  proyecto.logs.unshift({ hora: horaStr, mensaje });
};

// --- ASIGNAR / REMOVER TRABAJADOR (usan cargarAsignaciones, definida arriba) ---

const asignarTrabajador = async () => {
  if (!nuevoMiembro.value.empleadoId || !proyectoSeleccionado.value) return;

  const idNumerico = Number(nuevoMiembro.value.empleadoId);
  const emp = props.empleados.find(e => e.id === idNumerico);
  const nombreEmpleado = emp ? emp.nombre : 'Empleado';
  const rolPorDefecto = emp?.cargo?.nombre || 'Colaborador';

  try {
    await api.createAsignacion({
      proyectoId: Number(proyectoSeleccionado.value.id),
      empleadoId: idNumerico,
      rol: rolPorDefecto
    });

    // En vez de "parchear" el array local a mano, recargamos desde el backend.
    // Esto es la fuente de la verdad y ya no depende de la prop "proyectos".
    await cargarAsignaciones(proyectoSeleccionado.value.id);

    registrarLog(proyectoSeleccionado.value, `Asignó a ${nombreEmpleado} al equipo.`);
    nuevoMiembro.value = { empleadoId: '' };

    // Avisamos al padre para que refresque KPIs / lista de proyectos.
    // Ya no nos afecta aunque el padre recargue "proyectos", porque
    // asignacionesActuales vive fuera de ese flujo.
    emit('empleado-asignado');
  } catch (error) {
    console.error('Error al guardar la asignación:', error);
    alert('No se pudo guardar la asignación del trabajador en el backend.');
  }
};

const removerTrabajador = async (asignacionId) => {
  if (!confirm('¿Deseas dar de baja a este colaborador de este proyecto?')) return;
  try {
    await api.deleteAsignacion(asignacionId);
    await cargarAsignaciones(proyectoSeleccionado.value.id);
    registrarLog(proyectoSeleccionado.value, `Removió una asignación del equipo`);
    emit('empleado-asignado');
  } catch (error) {
    console.error('Error al remover:', error);
    alert('No se pudo remover la asignación en el backend.');
  }
};

// --- LOGICA DE TAREAS ---
const agregarTarea = () => {
  if (nuevaTarea.value.fechaInicio && nuevaTarea.value.fechaLimite &&
      nuevaTarea.value.fechaLimite < nuevaTarea.value.fechaInicio) {
    alert('La fecha de fin no puede ser anterior a la fecha de inicio.');
    return;
  }

  if (!proyectoSeleccionado.value.tareas) proyectoSeleccionado.value.tareas = [];

  const trabajador = asignacionesActuales.value.find(a => a.id === nuevaTarea.value.asignacionId);

  proyectoSeleccionado.value.tareas.push({
    id: Date.now(),
    nombre: nuevaTarea.value.nombre,
    asignacionId: nuevaTarea.value.asignacionId,
    trabajadorNombre: trabajador?.empleado?.nombre || trabajador?.nombre || 'Colaborador',
    fechaInicio: nuevaTarea.value.fechaInicio,
    fechaLimite: nuevaTarea.value.fechaLimite,
    completada: false
  });

  registrarLog(
    proyectoSeleccionado.value,
    `Agregó tarea "${nuevaTarea.value.nombre}" (asignada a ${trabajador?.empleado?.nombre || trabajador?.nombre || 'colaborador'})`
  );
  nuevaTarea.value = { nombre: '', asignacionId: '', fechaInicio: '', fechaLimite: '' };
};

// Muestra el nombre del trabajador de la tarea. Si esa persona ya fue
// removida del Equipo, cae al nombre guardado al momento de crear la tarea.
const nombreTrabajadorTarea = (tarea) => {
  const enEquipo = asignacionesActuales.value.find(a => a.id === tarea.asignacionId);
  if (enEquipo) return enEquipo.empleado?.nombre || enEquipo.nombre || 'Colaborador';
  return tarea.trabajadorNombre || 'Sin asignar';
};

const eliminarTarea = (idTarea) => {
  proyectoSeleccionado.value.tareas = proyectoSeleccionado.value.tareas.filter(t => t.id !== idTarea);
};
</script>

<style scoped>
/* Los estilos se mantienen exactamente iguales */
.planning-workspace {
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  padding: 1.5rem;
  background-color: #f8fafc;
  min-height: 80vh;
}
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.25rem;
  margin-bottom: 2rem;
}
.kpi-box {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.kpi-val {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e293b;
}
.kpi-lbl {
  font-size: 0.85rem;
  color: #64748b;
  margin-top: 0.25rem;
}
.text-danger .kpi-val { color: #ef4444; }
.text-success .kpi-val { color: #10b981; }
.main-layout {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
}
.projects-sidebar {
  width: 320px;
  flex-shrink: 0;
}
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.sidebar-header h3 {
  font-size: 1.15rem;
  color: #334155;
  margin: 0;
}
.projects-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.project-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}
.project-card:hover {
  border-color: #cbd5e1;
  transform: translateY(-1px);
}
.project-card.is-active {
  border: 2px solid #0f172a;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}
.card-top h4 {
  margin: 0;
  font-size: 1rem;
  color: #1e293b;
}
.responsable-text {
  margin: 6px 0;
  font-size: 0.85rem;
  color: #64748b;
}
.dates-text {
  margin: 0;
  font-size: 0.8rem;
  color: #475569;
}
.date-alert {
  color: #b91c1c;
  font-weight: 500;
}
.status-pill {
  font-size: 0.75rem;
  padding: 3px 8px;
  border-radius: 20px;
  font-weight: 500;
}
.status-pill.en-ejecución {
  background-color: #e0f2fe;
  color: #0369a1;
}
.status-pill.en-planificación {
  background-color: #f1f5f9;
  color: #475569;
}
.status-pill.big {
  font-size: 0.85rem;
  padding: 4px 12px;
}
.planning-detail {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
  min-height: 450px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.detail-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #0f172a;
}
.alert-banner {
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  color: #991b1b;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 0.85rem;
  margin-bottom: 1.25rem;
  font-weight: 500;
}
.tabs-bar {
  display: flex;
  gap: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 1.25rem;
}
.tab-btn {
  background: none;
  border: none;
  padding: 8px 4px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  position: relative;
}
.tab-btn:hover {
  color: #1e293b;
}
.tab-btn.active-tab {
  color: #000;
  font-weight: 600;
  border-bottom: 2px solid #000;
}
.task-inline-form {
  display: flex;
  gap: 10px;
  margin-bottom: 1.25rem;
}
/* Igual que .task-inline-form pero permite que los campos bajen de línea
   cuando no caben todos (ahora son más: nombre, MS, trabajador, 2 fechas, botón) */
.task-form-grid {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 10px;
  margin-bottom: 0.5rem;
}
.task-form-grid .input-task-name {
  flex: 2;
  min-width: 160px;
}
.task-form-grid .select-ms {
  flex: 1;
  min-width: 150px;
}
.date-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 130px;
}
.date-field-lbl {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 500;
}
.form-hint {
  font-size: 0.8rem;
  color: #b45309;
  background-color: #fffbeb;
  border: 1px solid #fde68a;
  padding: 6px 10px;
  border-radius: 6px;
  margin: 0 0 1.25rem 0;
}
.input-task-name {
  flex: 2;
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 0.9rem;
}
.select-ms {
  flex: 1;
  padding: 8px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 0.9rem;
  background-color: #fff;
}
.select-ms:disabled {
  background-color: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}
.input-task-date {
  flex: 1;
  width: 100%;
  padding: 8px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 0.9rem;
  box-sizing: border-box;
}
.btn-submit-task {
  background-color: #000;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
}
.btn-submit-task:hover {
  background-color: #1e293b;
}
.tasks-table-wrapper {
  overflow-x: auto;
}
.planning-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.9rem;
}
.planning-table th {
  background-color: #f8fafc;
  color: #475569;
  padding: 10px;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}
.planning-table td {
  padding: 12px 10px;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
}
.line-through {
  text-decoration: line-through;
  color: #94a3b8;
}
.ms-badge {
  background-color: #f0fdf4;
  color: #16a34a;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
  border: 1px solid #bbf7d0;
}
.btn-delete-task {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  font-size: 1rem;
}
.btn-delete-task:hover {
  color: #ef4444;
}
.placeholder-tab-content {
  padding: 5px 0;
}
.placeholder-tab-content h4 {
  margin-top: 0;
  color: #1e293b;
}
.logs-container {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 10px;
  max-height: 200px;
  overflow-y: auto;
}
.log-entry {
  font-size: 0.85rem;
  margin-bottom: 6px;
  color: #475569;
}
.log-time {
  color: #94a3b8;
  font-weight: bold;
}
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  font-style: italic;
  padding: 2rem;
  text-align: center;
}
</style>