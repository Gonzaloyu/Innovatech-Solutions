<template>
  <div class="card">
    <h2>Gestión de Proyectos</h2>
    <div v-if="loading">Cargando proyectos...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <ul v-else>
      <li v-for="project in projects" :key="project.id">
        <strong>{{ project.name }}</strong> - Estado: {{ project.status }}
      </li>
      <li v-if="projects.length === 0">No hay proyectos registrados.</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const projects = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const response = await api.getProjects();
    // Validamos si el BFF nos mandó el error del Circuit Breaker
    if (response.data.error) {
      error.value = response.data.error;
    } else {
      projects.value = response.data;
    }
  } catch (err) {
    error.value = 'Error al conectar con el BFF.';
  } finally {
    loading.value = false;
  }
});
</script>