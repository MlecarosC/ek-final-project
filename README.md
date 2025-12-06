# Final Project - Eureka

API REST desarrollada con Spring Boot para la gestión de usuarios y departamentos. Este proyecto implementa operaciones, validaciones, manejo de excepciones y despliegue con Docker.

## 🚀 Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.8**
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Lombok**
- **Bean Validation**
- **Maven**

## 📋 Requisitos Previos

- Docker Desktop instalado
- Docker Compose
- Puerto 8085 disponible (API)
- Puerto 3307 disponible (MySQL)

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/eureka/project/
├── configs/              # Configuraciones de beans
├── controllers/          # Controladores REST
├── dto/                  # Data Transfer Objects
├── exceptions/           # Manejo de excepciones
├── models/              # Entidades JPA
├── repositories/        # Repositorios Spring Data
└── services/            # Lógica de negocio
    └── impl/
```

## 🗄️ Modelo de Datos

### Tabla `departments`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INT | Primary Key (auto-increment) |
| name | VARCHAR(100) | Nombre del departamento |

### Tabla `users`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INT | Primary Key (auto-increment) |
| name | VARCHAR(100) | Nombre del usuario |
| email | VARCHAR(150) | Email único del usuario |
| department_id | INT | Foreign Key a departments |

### Relaciones
- Un departamento puede tener muchos usuarios (One-to-Many)
- Un usuario pertenece a un departamento (Many-to-One)

## 🔧 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd final-project
```

### 2. Levantar los servicios con Docker

```bash
docker-compose up --build
```

Esto creará y ejecutará:
- Contenedor MySQL en puerto `3307`
- Contenedor Spring Boot API en puerto `8085`

### 3. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver:
```
eureka-final-mysql        Running
eureka-final-api-users    Running
```

## 📡 Endpoints de la API

### Base URL
```
http://localhost:8085/api/v1/users
```

### 1. Obtener usuarios por categorías (departamentos)

**GET** `/by-categories`

Retorna la cantidad de usuarios agrupados por departamento.

**Ejemplo de Request:**
```bash
curl http://localhost:8085/api/v1/users/by-categories
```

**Ejemplo de Response (200 OK):**
```json
[
  {
    "departmentId": 1,
    "departmentName": "Ventas",
    "userCount": 17
  },
  {
    "departmentId": 2,
    "departmentName": "Recursos Humanos",
    "userCount": 25
  },
  {
    "departmentId": 3,
    "departmentName": "Contabilidad",
    "userCount": 20
  }
]
```

### 2. Crear usuario

**POST** `/create`

Crea un nuevo usuario en el sistema.

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "departmentId": 1
}
```

**Validaciones:**
- `name`: Obligatorio, máximo 50 caracteres
- `email`: Obligatorio, formato email válido, máximo 150 caracteres, único
- `departmentId`: Obligatorio, debe existir en la BD

**Ejemplo de Request:**
```bash
curl -X POST http://localhost:8085/api/v1/users/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "departmentId": 1
  }'
```

**Ejemplo de Response (201 Created):**
```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "departmentId": 1
}
```

## ⚠️ Manejo de Errores

### Error de Validación (400 Bad Request)
```json
{
  "timestamp": "2024-12-05",
  "code": 400,
  "message": "Validation failed",
  "validationErrors": {
    "email": "El correo electrónico debe ser válido",
    "name": "El nombre es obligatorio"
  }
}
```

### Email Duplicado (409 Conflict)
```json
{
  "timestamp": "2024-12-05",
  "code": 409,
  "message": "Email existente"
}
```

### Departamento No Encontrado (404 Not Found)
```json
{
  "timestamp": "2024-12-05",
  "code": 404,
  "message": "Departamento no encontrado con ID: 99"
}
```

## 🔍 Características Técnicas Implementadas

### Validaciones
- **Bean Validation** con anotaciones `@Valid`
- Validación de email único a nivel de servicio
- Validación de existencia de departamento

### Manejo de Excepciones
- **Global Exception Handler** con `@RestControllerAdvice`
- Excepciones personalizadas:
  - `DataException`: Errores de base de datos
  - `UniqueEmailException`: Email duplicado
- Respuestas de error estandarizadas

## 🐳 Configuración Docker

### docker-compose.yml

El proyecto usa dos servicios:

1. **MySQL Database**
   - Puerto: `3307:3306`
   - Base de datos: `eureka-project-db`
   - Usuario: `eureka_user`
   - Password: `eureka_pass`
   - Healthcheck configurado

2. **Spring Boot API**
   - Puerto: `8085:8085`
   - Depende de MySQL
   - Variables de entorno configuradas
   - Multi-stage build para optimizar imagen

### Comandos Docker Útiles

```bash
# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (resetear BD)
docker-compose down -v

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f api-users

# Reconstruir imágenes
docker-compose up --build

# Acceder al contenedor MySQL
docker exec -it eureka-final-mysql mysql -u eureka_user -peureka_pass eureka-project-db
```

## 📊 Datos de Prueba

El proyecto incluye datos de prueba precargados:

- **3 Departamentos**: Ventas, Recursos Humanos, Contabilidad
- **62 Usuarios**:
  - 17 en Ventas
  - 25 en Recursos Humanos
  - 20 en Contabilidad

## 👨‍💻 Autor
Martin Lecaros

Proyecto desarrollado para Eureka
