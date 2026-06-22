<template>
  <div class="portal-container">

    <!-- Login Empleado -->
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

    <!-- Dashboard del Empleado -->
    <div v-else>
      <div class="portal-header">
        <div>
          <h2>Hola, {{ empleadoActivo.nombre }}</h2>
          <p>{{ empleadoActivo.cargo?.nombre }} — {{ empleadoActivo.departamento?.nombre }}</p>
        </div>
        <button class="btn-volver" @click="cerrarSesion">← Salir</button>
      </div>

      <div class="portal-content">
        <h3>Mis Proyectos Asignados ({{ misProyectos.length }})</h3>

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
              <span>Inicio: {{ p.fechaInicio }}</span>
              <span>Fin: {{ p.fechaFin }}</span>
            </div>

            <!-- Cambiar estado -->
            <div v-if="p.estado?.nombre !== 'Finalizado'" class="proyecto-acciones">
              <select v-model="cambios[p.id]">
                <option disabled value="">Cambiar estado...</option>
                <option value="2">En Ejecución</option>
                <option value="3">Finalizado</option>
              </select>
              <button 
                v-if="cambios[p.id]" 
                class="btn-actualizar"
                @click="actualizarEstado(p)"
              >
                Actualizar
              </button>
            </div>

            <!-- Completado -->
            <div v-else class="proyecto-completado">
              Completado el {{ p.fechaFin }} por {{ empleadoActivo.nombre }}
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
      cambios: {},
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
      } catch (e) {
        console.error('Error cargando proyectos:', e);
      }
    },
    async actualizarEstado(proyecto) {
      try {
        const estadoId = parseInt(this.cambios[proyecto.id]);
        const body = {
          estadoId,
          empleadoCompletaId: this.empleadoActivo.id
        };
        if (estadoId === 3) {
          body.fechaFin = new Date().toISOString().split('T')[0];
        }
        await api.actualizarEstadoProyecto(proyecto.id, body);
        alert('Estado actualizado con éxito');
        this.cambios[proyecto.id] = '';
        await this.cargarProyectos();
      } catch (e) {
        alert('Error al actualizar estado');
      }
    },
    cerrarSesion() {
      this.empleadoActivo = null;
      this.misProyectos = [];
      this.nombreBusqueda = '';
    }
  }
};
</script>

