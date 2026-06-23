<template>
  <div class="portal-container">

    <div v-if="!empleadoActivo" class="portal-login">
      <div class="welcome-card">
        <h2>Portal del Empleado</h2>
        <p>Ingresa tu nombre para ver tus proyectos asignados</p>
        <div class="form-container">
          <form @submit.prevent="buscarEmpleado">
            <input 
              v-model="nombreBusqueda" 
              placeholder="Tu nombre completo" 
              required 
            />
            <button type="submit" class="btn-empleado">Ingresar</button>
          </form>
        </div>
        <p v-if="errorBusqueda" class="error">{{ errorBusqueda }}</p>
        <button class="btn-volver" @click="$emit('volver')">← Volver</button>
      </div>
    </div>

    <div v-else>
      <div class="portal-header">
        <div>
          <h2>Hola, {{ empleadoActivo.nombre }}</h2>
          <p>{{ empleadoActivo.cargo?.nombre }} — {{ empleadoActivo.departamento?.nombre }}</p>
          <small>Tu valor por hora: ${{ empleadoActivo.valorHora || 0 }}</small>
        </div>
        <button class="btn-volver" @click="cerrarSesion">← Salir</button>
      </div>

      <div class="portal-content">
        <h3>Mis Proyectos y Tareas Asignadas ({{ misProyectos.length }})</h3>

        <div v-if="misProyectos.length === 0" class="empty-msg">
          No tienes proyectos asignados.
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

            <div class="tareas-seccion" v-if="p.tareas && p.tareas.length > 0">
              <h4>Tareas vinculadas:</h4>
              <ul class="lista-tareas">
                <li v-for="tarea in p.tareas" :key="tarea.id" class="tarea-item">
                  <span>📌 {{ tarea.nombre }} — <small class="task-status">{{ tarea.estado }}</small></span>
                  <button 
                    v-if="!['Listo', 'Finalizada', 'Finalizado'].includes(tarea.estado)" 
                    @click="finalizarTarea(p.id, tarea)" 
                    class="btn-small btn-success"
                  >
                    ✓ Finalizar Tarea
                  </button>
                </li>
              </ul>
            </div>

            <div v-if="p.estado?.nombre !== 'Finalizado'" class="reporte-empleado-bloque">
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
import api from '../services/api';

export default {
  emits: ['volver'],
  data() {
    return {
      nombreBusqueda: '',
      empleadoActivo: null,
      misProyectos: [],
      reportes: {}, 
      errorBusqueda: null
    };
  },
  methods: {
    async buscarEmpleado() {
      this.errorBusqueda = null;
      try {
        const res = await api.getEmpleados();
        const empleados = res.data.error ? [] : res.data;
        const encontrado = empleados.find(
          e => e.nombre.toLowerCase() === this.nombreBusqueda.toLowerCase()
        );
        if (!encontrado) {
          this.errorBusqueda = 'Empleado no encontrado. Verifica tu nombre.';
          return;
        }
        this.empleadoActivo = encontrado;
        await this.cargarProyectos();
      } catch (e) {
        this.errorBusqueda = 'Error al buscar empleado.';
      }
    },
    async cargarProyectos() {
      try {
        const res = await api.getProyectosPorEmpleado(this.empleadoActivo.id);
        this.misProyectos = res.data.error ? [] : res.data;
        
        this.misProyectos.forEach(p => {
          this.reportes[p.id] = {
            comentario: '',
            herramientas: [],
            nuevaHerramientaNombre: '',
            nuevaHerramientaCosto: 0
          };
        });
      } catch (e) {
        console.error('Error cargando proyectos:', e);
      }
    },
    agregarHerramienta(proyectoId) {
      const r = this.reportes[proyectoId];
      if (r.nuevaHerramientaNombre.trim() === '') return;

      r.herramientas.push({
        nombre: r.nuevaHerramientaNombre,
        costo: r.nuevaHerramientaCosto || 0
      });

      r.nuevaHerramientaNombre = '';
      r.nuevaHerramientaCosto = 0;
    },
    eliminarHerramienta(proyectoId, index) {
      this.reportes[proyectoId].herramientas.splice(index, 1);
    },
    async finalizarTarea(proyectoId, tarea) {
      if(confirm(`¿Seguro que deseas marcar como LISTA la tarea: ${tarea.nombre}?`)) {
        try {
          // Actualizamos la tarea en el Backend a "Listo"
          const tareaActualizada = { ...tarea, estado: 'Listo' };
          await api.actualizarTarea(tarea.id, tareaActualizada);
          
          // Actualizamos visualmente en el portal
          tarea.estado = 'Listo'; 
          alert('Tarea finalizada y sincronizada con el administrador.');
        } catch (e) {
          console.error(e);
          alert('Error al sincronizar la tarea con el servidor. Verifica que tengas api.actualizarTarea creado.');
        }
      }
    },
    async actualizarEstado(proyecto, estadoId) {
      try {
        const reporteProyecto = this.reportes[proyecto.id];
        const costoHerramientasTotales = reporteProyecto.herramientas.reduce((acc, curr) => acc + curr.costo, 0);

        const body = {
          estadoId: estadoId,
          empleadoCompletaId: this.empleadoActivo.id,
          comentario: reporteProyecto.comentario,
          herramientas: reporteProyecto.herramientas,
          costoHerramientas: costoHerramientasTotales
        };

        if (estadoId === 3) {
          body.fechaFin = new Date().toISOString().split('T')[0];
          
          const fechaInicio = new Date(proyecto.fechaInicio);
          const fechaFin = new Date(body.fechaFin);
          const diasDiferencia = Math.max(1, Math.round((fechaFin - fechaInicio) / (1000 * 60 * 60 * 24)));
          
          const horasDedicadasEstimadas = diasDiferencia * 8; 
          body.horasDedicadas = horasDedicadasEstimadas;
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
    },
    cerrarSesion() {
      this.empleadoActivo = null;
      this.misProyectos = [];
      this.nombreBusqueda = '';
      this.reportes = {};
    }
  }
};
</script>