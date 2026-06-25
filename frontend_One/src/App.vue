<template>
  <div id="app">
    <header class="main-header">
      <div class="header-title">
        <h1>Innovatech Solutions</h1>
        <p>Plataforma Inteligente de Gestión Integral</p>
      </div>
      
      <div v-if="isAuthenticated" class="user-nav">
        <span class="user-name">Hola, {{ user?.email }}</span>
        
        <button @click="cerrarSesionApp" class="auth-btn logout">
          Cerrar Sesión
        </button>
      </div>
    </header>

    <div v-if="isLoading" class="loading-screen">
      <p>Verificando credenciales...</p>
    </div>

    <template v-else-if="isAuthenticated">
      <template v-if="rol === 'admin'">
        
        <div class="kpi-container">
          <div class="kpi-card green">
            <p class="kpi-numero">{{ kpisCalculados.totalProyectos }}</p>
            <p class="kpi-label">Total Proyectos</p>
          </div>
          <div class="kpi-card blue">
            <p class="kpi-numero">{{ kpisCalculados.proyectosEnEjecucion }}</p>
            <p class="kpi-label">En Ejecución</p>
          </div>
          <div class="kpi-card red">
            <p class="kpi-numero">{{ kpisCalculados.proyectosAtrasados }}</p>
            <p class="kpi-label">Atrasados</p>
          </div>
          <div class="kpi-card purple">
            <p class="kpi-numero">{{ kpisCalculados.totalEmpleados }}</p>
            <p class="kpi-label">Empleados</p>
          </div>
          <div class="kpi-card orange">
            <p class="kpi-numero">{{ kpisCalculados.totalAsignaciones }}</p>
            <p class="kpi-label">Asignaciones</p>
          </div>
        </div>

        <div class="page-container">
          <div class="forms-row">
            <div class="module">
              <div class="module-header">
                <h2>Gestión de Proyectos</h2>
                <span class="badge mysql">MySQL</span>
              </div>
              <ProyectoForm @proyecto-creado="cargarDatos" />
              <button class="btn-ver" @click="abrirModal('proyectos')">
                Ver Proyectos ({{ proyectos.length }})
              </button>
            </div>

            <div class="module">
              <div class="module-header">
                <h2>Gestión de Recursos</h2>
                <span class="badge postgres">PostgreSQL</span>
              </div>
              <EmpleadoForm @empleado-creado="cargarDatos" />
              <button class="btn-ver" @click="abrirModal('empleados')">
                Ver Empleados ({{ empleados.length }})
              </button>
            </div>
          </div>

          <div class="module" style="margin-top: 30px;">
            <AdminPlanning 
              :proyectos="proyectosParaPlanning"
              :empleados="empleados"
              @empleado-asignado="cargarDatos"
            />
          </div>
        </div>

        <div v-if="modalActivo" class="modal-overlay" @click.self="cerrarModal">
          <div class="modal-box">
            <div class="modal-header">
              <h2 v-if="modalActivo === 'proyectos'">Proyectos Registrados</h2>
              <h2 v-else>Empleados Registrados</h2>
              <button class="modal-close" @click="cerrarModal">✕</button>
            </div>

            <div v-if="modalActivo === 'proyectos'">
              <div v-if="proyectos.length === 0" class="empty-msg">
                No hay proyectos registrados.
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Estado</th>
                    <th>Categoría</th>
                    <th>Cliente</th>
                    <th>Fecha Inicio</th>
                    <th>Fecha Fin</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="p in proyectos" :key="p.id">
                    <td><strong>{{ p.nombre }}</strong></td>
                    <td>
                      <span :class="claseEstado(p.estado?.nombre)">
                        {{ p.estado?.nombre }}
                      </span>
                    </td>
                    <td>{{ p.categoria?.nombre }}</td>
                    <td>{{ p.cliente?.nombreEmpresa }}</td>
                    <td>{{ p.fechaInicio }}</td>
                    <td>{{ p.fechaFin }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="modalActivo === 'empleados'">
              <div v-if="empleados.length === 0" class="empty-msg">
                No hay empleados registrados.
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Cargo</th>
                    <th>Nivel</th>
                    <th>Departamento</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="e in empleados" :key="e.id">
                    <td><strong>{{ e.nombre }}</strong></td>
                    <td>{{ e.email }}</td>
                    <td>{{ e.cargo?.nombre }}</td>
                    <td>{{ e.cargo?.nivel }}</td>
                    <td>{{ e.departamento?.nombre }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </template>

      <template v-else>
        <PortalEmpleado />
      </template>

    </template>

    <main v-else class="login-welcome">
      <div class="welcome-card">
        <h2>Acceso Restringido</h2>
        <p>Inicia sesión para acceder a la plataforma.</p>
        <button @click="loginWithRedirect()" class="auth-btn login">
          Iniciar Sesión
        </button>
      </div>
    </main>

    <footer class="main-footer">
      <p>&copy; 2026 Innovatech Solutions — Arquitectura de Microservicios</p>
    </footer>
  </div>
</template>

<script>
import { useAuth0 } from 'libreria_vue_auth';
import { computed } from 'vue'; 
import api from './services/api';
import ProyectoForm from './components/ProyectoForm.vue';
import EmpleadoForm from './components/EmpleadoForm.vue';
import PortalEmpleado from './components/PortalEmpleado.vue';
import AdminPlanning from './components/AdminPlanning.vue';

export default {
  name: 'App',
  components: { ProyectoForm, EmpleadoForm, PortalEmpleado, AdminPlanning },
  setup() {
    const { loginWithRedirect, logout, user, isAuthenticated, isLoading } = useAuth0();

    
    const rol = computed(() => {
      if (!user.value) return null;
      
      if (user.value.email === 'chalonecul@gmail.com') {
        return 'admin';
      }
      
      const roles = user.value['https://innovatech.com/roles'] || [];
      return roles[0]; 
    });

    return { 
      loginWithRedirect, 
      logout, 
      user, 
      isAuthenticated, 
      isLoading,
      rol 
    };
  },
  data() {
    return {
      proyectos: [],
      empleados: [],
      kpisBackend: null,
      modalActivo: null
    };
  },
  computed: {
    kpisCalculados() {
      let enEjecucion = 0;
      let atrasados = 0;

      this.proyectos.forEach(p => {
        const estadoLimpio = this.limpiarTexto(p.estado?.nombre || p.estado);
        if (estadoLimpio === 'en ejecucion') enEjecucion++;
        if (estadoLimpio === 'atrasado' || estadoLimpio === 'atrasados') atrasados++;
      });

      return {
        totalProyectos: this.proyectos.length,
        proyectosEnEjecucion: enEjecucion,
        proyectosAtrasados: atrasados,
        totalEmpleados: this.empleados.length,
        totalAsignaciones: this.kpisBackend?.totalAsignaciones || 0
      };
    },
    proyectosParaPlanning() {
      return this.proyectos.map(p => ({
        ...p,
        estado: p.estado && typeof p.estado === 'object' ? p.estado.nombre : (p.estado || 'En Planificación'),
        responsable: p.responsable || 'Sin asignar'
      }));
    }
  },
  watch: {
   
    rol: {
      immediate: true,
      handler(nuevoRol) {
        if (nuevoRol === 'admin') {
          this.cargarDatos();
        }
      }
    },

    isAuthenticated(autenticado) {
      if (autenticado && this.rol === 'admin') {
        this.cargarDatos();
      }
    }
  },
  methods: {
    limpiarTexto(texto) {
      if (!texto) return '';
      return texto
        .toString()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "") 
        .toLowerCase()
        .trim();
    },
    claseEstado(estado) {
      const limpio = this.limpiarTexto(estado);
      return 'status ' + limpio.replace(/ /g, '-');
    },
    async cargarDatos() {
      if (!this.isAuthenticated || this.rol !== 'admin') return;
      try {
        console.log("Cargando datos para el Administrador desde API...");
        const [resProyectos, resEmpleados, resKpis] = await Promise.all([
          api.getProyectos(),
          api.getEmpleados(),
          api.getKpis()
        ]);

        this.proyectos = resProyectos?.data?.error ? [] : (resProyectos?.data || resProyectos || []);
        this.empleados = resEmpleados?.data?.error ? [] : (resEmpleados?.data || resEmpleados || []);
        console.log(`Datos cargados -> Proyectos: ${this.proyectos.length}, Empleados: ${this.empleados.length}`);
        this.kpisBackend = resKpis?.data?.error ? null : (resKpis?.data || resKpis || null);

      } catch (error) {
        console.error('Error al cargar datos en App.vue:', error);
      }
    },
    // En App.vue → methods
    abrirModal(tipo) {
      this.modalActivo = tipo;
    },
    cerrarModal() {       
      this.modalActivo = null;
    },
    cerrarSesionApp() {
      this.proyectos = [];
      this.empleados = [];
      this.kpisBackend = null;
      this.modalActivo = null;
      this.logout({ logoutParams: { returnTo: window.location.origin } });
    }
  },
  mounted() {
    if (this.isAuthenticated && this.rol === 'admin') {
      this.cargarDatos();
    }
  }
};
</script>

<style>
@import './assets/planning.css';
@import './assets/dashboard.css';
</style>