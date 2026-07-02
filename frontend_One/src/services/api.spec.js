import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import api from './api'

vi.mock('axios', () => {
  const mockApiClient = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  }
  return {
    default: {
      create: vi.fn(() => mockApiClient),
    },
  }
})

const apiClient = axios.create()

describe('api.js', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  // PROYECTOS
  it('getProyectos hace GET a /proyectos', () => {
    api.getProyectos()
    expect(apiClient.get).toHaveBeenCalledWith('/proyectos')
  })

  it('createProyecto hace POST a /proyectos con los datos', () => {
    const data = { nombre: 'Proyecto X' }
    api.createProyecto(data)
    expect(apiClient.post).toHaveBeenCalledWith('/proyectos', data)
  })

  it('actualizarProyecto hace PUT a /proyectos/:id con los datos', () => {
    const data = { nombre: 'Actualizado' }
    api.actualizarProyecto(5, data)
    expect(apiClient.put).toHaveBeenCalledWith('/proyectos/5', data)
  })

  it('actualizarEstadoProyecto hace PATCH a /proyectos/:id/estado', () => {
    const data = { estado: 'finalizado' }
    api.actualizarEstadoProyecto(5, data)
    expect(apiClient.patch).toHaveBeenCalledWith('/proyectos/5/estado', data)
  })

  it('getProyectosPorEmpleado hace GET a /proyectos/empleado/:id', () => {
    api.getProyectosPorEmpleado(3)
    expect(apiClient.get).toHaveBeenCalledWith('/proyectos/empleado/3')
  })

  // TAREAS
  it('getTareas hace GET a /tareas', () => {
    api.getTareas()
    expect(apiClient.get).toHaveBeenCalledWith('/tareas')
  })

  it('createTarea hace POST a /tareas con los datos', () => {
    const data = { titulo: 'Nueva tarea' }
    api.createTarea(data)
    expect(apiClient.post).toHaveBeenCalledWith('/tareas', data)
  })

  it('getTareasConEmpleado hace GET a /tareas-con-empleado', () => {
    api.getTareasConEmpleado()
    expect(apiClient.get).toHaveBeenCalledWith('/tareas-con-empleado')
  })

  it('actualizarTarea hace PUT a /tareas/:id con los datos', () => {
    const data = { estado: 'completada' }
    api.actualizarTarea(7, data)
    expect(apiClient.put).toHaveBeenCalledWith('/tareas/7', data)
  })

  // EMPLEADOS
  it('getEmpleados hace GET a /empleados', () => {
    api.getEmpleados()
    expect(apiClient.get).toHaveBeenCalledWith('/empleados')
  })

  it('createEmpleado hace POST a /empleados con los datos', () => {
    const data = { nombre: 'Juan' }
    api.createEmpleado(data)
    expect(apiClient.post).toHaveBeenCalledWith('/empleados', data)
  })

  it('getEmpleadoPorId hace GET a /empleados/:id', () => {
    api.getEmpleadoPorId(2)
    expect(apiClient.get).toHaveBeenCalledWith('/empleados/2')
  })

  // LOGS
  it('getLogs hace GET a /proyectos/:id/logs', () => {
    api.getLogs(10)
    expect(apiClient.get).toHaveBeenCalledWith('/proyectos/10/logs')
  })

  it('crearLog hace POST a /proyectos/:id/logs con los datos', () => {
    const data = { mensaje: 'Se actualizÃ³ el estado' }
    api.crearLog(10, data)
    expect(apiClient.post).toHaveBeenCalledWith('/proyectos/10/logs', data)
  })

  // ASIGNACIONES
  it('createAsignacion hace POST a /asignaciones con los datos', () => {
    const data = { empleadoId: 1, proyectoId: 2 }
    api.createAsignacion(data)
    expect(apiClient.post).toHaveBeenCalledWith('/asignaciones', data)
  })

  it('getAsignacionesPorProyecto hace GET a /asignaciones/proyecto/:id', () => {
    api.getAsignacionesPorProyecto(4)
    expect(apiClient.get).toHaveBeenCalledWith('/asignaciones/proyecto/4')
  })

  it('deleteAsignacion hace DELETE a /asignaciones/:id', () => {
    api.deleteAsignacion(9)
    expect(apiClient.delete).toHaveBeenCalledWith('/asignaciones/9')
  })

  // KPIs Y REPORTES
  it('getKpis hace GET a /kpis', () => {
    api.getKpis()
    expect(apiClient.get).toHaveBeenCalledWith('/kpis')
  })

  it('getEmpleadosPorDepartamento hace GET al endpoint correcto', () => {
    api.getEmpleadosPorDepartamento()
    expect(apiClient.get).toHaveBeenCalledWith('/reportes/empleados-por-departamento')
  })

  it('getProyectosPorEstado hace GET al endpoint correcto', () => {
    api.getProyectosPorEstado()
    expect(apiClient.get).toHaveBeenCalledWith('/reportes/proyectos-por-estado')
  })

  it('getProyectosPorCategoria hace GET al endpoint correcto', () => {
    api.getProyectosPorCategoria()
    expect(apiClient.get).toHaveBeenCalledWith('/reportes/proyectos-por-categoria')
  })
})