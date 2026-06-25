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
              <span class="status-pill" :class="(proj.estado?.nombre || proj.estado || 'en-planificación').toLowerCase().replace(/ /g, '-')">
                {{ proj.estado?.nombre || proj.estado || 'En Planificación' }}
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
          <span class="status-pill big" :class="(proyectoSeleccionado.estado?.nombre || proyectoSeleccionado.estado || 'en-planificación').toLowerCase().replace(/ /g, '-')">
            {{ proyectoSeleccionado.estado?.nombre || proyectoSeleccionado.estado || 'En Planificación' }}
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

          <div v-else-if="pestañaActiva === 'Tareas'">
            <form @submit.prevent="agregarTarea" class="task-form-grid">
              <input
                type="text"
                v-model="nuevaTarea.nombre"
                placeholder="Nueva tarea..."
                required
                class="input-task-name"
              />
              <select v-model="nuevaTarea.empleadoId" class="select-ms" required :disabled="asignacionesActuales.length === 0">
                <option value="" disabled selected>
                  {{ asignacionesActuales.length === 0 ? 'Sin equipo asignado aún' : 'Trabajador asignado...' }}
                </option>
                <option v-for="member in asignacionesActuales" :key="member.id" :value="member.empleado?.id || member.empleadoId">
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
              <button type="submit" class="btn-submit-task" :disabled="cargandoTareas">
                {{ cargandoTareas ? '...' : 'Agregar' }}
              </button>
            </form>

            <p v-if="asignacionesActuales.length === 0" class="form-hint">
              Asigna al menos un trabajador en la pestaña "Equipo" antes de crear tareas.
            </p>

            <div v-if="cargandoTareas" class="empty-state">
              Cargando tareas del servidor...
            </div>
            <div v-else-if="!proyectoSeleccionado.tareas || proyectoSeleccionado.tareas.length === 0" class="empty-state">
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
                      <select
                        v-model="tarea.estado"
                        class="status-select-badge"
                        :class="obtenerClaseEstado(tarea.estado)"
                        @change="actualizarEstadoTarea(tarea)"
                      >
                        <option value="Pendiente">Pendiente</option>
                        <option value="En Ejecución">En Ejecución</option>
                        <option value="Finalizado">Finalizado</option>
                      </select>
                    </td>
                    <td>
                      <span :class="{ 'line-through': tarea.estado === 'Finalizado' }"><strong>{{ tarea.nombre }}</strong></span>
                    </td>
                    <td>{{ nombreTrabajadorTarea(tarea) }}</td>
                    <td>{{ formatearFecha(tarea.fechaInicio) }}</td>
                    <td :class="{ 'date-alert': (esAtrasado(tarea.fechaLimite) && tarea.estado !== 'Finalizado') }">
                      {{ formatearFecha(tarea.fechaLimite) }}
                    </td>
                    <td>
                      <button type="button" @click="eliminarTarea(tarea.id)" class="btn-delete-task">✕</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-else-if="pestañaActiva === 'Costos'">
            <h4>Presupuesto de Infraestructura y Desarrollo</h4>
            <div class="cost-row" style="display: flex; gap: 10px; align-items: center; margin-bottom: 20px;">
              <label>Presupuesto Estimado (USD):</label>
              <input type="number" v-model.number="proyectoSeleccionado.costo" style="width: 150px; padding: 6px; border: 1px solid #cbd5e1; border-radius: 6px;" />
            </div>

            <hr style="border: 0; border-top: 1px solid #e2e8f0; margin: 15px 0;">

            <h4>Tiempo de Ejecución</h4>
            <div class="tiempo-grid">
              <div class="tiempo-item">
                <span class="tiempo-lbl">Fecha Inicio</span>
                <span class="tiempo-val">{{ formatearFecha(proyectoSeleccionado.fechaInicio) }}</span>
              </div>
              <div class="tiempo-item">
                <span class="tiempo-lbl">{{ esFinalizado(proyectoSeleccionado) ? 'Fecha Fin Real' : 'Estado' }}</span>
                <span class="tiempo-val">
                  {{ esFinalizado(proyectoSeleccionado) ? formatearFecha(proyectoSeleccionado.fechaFin) : 'En curso (calculando hasta hoy)' }}
                </span>
              </div>
              <div class="tiempo-item">
                <span class="tiempo-lbl">Días {{ esFinalizado(proyectoSeleccionado) ? 'trabajados' : 'transcurridos' }}</span>
                <span class="tiempo-val">{{ diasTranscurridosProyecto }} días</span>
              </div>
              <div class="tiempo-item">
                <span class="tiempo-lbl">Horas estimadas</span>
                <span class="tiempo-val">{{ horasEstimadasProyecto }} hrs</span>
              </div>
            </div>

            <p class="formula-nota">
              Fórmula: días = ({{ esFinalizado(proyectoSeleccionado) ? 'fecha fin real' : 'fecha actual' }} − fecha inicio) · horas estimadas = días × 8 hrs/día · costo mano de obra estimado = horas × valor/hora promedio del equipo asignado.
            </p>

            <div class="tiempo-grid" style="margin-top: 10px;">
              <div class="tiempo-item">
                <span class="tiempo-lbl">Valor/hora promedio del equipo</span>
                <span class="tiempo-val">${{ valorHoraPromedioEquipo.toFixed(2) }}</span>
              </div>
              <div class="tiempo-item">
                <span class="tiempo-lbl">Costo Mano de Obra Estimado</span>
                <span class="tiempo-val">${{ costoManoObraEstimado.toLocaleString() }}</span>
              </div>
            </div>

            <hr style="border: 0; border-top: 1px solid #e2e8f0; margin: 15px 0;">

            <h4>Desglose de Costos Reales</h4>
            <div v-if="cargandoGastos" class="empty-state">
              Cargando gastos reportados...
            </div>
            <div v-else-if="gastosProyecto.length === 0" class="empty-state">
              No hay costos de recursos ni mano de obra reportados en este proyecto todavía.
            </div>

            <div class="tasks-table-wrapper" v-else>
              <table class="planning-table">
                <thead>
                  <tr>
                    <th>Tipo de Gasto</th>
                    <th>Descripción</th>
                    <th>Fecha de Registro</th>
                    <th>Monto</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="gasto in gastosProyecto" :key="gasto.id">
                    <td>
                      <span class="ms-badge">{{ gasto.tipo || 'Recurso' }}</span>
                    </td>
                    <td>{{ gasto.descripcion }}</td>
                    <td>{{ formatearFecha(gasto.fechaRegistro) || 'N/A' }}</td>
                    <td><strong>${{ gasto.monto }}</strong></td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr style="background-color: #f1f5f9;">
                    <td colspan="3" style="text-align: right; padding: 10px; font-size: 1.1rem;">
                      <strong>Costo Real Total Generado:</strong>
                    </td>
                    <td style="padding: 10px; font-size: 1.1rem; color: #0f172a;">
                      <strong>${{ costoTotalGastos }}</strong>
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <div class="resumen-costos-grid">
              <div class="resumen-item">
                <span class="resumen-lbl">Total Herramientas</span>
                <span class="resumen-val">${{ totalHerramientas.toLocaleString() }}</span>
              </div>
              <div class="resumen-item">
                <span class="resumen-lbl">Total Mano de Obra Registrado</span>
                <span class="resumen-val">${{ totalManoObra.toLocaleString() }}</span>
              </div>
              <div class="resumen-item total-final">
                <span class="resumen-lbl">Costo Real Total</span>
                <span class="resumen-val">${{ costoTotalGastos.toLocaleString() }}</span>
              </div>
              <div class="resumen-item" :class="diferenciaPresupuesto >= 0 ? 'text-success' : 'text-danger'">
                <span class="resumen-lbl">{{ diferenciaPresupuesto >= 0 ? 'Ahorro vs presupuesto' : 'Sobrecosto vs presupuesto' }}</span>
                <span class="resumen-val">${{ Math.abs(diferenciaPresupuesto).toLocaleString() }}</span>
              </div>
            </div>
          </div>

          <div v-else-if="pestañaActiva === 'Historial'">
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
import axios from 'axios';

const props = defineProps({
  proyectos: { type: Array, required: true, default: () => [] },
  empleados: { type: Array, required: true, default: () => [] }
});

const emit = defineEmits(['empleado-asignado']);

const pestañaActiva = ref('Equipo');
const proyectoSeleccionado = ref(null);
const proyectosLocales = ref([]);
const asignacionesActuales = ref([]);
const cargandoAsignaciones = ref(false);
const cargandoTareas = ref(false);

const gastosProyecto = ref([]);
const cargandoGastos = ref(false);

const nuevaTarea = ref({ nombre: '', empleadoId: '', fechaInicio: '', fechaLimite: '' });
const nuevoMiembro = ref({ empleadoId: '' });

// ==========================================
// LLAMADAS A LA API
// ==========================================
const cargarAsignaciones = async (proyectoId) => {
  if (!proyectoId) return;
  cargandoAsignaciones.value = true;
  try {
    const respuesta = await api.getAsignacionesPorProyecto(proyectoId);
    const lista = respuesta?.data || [];
    const listaEmpleados = props.empleados || [];

    asignacionesActuales.value = lista.map(a => {
      if (!a) return null;
      const emp = listaEmpleados.find(e => e.id === a.empleadoId);
      return {
        id: a.id,
        empleadoId: a.empleadoId,
        rol: a.rol || 'Desarrollador',
        empleado: emp ? { id: emp.id, nombre: emp.nombre, email: emp.email, valorHora: emp.valorHora } : null
      };
    }).filter(item => item !== null && item.empleado !== null);
  } catch (error) {
    console.error('Error al cargar asignaciones:', error);
    asignacionesActuales.value = [];
  } finally {
    cargandoAsignaciones.value = false;
  }
};

const cargarTareasDelProyecto = async (proyectoId) => {
  if (!proyectoId) return;
  cargandoTareas.value = true;
  try {
    const respuesta = await axios.get(`http://localhost:3000/api/bff/tareas/proyecto/${proyectoId}`);
    if (proyectoSeleccionado.value && proyectoSeleccionado.value.id === proyectoId) {
      proyectoSeleccionado.value.tareas = respuesta.data || [];
    }
  } catch (error) {
    console.error('Error al cargar tareas:', error);
  } finally {
    cargandoTareas.value = false;
  }
};

const cargarGastosDelProyecto = async (proyectoId) => {
  if (!proyectoId) return;
  cargandoGastos.value = true;
  try {
    const response = await fetch(`http://localhost:8081/api/gastos/proyecto/${proyectoId}`);
    if (response.ok) {
      gastosProyecto.value = await response.json();
    } else {
      gastosProyecto.value = [];
    }
  } catch (error) {
    console.error("Error al cargar gastos desde el backend:", error);
    gastosProyecto.value = [];
  } finally {
    cargandoGastos.value = false;
  }
};

// ==========================================
// WATCHERS
// ==========================================
watch(() => props.proyectos, (nuevosProyectos) => {
  if (!nuevosProyectos || !Array.isArray(nuevosProyectos)) return;
  const idPrevio = proyectoSeleccionado.value?.id;

  proyectosLocales.value = nuevosProyectos.map(p => ({
    ...p,
    costo: p.costo || 0,
    tareas: p.tareas || [],
    logs: p.logs || []
  }));

  if (proyectosLocales.value.length > 0) {
    const mapeado = idPrevio ? proyectosLocales.value.find(p => p.id === idPrevio) : null;
    if (!proyectoSeleccionado.value || !mapeado) {
      proyectoSeleccionado.value = proyectosLocales.value[0];
      cargarAsignaciones(proyectoSeleccionado.value.id);
      cargarTareasDelProyecto(proyectoSeleccionado.value.id);
      cargarGastosDelProyecto(proyectoSeleccionado.value.id);
    } else {
      const tareasActuales = proyectoSeleccionado.value.tareas || [];
      proyectoSeleccionado.value = mapeado;
      proyectoSeleccionado.value.tareas = tareasActuales;
    }
  }
}, { immediate: true, deep: true });

// ==========================================
// COMPUTED / KPIS GENERALES
// ==========================================
const totalAtrasados = computed(() => proyectosLocales.value.filter(p => esAtrasado(p.fechaFin)).length);
const totalTareasCompletadas = computed(() => {
  return proyectosLocales.value.reduce((acc, p) => acc + (p.tareas ? p.tareas.filter(t => t.estado === 'Finalizado').length : 0), 0);
});
const totalCostos = computed(() => proyectosLocales.value.reduce((acc, p) => acc + (p.costo || 0), 0));

const costoTotalGastos = computed(() => {
  return gastosProyecto.value.reduce((acc, gasto) => acc + (gasto.monto || 0), 0);
});

const esFinalizado = (proy) => {
  const nombre = (proy?.estado?.nombre || proy?.estado || '').toString().toLowerCase();
  return nombre === 'finalizado';
};

const diasTranscurridosProyecto = computed(() => {
  if (!proyectoSeleccionado.value?.fechaInicio) return 0;
  const inicio = new Date(proyectoSeleccionado.value.fechaInicio);
  const fin = (esFinalizado(proyectoSeleccionado.value) && proyectoSeleccionado.value.fechaFin)
    ? new Date(proyectoSeleccionado.value.fechaFin)
    : new Date();
  const diffMs = fin - inicio;
  return Math.max(1, Math.round(diffMs / (1000 * 60 * 60 * 24)));
});

const horasEstimadasProyecto = computed(() => diasTranscurridosProyecto.value * 8);

const valorHoraPromedioEquipo = computed(() => {
  const valores = asignacionesActuales.value
    .map(a => a.empleado?.valorHora || 0)
    .filter(v => v > 0);
  if (!valores.length) return 0;
  return valores.reduce((acc, v) => acc + v, 0) / valores.length;
});

const costoManoObraEstimado = computed(() => Math.round(horasEstimadasProyecto.value * valorHoraPromedioEquipo.value));

const gastosHerramientas = computed(() => gastosProyecto.value.filter(g => g.tipo === 'Herramienta'));
const gastosManoObra = computed(() => gastosProyecto.value.filter(g => g.tipo === 'Mano de Obra'));
const totalHerramientas = computed(() => gastosHerramientas.value.reduce((acc, g) => acc + (g.monto || 0), 0));
const totalManoObra = computed(() => gastosManoObra.value.reduce((acc, g) => acc + (g.monto || 0), 0));

const diferenciaPresupuesto = computed(() => {
  const presupuesto = proyectoSeleccionado.value?.costo || 0;
  return presupuesto - costoTotalGastos.value;
});

// ==========================================
// METODOS AUXILIARES
// ==========================================
const esAtrasado = (fechaFinStr) => {
  if (!fechaFinStr) return false;
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  return new Date(fechaFinStr) < hoy;
};

const formatearFecha = (fechaStr) => {
  if (!fechaStr) return '';
  const partes = fechaStr.split('-');
  return partes.length === 3 ? `${partes[2]}-${partes[1]}-${partes[0]}` : fechaStr;
};

const registrarLog = (proyecto, mensaje) => {
  const ahora = new Date();
  const horaStr = `${String(ahora.getHours()).padStart(2, '0')}:${String(ahora.getMinutes()).padStart(2, '0')}`;
  if (!proyecto.logs) proyecto.logs = [];
  proyecto.logs.unshift({ hora: horaStr, mensaje });
};

const obtenerClaseEstado = (estado) => {
  if (estado === 'Finalizado') return 'badge-success';
  if (estado === 'En Ejecución') return 'badge-warning';
  return 'badge-pending';
};

const nombreTrabajadorTarea = (tarea) => {
  const empId = tarea.empleadoId || tarea.asignacionId;
  const enEquipo = asignacionesActuales.value.find(a => a.empleadoId === empId || a.id === empId);
  return enEquipo?.empleado?.nombre || 'Colaborador';
};

const seleccionarProyecto = (proyecto) => {
  if (proyectoSeleccionado.value?.id === proyecto.id) return;
  proyectoSeleccionado.value = proyecto;
  cargarAsignaciones(proyecto.id);
  cargarTareasDelProyecto(proyecto.id);
  cargarGastosDelProyecto(proyecto.id);
};

// ==========================================
// ACCIONES
// ==========================================
const asignarTrabajador = async () => {
  if (!nuevoMiembro.value.empleadoId || !proyectoSeleccionado.value) return;

  const idNumerico = Number(nuevoMiembro.value.empleadoId);
  const emp = props.empleados.find(e => e.id === idNumerico);
  const rolPorDefecto = emp?.cargo?.nombre || 'Colaborador';

  try {
    const respuesta = await api.createAsignacion({
      proyectoId: Number(proyectoSeleccionado.value.id),
      empleadoId: idNumerico,
      rol: rolPorDefecto
    });

    if (respuesta?.data?.error) {
      alert(`Aviso: ${respuesta.data.error}`);
      return;
    }

    nuevoMiembro.value.empleadoId = '';
    await cargarAsignaciones(proyectoSeleccionado.value.id);
    registrarLog(proyectoSeleccionado.value, `Asignó a ${emp?.nombre} al equipo.`);
    emit('empleado-asignado');
  } catch (error) {
    alert('No se pudo procesar la asignación.');
  }
};

const removerTrabajador = async (asignacionId) => {
  if (!confirm('¿Deseas dar de baja a este colaborador?')) return;
  try {
    await api.deleteAsignacion(asignacionId);
    await cargarAsignaciones(proyectoSeleccionado.value.id);
    registrarLog(proyectoSeleccionado.value, `Removió una asignación del equipo`);
    emit('empleado-asignado');
  } catch (error) {
    alert('No se pudo remover la asignación.');
  }
};

const agregarTarea = async () => {
  if (nuevaTarea.value.fechaLimite < nuevaTarea.value.fechaInicio) {
    alert('La fecha de fin no puede ser anterior.');
    return;
  }

  const emp = props.empleados.find(e => e.id === Number(nuevaTarea.value.empleadoId));

  const tareaPayload = {
    nombre: nuevaTarea.value.nombre,
    descripcion: '',
    proyecto: { id: Number(proyectoSeleccionado.value.id) },
    empleadoId: Number(nuevaTarea.value.empleadoId),
    fechaInicio: nuevaTarea.value.fechaInicio,
    fechaLimite: nuevaTarea.value.fechaLimite,
    estado: 'Pendiente'
  };

  try {
    await axios.post('http://localhost:3000/api/bff/tareas', tareaPayload);
    await cargarTareasDelProyecto(proyectoSeleccionado.value.id);
    registrarLog(proyectoSeleccionado.value, `Agregó tarea "${nuevaTarea.value.nombre}" a ${emp?.nombre}`);
    nuevaTarea.value = { nombre: '', empleadoId: '', fechaInicio: '', fechaLimite: '' };
  } catch (error) {
    alert('Error al guardar la tarea.');
  }
};

const actualizarEstadoTarea = async (tarea) => {
  try {
    const payload = {
      id: tarea.id,
      nombre: tarea.nombre,
      estado: tarea.estado,
      empleadoId: tarea.empleadoId,
      fechaInicio: tarea.fechaInicio,
      fechaLimite: tarea.fechaLimite
    };
    await api.actualizarTarea(tarea.id, payload);
    registrarLog(proyectoSeleccionado.value, `Cambió estado de tarea "${tarea.nombre}" a: ${tarea.estado}`);
  } catch (error) {
    alert('Error al persistir estado.');
  }
};

const eliminarTarea = async (idTarea) => {
  if (!confirm('¿Eliminar esta tarea?')) return;
  try {
    await axios.delete(`http://localhost:3000/api/bff/tareas/${idTarea}`);
    proyectoSeleccionado.value.tareas = proyectoSeleccionado.value.tareas.filter(t => t.id !== idTarea);
  } catch (error) {
    alert('No se pudo borrar la tarea.');
  }
};
</script>
