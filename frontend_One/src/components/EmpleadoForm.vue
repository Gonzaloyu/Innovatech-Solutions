<template>
  <div class="form-container">
    <h3>Registrar Empleado</h3>
    <form @submit.prevent="guardarEmpleado">
      <input v-model="nuevoEmpleado.nombre" placeholder="Nombre Completo" required />
      <input v-model="nuevoEmpleado.cargo" placeholder="Cargo (Ej: Arquitecto)" required />
      <input v-model="nuevoEmpleado.departamento" placeholder="Departamento" required />
      <input v-model="nuevoEmpleado.salario" type="number" placeholder="Salario" required />
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
        cargo: '',
        departamento: '',
        salario: null
      }
    };
  },
  methods: {
    async guardarEmpleado() {
      try {
        await api.createEmployee(this.nuevoEmpleado);
        alert('Empleado registrado con éxito');
        this.$emit('empleado-creado');
        this.nuevoEmpleado = { nombre: '', cargo: '', departamento: '', salario: null };
      } catch (error) {
        alert('Error: ' + (error.response?.data?.error || 'Servicio caído'));
      }
    }
  }
};
</script>