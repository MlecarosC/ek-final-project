# Final Project - Eureka

API REST desarrollada con Spring Boot para la gestión de usuarios y departamentos. Este proyecto implementa operaciones CRUD, validaciones, manejo de excepciones y despliegue con Docker.

## 🚀 Tecnologías Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.5.8**
- **Spring Data JPA**
- **MySQL 8.0**
- **Lombok**
- **Bean Validation**
- **Maven**

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (in-memory para tests)
- **MockMvc**

### DevOps
- **Docker & Docker Compose**
- **Multi-stage builds**

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

## 🧪 Tests

El proyecto incluye una suite completa de tests unitarios y de integración con **JUnit 5** y **Mockito**.

### Estructura de Tests

```
src/test/java/com/eureka/project/
├── controllers/
│   └── UserControllerTest.java          # Tests de endpoints REST (9 tests)
├── services/impl/
│   └── UserServiceImplTest.java         # Tests de lógica de negocio (8 tests)
├── repositories/
│   └── UserRepositoryTest.java          # Tests de queries JPQL (6 tests)
└── exceptions/
    └── GlobalExceptionHandlerTest.java  # Tests de manejo de errores (4 tests)
```

### Cobertura de Tests

| Clase | Tests | Descripción |
|-------|-------|-------------|
| UserControllerTest | 9 | Endpoints REST, validaciones, códigos HTTP |
| UserServiceImplTest | 8 | Lógica de negocio, excepciones, transacciones |
| UserRepositoryTest | 6 | Queries JPQL, persistencia, integridad de datos |
| GlobalExceptionHandlerTest | 4 | Manejo global de excepciones |
| **TOTAL** | **27** | **Cobertura completa** |

### Tecnologías de Testing

- **JUnit 5**: Framework de testing
- **Mockito**: Mocks y stubs
- **Spring Boot Test**: Testing de integración
- **H2 Database**: Base de datos en memoria para tests
- **MockMvc**: Testing de controladores REST

### Ejecutar Tests

#### Opción 1: Ejecutar todos los tests

```bash
./mvnw test
```

**Resultado esperado:**
```
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

#### Opción 2: Ejecutar tests específicos

```bash
# Tests de controladores
./mvnw test -Dtest=UserControllerTest

# Tests de servicios
./mvnw test -Dtest=UserServiceImplTest

# Tests de repositorios
./mvnw test -Dtest=UserRepositoryTest

# Tests de exception handler
./mvnw test -Dtest=GlobalExceptionHandlerTest
```

#### Opción 3: Limpiar y ejecutar tests

```bash
./mvnw clean test
```

### Ejecutar Tests con Docker

#### 1. Ejecutar tests dentro del contenedor

```bash
# Construir imagen con tests
docker-compose build api-users

# Ejecutar tests dentro del contenedor
docker-compose run --rm api-users ./mvnw test
```

### Configuración de Tests

Los tests usan una configuración separada con **H2 en memoria**:

**src/test/resources/application-test.properties**
```properties
# Base de datos H2 en modo MySQL
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver

# Hibernate para H2
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# NO ejecutar scripts SQL de producción
spring.sql.init.mode=never
```

### Ejemplos de Tests

#### Test de Controlador (MockMvc)

```java
@Test
void save_ReturnsCreated() throws Exception {
    when(userService.save(any())).thenReturn(userRequestDTO);

    mockMvc.perform(post("/api/v1/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRequestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Juan Pérez"));
}
```

#### Test de Servicio (Mockito)

```java
@Test
void save_ThrowsUniqueEmailException() {
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    UniqueEmailException exception = assertThrows(
        UniqueEmailException.class,
        () -> userService.save(userRequestDTO)
    );

    assertEquals("Email existente", exception.getMessage());
}
```

#### Test de Repositorio (Integration Test)

```java
@Test
void getUsersByCategories_GroupsCorrectly() {
    List<UsersByCategoriesDTO> result = 
        userRepository.getUsersByCategories();

    assertEquals(2, result.size());
    assertEquals("Ventas", result.get(0).getDepartmentName());
    assertEquals(3L, result.get(0).getUserCount());
}
```

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

## 🧪 Pruebas con Postman/cURL

### Colección de Ejemplos

#### 1. Listar usuarios por departamento
```bash
GET http://localhost:8085/api/v1/users/by-categories
```

#### 2. Crear usuario exitoso
```bash
POST http://localhost:8085/api/v1/users/create
Content-Type: application/json

{
  "name": "Test Usuario",
  "email": "test@example.com",
  "departmentId": 1
}
```

#### 3. Error: Email duplicado
```bash
POST http://localhost:8085/api/v1/users/create
Content-Type: application/json

{
  "name": "Otro Usuario",
  "email": "test@example.com",
  "departmentId": 1
}
```

#### 4. Error: Validación
```bash
POST http://localhost:8085/api/v1/users/create
Content-Type: application/json

{
  "name": "",
  "email": "email-invalido",
  "departmentId": 1
}
```

## 👨‍💻 Autor

Martin Lecaros