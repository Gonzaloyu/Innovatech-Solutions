<template>
  <div class="card">
    <h2>Recursos Humanos (Capacity)</h2>
    
    <div v-if="loading">Cargando empleados...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    
    <ul v-else>
      <li v-for="employee in employees" :key="employee.id">
        <strong>{{ employee.fullName }}</strong> ({{ employee.role }}) 
        - <span :class="{ available: employee.isAvailable, busy: !employee.isAvailable }">
            {{ employee.isAvailable ? 'Disponible' : 'Asignado' }}
          </span>
      </li>
      <li v-if="employees.length === 0">No hay empleados registrados.</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const employees = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const response = await api.getEmployees();
    if (response.data.error) {
      error.value = response.data.error;
    } else {
      employees.value = response.data;
    }
  } catch (err) {
    error.value = 'Error al conectar con el BFF.';
  } finally {
    loading.value = false;
  }
});
</script>