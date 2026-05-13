<template>
  <div class="form-container">
    <h3>Registrar Nuevo Proyecto</h3>
    <form @submit.prevent="guardarProyecto">
      <input v-model="nuevoProyecto.nombre" placeholder="Nombre del Proyecto" required />
      <textarea v-model="nuevoProyecto.descripcion" placeholder="Descripción"></textarea>
      <input v-model="nuevoProyecto.fechaInicio" type="date" required />
      <select v-model="nuevoProyecto.estado">
        <option value="Activo">Activo</option>
        <option value="En Pausa">En Pausa</option>
        <option value="Finalizado">Finalizado</option>
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
        estado: 'Activo'
      }
    };
  },
  methods: {
    async guardarProyecto() {
      try {
        const respuesta = await api.createProject(this.nuevoProyecto);
        if (respuesta.data && respuesta.data.error) {
          alert('Ups, problema en el backend: ' + respuesta.data.error);
          return; // Detenemos la ejecución aquí
        }

        alert('Proyecto guardado con éxito');
        this.$emit('proyecto-creado'); 
        this.nuevoProyecto = { nombre: '', descripcion: '', fechaInicio: '', estado: 'Activo' }; 
      } catch (error) {
        alert('Error crítico de red: ' + error.message);
      }
    }
  }
};
</script>