# 🚀 Innovatech Solutions

Sistema de gestión empresarial basado en microservicios, construido con Spring Boot, Vue.js, Kafka y múltiples bases de datos.

---

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Instalación](#instalación)
  - [1. Clonar los repositorios](#1-clonar-los-repositorios)
  - [2. Configurar la librería de componentes](#2-configurar-la-librería-de-componentes)
  - [3. Levantar la infraestructura](#3-levantar-la-infraestructura)
  - [4. Configurar las bases de datos](#4-configurar-las-bases-de-datos)
  - [5. Inicializar los backends](#5-inicializar-los-backends)
  - [6. Ejecutar el BFF](#6-ejecutar-el-bff)
  - [7. Ejecutar el frontend](#7-ejecutar-el-frontend)

---

## Arquitectura

El proyecto está compuesto por los siguientes módulos:

| Módulo | Tecnología | Descripción |
|---|---|---|
| `backend-analitico` | Spring Boot + PostgreSQL | Gestión de empleados, cargos y asignaciones |
| `backend-gestion` | Spring Boot + MySQL + Kafka | Gestión de proyectos, tareas y categorías |
| `backend-notificaciones` | Spring Boot + Kafka | Servicio de notificaciones y auditoría |
| `backend-reportes` | Spring Boot | Generación de reportes consolidados |
| `bff-web` | Node.js + Express | Backend for Frontend (agregador de APIs) |
| `frontend_One` | Vue.js + Vite | Interfaz de usuario principal |

---

## Requisitos previos

Asegúrate de tener instalado lo siguiente antes de continuar:

- [Java 17+](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Maven](https://maven.apache.org/) (o usar el wrapper `./mvnw` incluido)
- Un cliente de base de datos, como [DBeaver](https://dbeaver.io/) o [pgAdmin](https://www.pgadmin.org/)

---

## Instalación

### 1. Clonar los repositorios

Clona el proyecto principal y la librería de componentes en carpetas separadas:

```bash
# Proyecto principal
git clone https://github.com/Gonzaloyu/Innovatech-Solutions.git

# Librería de componentes UI
git clone https://github.com/Gonzaloyu/front.git
```

---

### 2. Configurar la librería de componentes

Navega a la carpeta de la librería, instala las dependencias, compílala y enlázala globalmente con npm:

```bash
cd libreria_mia
npm install
npm run build
npm link
```

> Esto expone la librería de forma local para que el frontend pueda consumirla sin necesidad de publicarla en npm.

---

### 3. Levantar la infraestructura

Desde la raíz del proyecto `Innovatech-Solutions`, ejecuta Docker Compose para levantar MySQL, PostgreSQL y Kafka:

```bash
docker compose up -d
```

Esto levantará los siguientes servicios:

| Servicio | Puerto local |
|---|---|
| MySQL | `3307` |
| PostgreSQL | `5435` |
| Kafka | `9092` |

> Espera unos segundos a que los contenedores pasen sus health checks antes de continuar.

---

### 4. Configurar las bases de datos

#### 4.1 Inicialización automática (backend-gestion)

Levanta  todos los microservicios. Al arrancar, Spring Boot ejecutará el archivo `data.sql` automáticamente y poblará la base de datos de cada microservisos y esto tambien sirve para verificar si estan correcto los puertos en el application.properties ya que si no prenden esto puede ser el error.

problemas que puede ser:

spring.datasource.url=jdbc:postgresql://localhost:5435/innovatech_resources <-- aqui esto puede ser el ya que esta el puerto 5432>
spring.datasource.username=postgres 
spring.datasource.password=root <--- aqui puede estar otra contraseña OJO>

Una vez que veas en los logs que la aplicación arrancó correctamente, **detenla**.

#### 4.2 Datos iniciales en PostgreSQL

Conéctate a la base de datos `innovatech_resources` en PostgreSQL (puerto `5435`, usuario `postgres`, contraseña `root`) y ejecuta el siguiente script SQL:

```sql
INSERT INTO departamentos (nombre) VALUES ('TI'), ('Ventas'), ('Consultoría');

INSERT INTO cargos (nombre, nivel) VALUES 
  ('Desarrollador', 'Junior'),
  ('Desarrollador', 'Senior'),
  ('Arquitecto', 'Senior');
```

Deberías ver el siguiente mensaje de confirmación:

```
INSERT 0 3
Query returned successfully in 85 msec.
```

---

### 5. Inicializar los backends

Con la infraestructura y las bases de datos listas, levanta todos los backends en el siguiente orden recomendado:

1. `backend-analitico`
2. `backend-gestion`
3. `backend-notificaciones`
4. `backend-reportes`

Cada uno puede levantarse con Maven desde su carpeta raíz:

```bash
./mvnw spring-boot:run
```

---

### 6. Ejecutar el BFF

Navega a la carpeta `bff-web` e instala las dependencias:

```bash
cd bff-web
npm init -y
npm install express axios cors opossum
npm install --save-dev nodemon
npm run dev
```

---

### 7. Ejecutar el frontend

Navega a la carpeta `frontend_One` e instala las dependencias:

```bash
cd frontend_One
npm install
npm install axios
npm install @auth0/auth0-vue
```

Enlaza la librería de componentes. El path varía según el equipo donde estés trabajando:

```bash
# Ejemplo en Windows — ajusta la ruta a donde clonaste la librería
npm install C:\Users\TuUsuario\Desktop\front-main\libreria_mia
```

Finalmente, levanta el servidor de desarrollo:

```bash
npm run dev

# Si hay conflictos de caché, forza la ejecución con:
npm run dev -- --force
```

---

## Credenciales por defecto

| Servicio | Usuario | Contraseña | Puerto |
|---|---|---|---|
| MySQL | `root` | `root` | `3307` |
| PostgreSQL | `postgres` | `root` | `5435` |

---

## Estructura del proyecto

```
INNOVATECH-SOLUTIONS/
├── backend-analitico/       # Microservicio de recursos humanos
├── backend-gestion/         # Microservicio de proyectos (con Kafka producer)
├── backend-notificaciones/  # Microservicio de notificaciones (Kafka consumer)
├── backend-reportes/        # Microservicio de reportes
├── bff-web/                 # Backend For Frontend
├── frontend_One/            # Aplicación Vue.js
└── docker-compose.yml       # Infraestructura (MySQL, PostgreSQL, Kafka)
```
