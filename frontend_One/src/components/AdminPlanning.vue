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
              <select v-model="nuevoMiembro.empleadoId" class="select-ms" required>
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
                      <button type="button" @click="removerTrabajador(member.id)" class="btn-delete-task">✕</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-else-if="pestañaActiva === 'Tareas'" class="placeholder-tab-content">
            <form @submit.prevent="agregarTarea" class="task-form-grid">
              <input
                type="text"
                v-model="nuevaTarea.nombre"
                placeholder="Nueva tarea..."
                required
                class="input-task-name"
              />
              <select v-model="nuevaTarea.asignacionId" class="select-ms" required :disabled="asignacionesActuales.length === 0">
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
                      <button type="button" @click="eliminarTarea(tarea.id)" class="btn-delete-task">✕</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-else-if="pestañaActiva === 'Costos'" class="placeholder-tab-content">
            <h4>Presupuesto de Infraestructura y Desarrollo</h4>
            <div class="cost-row" style="display: flex; gap: 10px; align-items: center;">
              <label>Presupuesto Estimado (USD):</label>
              <input type="number" v-model.number="proyectoSeleccionado.costo" style="width: 150px; padding: 6px; border: 1px solid #cbd5e1; border-radius: 6px;" />
            </div>
            <p style="font-size: 0.9rem; color: #64748b; margin-top: 10px;">
              Costo calculado para contenedores en microservicios y bases de datos Dockerizadas.
            </p>
          </div>

          <div v-else-if="pestañaActiva === 'Historial'" class="placeholder-tab-content">
            <h4>Bitácora de Cambios Recientes</h4>
            <div class="logs-container">
              <p v-if="!proyectoSeleccionado.logs || proyectoSeleccionado.logs.length === 0" class="empty-msg" style="color: #94a3b8; font-style: italic; padding: 10px 0;">No hay registros de actividad.</p>
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

const emit = defineEmits(['empleado-asignado']);

const pestañaActiva = ref('Equipo');
const proyectoSeleccionado = ref(null);
const proyectosLocales = ref([]);

const asignacionesActuales = ref([]);
const cargandoAsignaciones = ref(false);

const nuevaTarea = ref({ nombre: '', asignacionId: '', fechaInicio: '', fechaLimite: '' });
const nuevoMiembro = ref({ empleadoId: '' });

// 1. Cargar asignaciones desde el backend (Declarada primero)
const cargarAsignaciones = async (proyectoId) => {
  if (!proyectoId) return;
  cargandoAsignaciones.value = true;
  try {
    const respuesta = await api.getAsignacionesPorProyecto(proyectoId);
    const lista = respuesta?.data || [];

    asignacionesActuales.value = lista.map(a => {
      if (!a) return null;
      const emp = props.empleados.find(e => e.id === a.empleadoId);
      return {
        id: a.id,
        rol: a.rol || 'Desarrollador',
        empleado: emp
          ? { id: emp.id, nombre: emp.nombre, email: emp.email }
          : { id: a.empleadoId, nombre: 'Empleado', email: 'N/A' }
      };
    }).filter(item => item !== null);
  } catch (error) {
    console.error('Error al cargar asignaciones del proyecto:', error);
    asignacionesActuales.value = [];
  } finally {
    cargandoAsignaciones.value = false;
  }
};

// 2. Watch de sincronización (Ahora se ejecuta de forma segura abajo de cargarAsignaciones)
watch(() => props.proyectos, (nuevosProyectos) => {
  const idPrevio = proyectoSeleccionado.value?.id;

  proyectosLocales.value = nuevosProyectos.map(p => ({
    ...p,
    costo: p.costo || 0,
    tareas: p.tareas || [],
    logs: p.logs || []
  }));

  if (proyectosLocales.value.length > 0) {
    if (!proyectoSeleccionado.value) {
      proyectoSeleccionado.value = proyectosLocales.value[0];
      cargarAsignaciones(proyectoSeleccionado.value.id);
    } else {
      const mapeado = proyectosLocales.value.find(p => p.id === idPrevio);
      if (mapeado) {
        proyectoSeleccionado.value = mapeado;
      }
    }
  }
}, { immediate: true, deep: true });

// KPIs Reactivos Computados
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

// Métodos de control y formato de fechas
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

// Acciones del equipo (API Backend)
const asignarTrabajador = async () => {
  if (!nuevoMiembro.value.empleadoId || !proyectoSeleccionado.value) return;

  const idNumerico = Number(nuevoMiembro.value.empleadoId);
  const emp = props.empleados.find(e => e.id === idNumerico);
  const nombreEmpleado = emp ? emp.nombre : 'Empleado';
  const rolPorDefecto = emp && emp.cargo && emp.cargo.nombre ? emp.cargo.nombre : 'Colaborador';

  try {
    const respuesta = await api.createAsignacion({
      proyectoId: Number(proyectoSeleccionado.value.id),
      empleadoId: idNumerico,
      rol: rolPorDefecto
    });

    if (respuesta && respuesta.data && respuesta.data.error) {
      alert(`Aviso del Sistema: ${respuesta.data.error}`);
      return;
    }

    nuevoMiembro.value.empleadoId = '';
    await cargarAsignaciones(proyectoSeleccionado.value.id);
    
    if (proyectoSeleccionado.value) {
      if (!proyectoSeleccionado.value.logs) proyectoSeleccionado.value.logs = [];
      registrarLog(proyectoSeleccionado.value, `Asignó a ${nombreEmpleado} al equipo.`);
    }
    
    emit('empleado-asignado');
  } catch (error) {
    console.error('Error al guardar la asignación:', error);
    alert('No se pudo procesar la asignación del trabajador.');
  }
};

const removerTrabajador = async (asignacionId) => {
  if (!confirm('¿Deseas dar de baja a este colaborador de este proyecto?')) return;
  try {
    await api.deleteAsignacion(asignacionId);
    await cargarAsignaciones(proyectoSeleccionado.value.id);
    if (proyectoSeleccionado.value) {
      registrarLog(proyectoSeleccionado.value, `Removió una asignación del equipo`);
    }
    emit('empleado-asignado');
  } catch (error) {
    console.error('Error al remover:', error);
    alert('No se pudo remover la asignación en el backend.');
  }
};

// Gestión de tareas locales
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

const nombreTrabajadorTarea = (tarea) => {
  const enEquipo = asignacionesActuales.value.find(a => a.id === tarea.asignacionId);
  if (enEquipo) return enEquipo.empleado?.nombre || enEquipo.nombre || 'Colaborador';
  return tarea.trabajadorNombre || 'Sin asignar';
};

const eliminarTarea = (idTarea) => {
  proyectoSeleccionado.value.tareas = proyectoSeleccionado.value.tareas.filter(t => t.id !== idTarea);
};
</script>

<style src="../assets/planning.css" scoped></style>