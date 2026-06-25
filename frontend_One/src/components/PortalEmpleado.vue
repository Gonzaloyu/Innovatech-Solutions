<template>
  <div class="portal-container">

    <div v-if="cargandoPortal" class="portal-login">
      <div class="welcome-card">
        <h2>Portal del Empleado</h2>
        <p>Identificando tu usuario y cargando proyectos...</p>
        <div style="margin: 20px 0; color: #3b82f6;">Sincronizando con Auth0...</div>
        <p v-if="errorBusqueda" class="error">{{ errorBusqueda }}</p>
      </div>
    </div>

    <div v-else-if="!empleadoActivo" class="portal-login">
      <div class="welcome-card">
        <h2>Portal del Empleado</h2>
        <p class="error" style="font-size: 1.1rem; margin-bottom: 20px;">
          ⚠️ Tu cuenta ({{ user?.email }}) no está registrada como empleado en el sistema.
        </p>
        <p>Contacta al administrador para que registre tu correo exactamente igual.</p>
      </div>
    </div>

    <div v-else>
      <div class="portal-header">
        <div>
          <h2>Hola, {{ empleadoActivo.nombre }}</h2>
          <p>{{ empleadoActivo.cargo?.nombre }} — {{ empleadoActivo.departamento?.nombre }}</p>
          <small>Tu valor por hora: ${{ empleadoActivo.valorHora || 0 }}</small>
        </div>
      </div>

      <div class="portal-content">
        <h3>Mis Proyectos y Tareas Asignadas ({{ misProyectos.length }})</h3>

        <div v-if="misProyectos.length === 0" class="empty-msg">
          No tienes proyectos asignados actualmente.
        </div>

        <div v-else class="proyectos-grid">
          <div v-for="p in misProyectos" :key="p.id" class="proyecto-card">
            
            <div class="proyecto-card-header">
              <strong>{{ p.nombre }}</strong>
              <span :class="'status ' + (p.estado?.nombre?.toLowerCase().replace(/ /g, '-') || '')">
                {{ p.estado?.nombre }}
              </span>
            </div>

            <p class="proyecto-desc">{{ p.descripcion }}</p>

            <div class="proyecto-fechas">
              <span><strong>Inicio:</strong> {{ p.fechaInicio }}</span>
              <span><strong>Fin Estimado:</strong> {{ p.fechaFin }}</span>
            </div>

            <div class="tareas-seccion" v-if="p.misTareas && p.misTareas.length > 0">
              <h4>Tus tareas vinculadas:</h4>
              <ul class="lista-tareas">
                <li v-for="tarea in p.misTareas" :key="tarea.id" class="tarea-item">
                  <span>📌 {{ tarea.nombre }}</span>
                  <select
                    v-model="tarea.estado"
                    class="status-select-badge"
                    :class="obtenerClaseEstado(tarea.estado)"
                    @change="cambiarEstadoTarea(tarea)"
                  >
                    <option value="Pendiente">Pendiente</option>
                    <option value="En Ejecución">En Ejecución</option>
                    <option value="Finalizado">Finalizado</option>
                  </select>
                </li>
              </ul>
            </div>
            <div v-else-if="p.tareas && p.tareas.length > 0" class="empty-msg" style="margin-top: 15px; font-size: 0.9em; color: #666;">
              * Este proyecto tiene tareas, pero ninguna está asignada a ti.
            </div>

            <div v-if="p.estado?.nombre !== 'Finalizado' && reportes[p.id]" class="reporte-empleado-bloque">
              <hr />
              <h4>Reporte de Progreso y Herramientas (Proyecto)</h4>
              
              <div class="form-group">
                <label>Comentario de Entrega:</label>
                <textarea
                  v-model="reportes[p.id].comentario"
                  placeholder="Escribe los detalles de lo que hiciste en este proyecto..."
                ></textarea>
              </div>

              <div class="form-group">
                <label>Herramientas / Recursos Utilizados:</label>
                <div class="herramientas-inputs">
                  <input v-model="reportes[p.id].nuevaHerramientaNombre" placeholder="Ej. Máquina Virtual" />
                  <input type="number" v-model.number="reportes[p.id].nuevaHerramientaCosto" placeholder="Costo $" />
                  <button type="button" @click="agregarHerramienta(p.id)" class="btn-add-tool">+</button>
                </div>
                <ul class="lista-herramientas-agregadas">
                  <li v-for="(h, idx) in reportes[p.id].herramientas" :key="idx">
                    🛠️ {{ h.nombre }} (Costó: ${{ h.costo }})
                    <button type="button" @click="eliminarHerramienta(p.id, idx)" class="btn-del-tool">x</button>
                  </li>
                </ul>
              </div>

              <div class="proyecto-acciones">
                <button class="btn-actualizar btn-guardar" @click="actualizarEstado(p, 2)">
                  Guardar Progreso
                </button>
                <button class="btn-actualizar btn-finalizar" @click="actualizarEstado(p, 3)">
                  Finalizar Proyecto
                </button>
              </div>
            </div>

            <div v-else class="proyecto-completado">
              <p>✅ <strong>Proyecto Completado</strong> el {{ p.fechaFin }} por {{ empleadoActivo.nombre }}</p>
              <p class="costo-total-resumen">
                Costo Total Calculado: ${{ p.costoTotalCalculado || 0 }}
              </p>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuth0 } from 'libreria_vue_auth';
import api from '../services/api';

export default {
  name: 'PortalEmpleado',
  emits: ['volver'],
  setup() {
    const { user, isAuthenticated } = useAuth0();
    return { user, isAuthenticated };
  },
  data() {
    return {
      cargandoPortal: true,
      empleadoActivo: null,
      misProyectos: [],
      reportes: {},
      errorBusqueda: null
    };
  },
  watch: {
    user: {
      immediate: true,
      handler(newUser) {
        if (newUser) {
          this.vincularEmpleadoAutomatico();
        } else {
          this.cargandoPortal = false;
        }
      }
    }
  },
  methods: {
    async vincularEmpleadoAutomatico() {
      this.cargandoPortal = true;
      this.errorBusqueda = null;

      try {
        const res = await api.getEmpleados();
        const empleados = res?.data?.error ? [] : (res?.data || res || []);
        
        const encontrado = empleados.find(e =>
          (e.email && e.email.toLowerCase() === this.user?.email?.toLowerCase()) ||
          (e.nombre && e.nombre.toLowerCase() === this.user?.name?.toLowerCase())
        );

        if (!encontrado) {
          this.empleadoActivo = null;
          return;
        }

        this.empleadoActivo = encontrado;
        await this.cargarProyectos();
      } catch (e) {
        console.error("Error al vincular empleado:", e);
        this.errorBusqueda = 'Error de comunicación con el servicio de recursos.';
      } finally {
        this.cargandoPortal = false;
      }
    },
    async cargarProyectos() {
      try {
        const res = await api.getProyectosPorEmpleado(this.empleadoActivo.id);
        const proyectosRaw = res?.data?.error ? [] : (res?.data || res || []);
        
        const nuevosReportes = { ...this.reportes };
        
        this.misProyectos = proyectosRaw.map(p => {
          if (!nuevosReportes[p.id]) {
            nuevosReportes[p.id] = {
              comentario: '',
              herramientas: [],
              nuevaHerramientaNombre: '',
              nuevaHerramientaCosto: 0
            };
          }

          const tareasFiltradas = p.tareas ? p.tareas.filter(tarea => {
            const idEncargado = tarea.empleadoId || (tarea.empleado && tarea.empleado.id) || tarea.trabajadorId;
            return Number(idEncargado) === Number(this.empleadoActivo.id);
          }) : [];

          return {
            ...p,
            misTareas: tareasFiltradas
          };
        });

        this.reportes = nuevosReportes;
      } catch (e) {
        console.error('Error cargando proyectos del empleado:', e);
      }
    },
    async agregarHerramienta(proyectoId) {
      const r = this.reportes[proyectoId];
      if (!r.nuevaHerramientaNombre || r.nuevaHerramientaNombre.trim() === '') return;

      const nombre = r.nuevaHerramientaNombre.trim();
      const costo = parseFloat(r.nuevaHerramientaCosto) || 0;

      try {
        const nuevoGasto = {
          descripcion: nombre,
          monto: costo,
          tipo: 'Herramienta',
          proyecto: { id: proyectoId },
          empleado: { id: this.empleadoActivo.id }
        };

        const response = await fetch('http://localhost:8081/api/gastos', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(nuevoGasto)
        });

        if (response.ok) {
          const dataGasto = await response.json().catch(() => ({}));
          r.herramientas.push({ id: dataGasto.id || null, nombre, costo });
          alert("¡Herramienta registrada exitosamente en los costos del proyecto!");
        } else {
          alert("No se pudo guardar el costo en el servidor.");
        }
      } catch (error) {
        console.error("Error al guardar herramienta en el backend:", error);
        alert("Error de conexión con la base de datos.");
      }

      r.nuevaHerramientaNombre = '';
      r.nuevaHerramientaCosto = 0;
    },
    async eliminarHerramienta(proyectoId, index) {
      const r = this.reportes[proyectoId];
      const herramienta = r.herramientas[index];

      if (herramienta.id) {
        if (!confirm(`¿Seguro que deseas eliminar esta herramienta? Se borrará permanentemente de la base de datos de gastos.`)) {
          return;
        }
        try {
          const response = await fetch(`http://localhost:8081/api/gastos/${herramienta.id}`, {
            method: 'DELETE'
          });
          if (!response.ok) {
            console.warn("No se pudo borrar del servidor, procediendo a remover de la interfaz local.");
          }
        } catch (error) {
          console.error("Error al conectar con la API de borrado de gastos:", error);
        }
      }
      
      r.herramientas.splice(index, 1);
    },

    async cambiarEstadoTarea(tarea) {
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
      } catch (e) {
        console.error(e);
        alert('Error al sincronizar la tarea con el servidor.');
      }
    },

    obtenerClaseEstado(estado) {
      if (estado === 'Finalizado') return 'badge-success';
      if (estado === 'En Ejecución') return 'badge-warning';
      return 'badge-pending';
    },

    async actualizarEstado(proyecto, estadoId) {
      try {
        const reporteProyecto = this.reportes[proyecto.id];
        const costoHerramientasTotales = reporteProyecto.herramientas.reduce((acc, curr) => acc + curr.costo, 0);

        const body = {
          estadoId: estadoId,
          empleadoCompletaId: this.empleadoActivo.id,
          comentario: reporteProyecto.comentario,
          herramientas: reporteProyecto.herramientas.map(h => ({ nombre: h.nombre, costo: h.costo })),
          costoHerramientas: costoHerramientasTotales
        };

        if (estadoId === 3) {
          body.fechaFin = new Date().toISOString().split('T')[0];
          
          const fechaInicio = new Date(proyecto.fechaInicio);
          const fechaFin = new Date(body.fechaFin);
          const diasDiferencia = Math.max(1, Math.round((fechaFin - fechaInicio) / (1000 * 60 * 60 * 24)));
          
          const horasDedicadasEstimadas = diasDiferencia * 8;
          body.horasDedicadas = horasDedicadasEstimadas;

          const valorHoraEmpleado = this.empleadoActivo.valorHora || 0;
          const costoManoObraCalculado = horasDedicadasEstimadas * valorHoraEmpleado;

          try {
            await fetch('http://localhost:8081/api/gastos', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                descripcion: `Mano de Obra - ${this.empleadoActivo.nombre} (${horasDedicadasEstimadas} hrs)`,
                monto: costoManoObraCalculado,
                tipo: 'Mano de Obra',
                proyecto: { id: proyecto.id },
                empleado: { id: this.empleadoActivo.id }
              })
            });
          } catch (errGasto) {
            console.error("No se pudo auto-registrar el costo de mano de obra:", errGasto);
          }
        }

        await api.actualizarEstadoProyecto(proyecto.id, body);
        
        if (estadoId === 3) {
          alert('Proyecto finalizado y analíticas registradas con éxito');
        } else {
          alert('Progreso guardado en ejecución con éxito');
        }
        
        await this.cargarProyectos();
      } catch (e) {
        alert('Error al actualizar estado');
      }
    }
  }
};
</script>