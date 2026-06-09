<template>
  <div class="form-container">
    <h3>Registrar Nuevo Proyecto</h3>
    <form @submit.prevent="guardarProyecto">
      <input v-model="nuevoProyecto.nombre" placeholder="Nombre del Proyecto" required />
      <textarea v-model="nuevoProyecto.descripcion" placeholder="Descripción"></textarea>
      <input v-model="nuevoProyecto.fechaInicio" type="date" required />
      <input v-model="nuevoProyecto.fechaFin" type="date" required />

      <select v-model="nuevoProyecto.estado.id" required>
        <option disabled value="">Seleccionar Estado</option>
        <option :value="1">En Planificacion</option>
        <option :value="2">En Ejecucion</option>
        <option :value="3">Finalizado</option>
      </select>

      <select v-model="nuevoProyecto.categoria.id" required>
        <option disabled value="">Seleccionar Categoría</option>
        <option :value="1">Gestion</option>
        <option :value="2">Desarrollo</option>
        <option :value="3">Consultoria</option>
      </select>

      <select v-model="nuevoProyecto.cliente.id" required>
        <option disabled value="">Seleccionar Cliente</option>
        <option :value="1">Empresa 1</option>
        <option :value="2">Empresa 2</option>
      </select>

      <!-- Selector de empleado -->
      <select v-model="nuevoProyecto.empleadoId">
        <option :value="null">Sin empleado asignado</option>
        <option v-for="emp in empleados" :key="emp.id" :value="emp.id">
          {{ emp.nombre }} — {{ emp.departamento?.nombre }}
        </option>
      </select>

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