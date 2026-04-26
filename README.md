# FoodV Backend

Backend del sistema de delivery universitario FoodV, desarrollado con Spring Boot 4.x bajo Arquitectura Hexagonal (Ports & Adapters).

## Stack Tecnológico

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 16 |
| Redis | 7 |
| Flyway | 11.14.1 |
| Spring Security + JWT | 7.0.5 / 0.13.0 |
| MercadoPago SDK | 2.9.2 |
| Cloudinary SDK | 2.3.2 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.46 |
| Bucket4j | 8.10.1 |
| SpringDoc OpenAPI | 2.8.8 |
| Docker + Docker Compose | — |

## Arquitectura

```
src/main/java/com/foodv/backend/
├── domain/                  # Capa de dominio (sin dependencias de framework)
│   ├── model/               # Entidades y Value Objects
│   ├── port/
│   │   ├── in/              # Casos de uso (puertos de entrada)
│   │   └── out/             # Repositorios y servicios externos (puertos de salida)
│   └── service/             # Lógica de negocio pura
├── application/             # Handlers — implementan los casos de uso
└── infrastructure/          # Adaptadores (BD, web, pagos, IA, notificaciones)
    ├── persistence/         # JPA entities, repositories, adapters
    ├── web/                 # Controllers, DTOs, mappers
    ├── payment/             # MercadoPago adapter
    ├── notification/        # WebSocket adapter
    ├── ai/                  # FastAPI AI service adapter
    ├── storage/             # Cloudinary adapter
    └── config/              # Spring Security, JWT, CORS, WebSocket, Flyway
```

## Módulos

| Módulo | Descripción |
|---|---|
| `users` | Gestión de usuarios con roles ESTUDIANTE, REPARTIDOR, COMERCIO, ADMIN |
| `auth` | Autenticación JWT — register, login, logout, refresh token |
| `aulas` | Catálogo de aulas de la universidad |
| `stores` | Tiendas de los comercios dentro del campus |
| `products` | Catálogo de productos por tienda con búsqueda y filtros |
| `orders` | Órdenes con máquina de estados: PENDIENTE → PREPARANDO → EN_CAMINO → ENTREGADO / CANCELADO |
| `payment` | Integración con MercadoPago Checkout Pro |
| `notification` | Notificaciones en tiempo real via WebSocket (STOMP) |
| `ai` | Integración con microservicio de recomendaciones (FastAPI + phi3) |
| `images` | Subida de imágenes a Cloudinary |

## Seguridad

- **JWT** con access token (24h) y refresh token (7 días) almacenado en BD
- **RBAC** por endpoint — roles: ESTUDIANTE, REPARTIDOR, COMERCIO, ADMIN
- **Rate Limiting** — 100 peticiones/minuto por usuario/IP con Bucket4j
- **Brute Force Protection** — bloqueo de 15 minutos tras 5 intentos fallidos
- **Headers de seguridad** — X-Content-Type-Options, X-Frame-Options, HSTS, CSP
- **Política de contraseñas** — mínimo 8 caracteres, mayúscula, minúscula y número
- **Firma de webhook** — verificación HMAC-SHA256 para MercadoPago

## Requisitos Previos

- Java 21
- Docker Desktop
- Maven (incluido via `./mvnw`)

## Configuración

```bash
# 1. Clonar el repositorio
git clone https://github.com/TU_USUARIO/foodv-backend.git
cd foodv-backend

# 2. Copiar el archivo de variables de entorno
cp .env.example .env

# 3. Editar .env con tus credenciales
nano .env
```

### Variables de entorno requeridas (`.env`)

```env
SERVER_PORT=8080

DB_HOST=localhost
DB_PORT=5432
DB_NAME=foodv_db
DB_USERNAME=foodv_user
DB_PASSWORD=foodv_secret

REDIS_HOST=localhost
REDIS_PORT=6379

JWT_SECRET=tu-secret-key-de-minimo-32-caracteres
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
JWT_ISSUER=foodv-backend

MERCADOPAGO_ACCESS_TOKEN=TEST-xxxxxxxxxxxx
MERCADOPAGO_PUBLIC_KEY=TEST-xxxxxxxxxxxx
MERCADOPAGO_WEBHOOK_SECRET=xxxxxxxxxxxx
MERCADOPAGO_NOTIFICATION_URL=https://tu-dominio.com/api/payments/webhook

CLOUDINARY_CLOUD_NAME=tu-cloud-name
CLOUDINARY_API_KEY=tu-api-key
CLOUDINARY_API_SECRET=tu-api-secret

AI_SERVICE_URL=http://localhost:8001
```

## Ejecución

```bash
# 1. Levantar PostgreSQL, pgAdmin y Redis con Docker
docker-compose up -d

# 2. Arrancar la aplicación (Flyway ejecuta las migraciones automáticamente)
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080/api`.
Swagger UI: `http://localhost:8080/api/swagger-ui.html`
Health check: `http://localhost:8080/api/actuator/health`

## Endpoints

### Autenticación (`/auth`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/auth/register` | — | Registrar usuario |
| POST | `/auth/login` | — | Iniciar sesión |
| POST | `/auth/logout` | — | Cerrar sesión |
| POST | `/auth/refresh` | — | Renovar access token |

### Usuarios (`/users`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/users` | ADMIN | Listar usuarios paginado |
| POST | `/users` | ADMIN | Crear usuario |
| GET | `/users/me` | Autenticado | Mi perfil |
| PUT | `/users/me/password` | Autenticado | Cambiar contraseña |
| GET | `/users/{id}` | ADMIN | Obtener usuario |
| PUT | `/users/{id}` | ADMIN | Actualizar usuario |
| DELETE | `/users/{id}` | ADMIN | Eliminar usuario |

### Aulas (`/aulas`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/aulas` | Autenticado | Listar aulas |
| GET | `/aulas/activas` | Autenticado | Listar aulas activas |
| POST | `/aulas` | ADMIN | Crear aula |
| GET | `/aulas/{id}` | Autenticado | Obtener aula |
| PUT | `/aulas/{id}` | ADMIN | Actualizar aula |
| DELETE | `/aulas/{id}` | ADMIN | Eliminar aula |

### Tiendas (`/stores`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/stores` | Autenticado | Listar tiendas paginado |
| GET | `/stores/activas` | Autenticado | Listar tiendas activas |
| POST | `/stores` | ADMIN/COMERCIO | Crear tienda |
| GET | `/stores/{id}` | Autenticado | Obtener tienda |
| GET | `/stores/owner/{ownerId}` | Autenticado | Tienda por dueño |
| PUT | `/stores/{id}` | ADMIN/COMERCIO | Actualizar tienda |
| DELETE | `/stores/{id}` | ADMIN | Eliminar tienda |

### Productos (`/products`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/products` | Autenticado | Listar paginado |
| GET | `/products/search` | Autenticado | Buscar con filtros |
| POST | `/products` | ADMIN/COMERCIO | Crear producto |
| GET | `/products/{id}` | Autenticado | Obtener producto |
| GET | `/products/store/{storeId}` | Autenticado | Productos por tienda |
| GET | `/products/store/{storeId}/activos` | Autenticado | Productos activos |
| GET | `/products/categoria/{categoria}` | Autenticado | Por categoría |
| PUT | `/products/{id}` | ADMIN/COMERCIO | Actualizar producto |
| DELETE | `/products/{id}` | ADMIN/COMERCIO | Eliminar producto |

#### Filtros de búsqueda (`/products/search`)
| Parámetro | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Búsqueda parcial por nombre |
| `categoria` | Enum | COMIDA, BEBIDA, SNACK, POSTRE, OTRO |
| `storeId` | Long | Filtrar por tienda |
| `precioMin` | Decimal | Precio mínimo |
| `precioMax` | Decimal | Precio máximo |
| `disponible` | Boolean | Solo disponibles |
| `page` | Int | Página (default 0) |
| `size` | Int | Tamaño (default 20) |
| `sortBy` | String | Campo de ordenamiento (default nombre) |

### Órdenes (`/orders`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/orders` | Autenticado | Listar paginado |
| POST | `/orders` | ESTUDIANTE/ADMIN | Crear orden |
| GET | `/orders/{id}` | Autenticado | Obtener orden |
| GET | `/orders/user/{userId}` | Autenticado | Órdenes por usuario |
| GET | `/orders/store/{storeId}` | Autenticado | Órdenes por tienda |
| GET | `/orders/status/{status}` | Autenticado | Por estado |
| PATCH | `/orders/{id}/status` | COMERCIO/REPARTIDOR/ADMIN | Cambiar estado |
| PATCH | `/orders/{id}/cancel` | Autenticado | Cancelar orden |

#### Estados de orden
```
PENDIENTE → PREPARANDO → EN_CAMINO → ENTREGADO
PENDIENTE → CANCELADO
PREPARANDO → CANCELADO
```

### Pagos (`/payments`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/payments` | Autenticado | Crear pago MercadoPago |
| GET | `/payments/{id}` | Autenticado | Obtener pago |
| GET | `/payments/order/{orderId}` | Autenticado | Pago por orden |
| GET | `/payments/user/{userId}` | Autenticado | Pagos por usuario |
| POST | `/payments/webhook` | — | Webhook MercadoPago |

### Imágenes (`/images`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/images/products/{productId}` | ADMIN/COMERCIO | Subir imagen de producto |
| POST | `/images/stores/{storeId}` | ADMIN/COMERCIO | Subir imagen de tienda |
| DELETE | `/images` | ADMIN/COMERCIO | Eliminar imagen |

### IA (`/ai`)
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/ai/recommendations` | Autenticado | Recomendaciones personalizadas |

### Utilidades
| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | `/actuator/health` | — | Health check |
| GET | `/actuator/info` | — | Info de la app |
| GET | `/actuator/metrics` | — | Métricas |
| GET | `/swagger-ui.html` | — | Documentación interactiva |

## WebSocket

Endpoint: `ws://localhost:8080/api/ws`

| Topic | Descripción |
|---|---|
| `/topic/user/{userId}` | Notificaciones para el estudiante |
| `/topic/store/{storeId}` | Notificaciones para el comercio |
| `/topic/order/{orderId}` | Actualizaciones de una orden |

## Migraciones de Base de Datos

| Versión | Descripción |
|---|---|
| V1 | Tabla users |
| V2 | Tabla aulas |
| V3 | Tabla stores |
| V4 | Tabla products |
| V5 | Tablas orders y order_items |
| V6 | Tabla payments |
| V7 | Tabla refresh_tokens |

## Tests

```bash
./mvnw test
```

- 10 tests de dominio — `OrderDomainService` máquina de estados
- 5 tests de `LoginHandler` con Mockito
- 4 tests de `CreateOrderHandler` con Mockito
- 3 tests de `CreateUserHandler` con Mockito
- 1 test de integración — `BackendApplicationTests`

## Patrones de Diseño Aplicados

- **Hexagonal Architecture** — dominio aislado del framework
- **Builder** — construcción de entidades complejas con Lombok
- **Adapter** — MercadoPago, Cloudinary, FastAPI AI como adaptadores
- **State** — máquina de estados de órdenes
- **Strategy** — evaluación de recomendaciones por restricciones dietéticas
- **Observer** — notificaciones WebSocket al cambiar estado de orden

## Proyecto Relacionado

- [foodv-ai-service](https://github.com/xavierdev25/foodv-ai-service) — Microservicio de recomendaciones con FastAPI + phi3