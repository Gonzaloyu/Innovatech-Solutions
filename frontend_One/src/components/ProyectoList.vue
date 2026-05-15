<template>
  <div class="card">
    <h2>Gestión de Proyectos</h2>
    <div v-if="loading">Cargando proyectos...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <ul v-else>
      <li v-for="proyecto in proyectos" :key="proyecto.id">
        <strong>{{ proyecto.nombre }}</strong>
        — Estado: {{ proyecto.estado?.nombre }}
        — Cliente: {{ proyecto.cliente?.nombreEmpresa }}
      </li>
      <li v-if="proyectos.length === 0">No hay proyectos registrados.</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const proyectos = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const response = await api.getProyectos();
    if (response.data.error) {
      error.value = response.data.error;
    } else {
      proyectos.value = response.data;
    }
  } catch (err) {
    error.value = 'Error al conectar con el BFF.';
  } finally {
    loading.value = false;
  }
});
</script>