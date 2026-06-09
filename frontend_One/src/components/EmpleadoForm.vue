<template>
  <div class="form-container">
    <h3>Registrar Empleado</h3>
    <form @submit.prevent="guardarEmpleado">
      <input v-model="nuevoEmpleado.nombre" placeholder="Nombre Completo" required />
      <input v-model="nuevoEmpleado.email" type="email" placeholder="Email" required />

      <select v-model="nuevoEmpleado.departamento.id" required>
        <option disabled value="">Seleccionar Departamento</option>
        <option value="1">Gestion</option>
        <option value="2">Desarrollo</option>
        <option value="3">Consultoría</option>
      </select>

      <select v-model="nuevoEmpleado.cargo.id" required>
        <option disabled value="">Seleccionar Cargo</option>
        <option value="1">Administrador</option>
        <option value="2">Desarrollador Senior</option>
        <option value="3">Testing</option>
      </select>

      <button type="submit" class="btn-empleado">Registrar Empleado</button>
    </form>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      nuevoEmpleado: {
        nombre: '',
        email: '',
        departamento: { id: '' },
        cargo: { id: '' }
      }
    };
  },
  methods: {
    async guardarEmpleado() {
      try {
        await api.createEmpleado(this.nuevoEmpleado);
        alert('Empleado registrado con éxito');
        this.$emit('empleado-creado');
        this.nuevoEmpleado = {
          nombre: '', email: '',
          departamento: { id: '' },
          cargo: { id: '' }
        };
      } catch (error) {
        alert('Error: ' + (error.response?.data?.error || 'Servicio caído'));
      }
    }
  }
};
</script>