<template>
  <div class="form-container">
    <h3>Registrar Nuevo Proyecto</h3>
    <form @submit.prevent="guardarProyecto">
      
      <div class="campo-grupo">
        <label for="nombre">Nombre del Proyecto</label>
        <input id="nombre" v-model="nuevoProyecto.nombre" placeholder="Ej. Rediseño web" required />
      </div>

      <div class="campo-grupo">
        <textarea v-model="nuevoProyecto.descripcion" placeholder="Descripción"></textarea>
      </div>
      
      <div class="campo-grupo">
        <label for="fechaInicio">Fecha Inicio</label>
        <input id="fechaInicio" v-model="nuevoProyecto.fechaInicio" type="date" required />
      </div>

      <div class="campo-grupo">
        <label for="fechaFin">Fecha Fin</label>
        <input id="fechaFin" v-model="nuevoProyecto.fechaFin" type="date" required />
      </div>

      <div class="campo-grupo">
        <select v-model="nuevoProyecto.estado.id" required>
          <option disabled value="">Seleccionar Estado</option>
          <option :value="1">En Planificación</option>
          <option :value="2">En Ejecución</option>
          <option :value="3">Finalizado</option>
        </select>
      </div>

      <div class="campo-grupo">
        <select v-model="nuevoProyecto.categoria.id" required>
          <option disabled value="">Seleccionar Categoría</option>
          <option :value="1">Desarrollo</option>
          <option :value="2">Infraestructura</option>
          <option :value="3">Consultoría</option>
        </select>
      </div>

      <div class="campo-grupo">
        <select v-model="nuevoProyecto.cliente.id" required>
          <option disabled value="">Seleccionar Cliente</option>
          <option :value="1">Empresa Demo 1</option>
          <option :value="2">Empresa Demo 2</option>
        </select>
      </div>

      <div class="campo-grupo">
        <select v-model="nuevoProyecto.empleadoId">
          <option :value="null">Sin empleado asignado</option>
          <option v-for="emp in empleados" :key="emp.id" :value="emp.id">
            {{ emp.nombre }} — {{ emp.departamento?.nombre }}
          </option>
        </select>
      </div>

      <button type="submit" class="btn-proyecto">Guardar Proyecto</button>
    </form>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      empleados: [],
      nuevoProyecto: {
        nombre: '',
        descripcion: '',
        fechaInicio: '',
        fechaFin: '',
        estado: { id: '' },      
        categoria: { id: '' },
        cliente: { id: '' },    
        empleadoId: null
      }
    };
  },
  async mounted() {
    try {
      const res = await api.getEmpleados();
      this.empleados = res.data.error ? [] : res.data;
    } catch (e) {
      console.error('Error cargando empleados:', e);
    }
  },
  methods: {
    async guardarProyecto() {
      try {
        const respuesta = await api.createProyecto(this.nuevoProyecto);
        if (respuesta.data?.error && !respuesta.data?.id) {
          alert('Problema en el backend: ' + respuesta.data.error);
          return;
        }
        alert('Proyecto guardado con éxito');
        this.$emit('proyecto-creado');
        
        this.nuevoProyecto = {
          nombre: '', 
          descripcion: '', 
          fechaInicio: '', 
          fechaFin: '',
          estado: { id: '' },   
          categoria: { id: '' },  
          cliente: { id: '' },   
          empleadoId: null
        };
      } catch (error) {
        alert('Error crítico de red: ' + error.message);
      }
    }
  }
};
</script>

