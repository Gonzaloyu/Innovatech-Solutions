<template>
  <div class="card">
    <h2>Recursos Humanos</h2>
    
    <div v-if="loading" class="spinner"></div>
    
    <div v-else-if="error" class="error">{{ error }}</div>
    <ul v-else>
      <li v-for="empleado in empleados" :key="empleado.id">
        <strong>{{ empleado.nombre }}</strong>
        ({{ empleado.cargo?.nombre }} - {{ empleado.cargo?.nivel }})
        — {{ empleado.departamento?.nombre }}
      </li>
      <li v-if="empleados.length === 0">No hay empleados registrados.</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const empleados = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const response = await api.getEmpleados();
    if (response.data.error) {
      error.value = response.data.error;
    } else {
      empleados.value = response.data;
    }
  } catch (err) {
    error.value = 'Error al conectar con el BFF.';
  } finally {
    loading.value = false;
  }
});
</script>