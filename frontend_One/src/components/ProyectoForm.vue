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
        <option value="1">En Planificación</option>
        <option value="2">En Ejecución</option>
        <option value="3">Finalizado</option>
      </select>

      <select v-model="nuevoProyecto.categoria.id" required>
        <option disabled value="">Seleccionar Categoría</option>
        <option value="1">Desarrollo</option>
        <option value="2">Infraestructura</option>
        <option value="3">Consultoría</option>
      </select>

      <select v-model="nuevoProyecto.cliente.id" required>
        <option disabled value="">Seleccionar Cliente</option>
        <option value="1">Cliente 1</option>
        <option value="2">Cliente 2</option>
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
      nuevoProyecto: {
        nombre: '',
        descripcion: '',
        fechaInicio: '',
        fechaFin: '',
        estado: { id: '' },
        categoria: { id: '' },
        cliente: { id: '' }
      }
    };
  },
  methods: {
    async guardarProyecto() {
      try {
        const respuesta = await api.createProyecto(this.nuevoProyecto);
        if (respuesta.data?.error) {
          alert('Problema en el backend: ' + respuesta.data.error);
          return;
        }
        alert('Proyecto guardado con éxito');
        this.$emit('proyecto-creado');
        this.nuevoProyecto = {
          nombre: '', descripcion: '', fechaInicio: '', fechaFin: '',
          estado: { id: '' }, categoria: { id: '' }, cliente: { id: '' }
        };
      } catch (error) {
        alert('Error crítico de red: ' + error.message);
      }
    }
  }
};
</script>