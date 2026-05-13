<template>
  <div id="app">
    <header class="main-header">
      <h1>Innovatech Solutions</h1>
      <p>Plataforma Inteligente de Gestión Integral</p>
      
      <div v-if="isAuthenticated" class="user-nav">
        <span class="user-name">Hola, {{ user?.name }}</span>
        <button @click="logout({ returnTo: window.location.origin })" class="auth-btn logout">
          Cerrar Sesión
        </button>
      </div>
    </header>

    <div v-if="isLoading" class="loading-screen">
      <p>Verificando credenciales...</p>
    </div>

    <main v-else-if="isAuthenticated" class="dashboard-container">
      <section class="module">
        <div class="module-header">
          <h2>Gestión de Proyectos</h2>
          <span class="badge mysql">MySQL</span>
        </div>
        <ProyectoForm @proyecto-creado="cargarDatos" />
        <div class="list-container">
          <h3>Proyectos Actuales</h3>
          <div v-if="proyectos.length === 0" class="empty-msg">No hay proyectos registrados.</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Fecha Inicio</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in proyectos" :key="p.id">
                <td><strong>{{ p.nombre }}</strong></td>
                <td><span :class="'status ' + p.estado.toLowerCase().replace(' ', '-')">{{ p.estado }}</span></td>
                <td>{{ p.fechaInicio }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="module">
        <div class="module-header">
          <h2>Gestión de Recursos</h2>
          <span class="badge postgres">PostgreSQL</span>
        </div>
        <EmpleadoForm @empleado-creado="cargarDatos" />
        <div class="list-container">
          <h3>Crear Empleados</h3>
          <div v-if="empleados.length === 0" class="empty-msg">No hay empleados registrados.</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Cargo</th>
                <th>Departamento</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="e in empleados" :key="e.id">
                <td><strong>{{ e.nombre }}</strong></td>
                <td>{{ e.cargo }}</td>
                <td>{{ e.departamento }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>

    <main v-else class="login-welcome">
      <div class="welcome-card">
        <h2>Acceso Restringido</h2>
        <p>Por favor, inicia sesión para gestionar los proyectos y recursos de la plataforma.</p>
        <button @click="loginWithRedirect()" class="auth-btn login">
          Iniciar Sesión con Auth0
        </button>
      </div>
    </main>

    <footer class="main-footer">
      <p>&copy; 2026 Innovatech Solutions - Arquitectura de Microservicios</p>
    </footer>
  </div>
</template>

<script>
import api from './services/api';
import ProyectoForm from './components/ProyectoForm.vue';
import EmpleadoForm from './components/EmpleadoForm.vue';

// IMPORTACIÓN DE TU LIBRERÍA
import { useAuth0 } from 'libreria_vue_auth';

// IMPORTACIÓN DEL CSS EXTERNO
import './assets/dashboard.css'; 

export default {
  name: 'App',
  components: { ProyectoForm, EmpleadoForm },
  setup() {
    // Usamos el hook de tu librería
    const { loginWithRedirect, logout, user, isAuthenticated, isLoading } = useAuth0();
    
    return {
      loginWithRedirect,
      logout,
      user,
      isAuthenticated,
      isLoading,
      window
    };
  },
  data() {
    return { proyectos: [], empleados: [] };
  },
  methods: {
    async cargarDatos() {
      // Solo intentamos cargar datos si el usuario está autenticado
      if (!this.isAuthenticated) return;

      try {
        const resProyectos = await api.getProjects();
        const resEmpleados = await api.getEmployees();
        this.proyectos = resProyectos.data.error ? [] : resProyectos.data;
        this.empleados = resEmpleados.data.error ? [] : resEmpleados.data;
      } catch (error) {
        console.error("Error al cargar datos:", error);
      }
    }
  },
  mounted() {
    // Solo cargamos datos si ya pasó el proceso de autenticación
    if (this.isAuthenticated) {
      this.cargarDatos();
    }
  },
  watch: {
    // Si el estado de autenticación cambia a verdadero, cargamos los datos automáticamente
    isAuthenticated(newVal) {
      if (newVal) {
        this.cargarDatos();
      }
    }
  }
};
</script>

<style>
/* Estilos rápidos para la autenticación */
.user-nav {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}
.user-name { font-weight: bold; color: #fff; }
.auth-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}
.login { background-color: #28a745; color: white; margin-top: 20px; }
.logout { background-color: #dc3545; color: white; font-size: 0.8rem; }
.login-welcome {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
}
.welcome-card {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}
.loading-screen { text-align: center; padding: 50px; }
</style>