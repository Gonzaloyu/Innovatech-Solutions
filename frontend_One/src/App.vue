<template>
  <div id="app">
    <header class="main-header">
      <h1>Innovatech Solutions</h1>
      <p>Plataforma Inteligente de Gestión Integral</p>
      <div v-if="isAuthenticated" class="user-nav">
        <span class="user-name">Hola, {{ user?.name }}</span>
        <button @click="logout({ logoutParams: { returnTo: window.location.origin } })"
                class="auth-btn logout">
          Cerrar Sesión
        </button>
      </div>
    </header>

    <div v-if="isLoading" class="loading-screen">
      <p>Verificando credenciales...</p>
    </div>

    <main v-else-if="isAuthenticated" class="dashboard-container">

      <!-- Módulo Proyectos -->
      <section class="module">
        <div class="module-header">
          <h2>Gestión de Proyectos</h2>
          <span class="badge mysql">MySQL</span>
        </div>
        <ProyectoForm @proyecto-creado="cargarDatos" />
        <div class="list-container">
          <div v-if="proyectos.length === 0" class="empty-msg">
            No hay proyectos registrados.
          </div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Cliente</th>
                <th>Fecha Inicio</th>
                <th>Fecha Fin</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in proyectos" :key="p.id">
                <td><strong>{{ p.nombre }}</strong></td>
                <td>
                  <span :class="'status ' + (p.estado?.nombre?.toLowerCase().replace(/ /g, '-') || '')">
                    {{ p.estado?.nombre }}
                  </span>
                </td>
                <td>{{ p.cliente?.nombreEmpresa }}</td>
                <td>{{ p.fechaInicio }}</td>
                <td>{{ p.fechaFin }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Módulo Empleados -->
      <section class="module">
        <div class="module-header">
          <h2>Gestión de Recursos</h2>
          <span class="badge postgres">PostgreSQL</span>
        </div>
        <EmpleadoForm @empleado-creado="cargarDatos" />
        <div class="list-container">
          <div v-if="empleados.length === 0" class="empty-msg">
            No hay empleados registrados.
          </div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Email</th>
                <th>Cargo</th>
                <th>Departamento</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="e in empleados" :key="e.id">
                <td><strong>{{ e.nombre }}</strong></td>
                <td>{{ e.email }}</td>
                <td>{{ e.cargo?.nombre }}</td>
                <td>{{ e.departamento?.nombre }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>

    <main v-else class="login-welcome">
      <div class="welcome-card">
        <h2>Acceso Restringido</h2>
        <p>Inicia sesión para gestionar proyectos y recursos.</p>
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
import { useAuth0 } from 'libreria_vue_auth';
import api from './services/api';
import ProyectoForm from './components/ProyectoForm.vue';
import EmpleadoForm from './components/EmpleadoForm.vue';
import './assets/dashboard.css';

export default {
  name: 'App',
  components: { ProyectoForm, EmpleadoForm },
  setup() {
    const { loginWithRedirect, logout, user, isAuthenticated, isLoading } = useAuth0();
    return { loginWithRedirect, logout, user, isAuthenticated, isLoading };
  },
  data() {
    return { proyectos: [], empleados: [] };
  },
  methods: {
    async cargarDatos() {
      if (!this.isAuthenticated) return;
      try {
        const [resProyectos, resEmpleados] = await Promise.all([
          api.getProyectos(),
          api.getEmpleados()
        ]);
        this.proyectos = resProyectos.data.error ? [] : resProyectos.data;
        this.empleados = resEmpleados.data.error ? [] : resEmpleados.data;
      } catch (error) {
        console.error('Error al cargar datos:', error);
      }
    }
  },
  mounted() {
    if (this.isAuthenticated) this.cargarDatos();
  },
  watch: {
    isAuthenticated(newVal) {
      if (newVal) this.cargarDatos();
    }
  }
};
</script>

<style>

.user-nav {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}
</style>