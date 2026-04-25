# FoodV Backend

Backend del sistema de delivery universitario FoodV, desarrollado con Spring Boot 4.x bajo Arquitectura Hexagonal (Ports & Adapters).

## Stack Tecnológico

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 16 |
| Flyway | 11.14.1 |
| Spring Security + JWT | 7.0.5 / 0.13.0 |
| MercadoPago SDK | 2.9.2 |
| Cloudinary SDK | 2.3.2 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.46 |
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
    └── config/              # Spring Security, JWT, CORS, WebSocket, Flyway
```

## Módulos

| Módulo | Descripción |
|---|---|
| `users` | Gestión de usuarios con roles ESTUDIANTE, REPARTIDOR, COMERCIO, ADMIN |
| `auth` | Autenticación JWT — register, login, refresh token |
| `aulas` | Catálogo de aulas de la universidad |
| `stores` | Tiendas de los comercios dentro del campus |
| `products` | Catálogo de productos por tienda con categorías |
| `orders` | Órdenes con máquina de estados: PENDIENTE → PREPARANDO → EN_CAMINO → ENTREGADO / CANCELADO |
| `payment` | Integración con MercadoPago Checkout Pro |
| `notification` | Notificaciones en tiempo real via WebSocket (STOMP) |
| `ai` | Integración con microservicio de recomendaciones (FastAPI + phi3) |

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
# Servidor
SERVER_PORT=8080

# PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=foodv_db
DB_USERNAME=foodv_user
DB_PASSWORD=foodv_secret

# JWT
JWT_SECRET=tu-secret-key-de-minimo-32-caracteres
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
JWT_ISSUER=foodv-backend

# MercadoPago
MERCADOPAGO_ACCESS_TOKEN=TEST-xxxxxxxxxxxx
MERCADOPAGO_PUBLIC_KEY=TEST-xxxxxxxxxxxx
MERCADOPAGO_WEBHOOK_SECRET=xxxxxxxxxxxx
MERCADOPAGO_NOTIFICATION_URL=https://tu-dominio.com/api/payments/webhook

# Cloudinary
CLOUDINARY_CLOUD_NAME=tu-cloud-name
CLOUDINARY_API_KEY=tu-api-key
CLOUDINARY_API_SECRET=tu-api-secret

# AI Service
AI_SERVICE_URL=http://localhost:8001
```

## Ejecución

```bash
# 1. Levantar PostgreSQL y pgAdmin con Docker
docker-compose up -d

# 2. Arrancar la aplicación (Flyway ejecuta las migraciones automáticamente)
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080/api`.

## Endpoints Principales

### Autenticación
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/refresh` | Renovar token |

### Recursos
| Método | Endpoint | Descripción |
|---|---|---|
| GET/POST | `/api/users` | Gestión de usuarios |
| GET/POST | `/api/aulas` | Gestión de aulas |
| GET/POST | `/api/stores` | Gestión de tiendas |
| GET/POST | `/api/products` | Gestión de productos |
| GET/POST | `/api/orders` | Gestión de órdenes |
| PATCH | `/api/orders/{id}/status` | Cambiar estado de orden |
| POST | `/api/payments` | Crear pago con MercadoPago |
| POST | `/api/payments/webhook` | Webhook de MercadoPago |
| POST | `/api/ai/recommendations` | Recomendaciones con IA |

## WebSocket

Endpoint: `ws://localhost:8080/api/ws`

| Topic | Descripción |
|---|---|
| `/topic/user/{userId}` | Notificaciones para el estudiante |
| `/topic/store/{storeId}` | Notificaciones para el comercio |
| `/topic/order/{orderId}` | Actualizaciones de una orden |

## Migraciones de Base de Datos

Flyway ejecuta las migraciones automáticamente al arrancar. Los archivos se encuentran en `src/main/resources/db/migration/`:

| Versión | Archivo | Descripción |
|---|---|---|
| V1 | `V1__create_users_table.sql` | Tabla de usuarios |
| V2 | `V2__create_aulas_table.sql` | Tabla de aulas |
| V3 | `V3__create_stores_table.sql` | Tabla de tiendas |
| V4 | `V4__create_products_table.sql` | Tabla de productos |
| V5 | `V5__create_orders_tables.sql` | Tablas de órdenes e items |
| V6 | `V6__create_payments_table.sql` | Tabla de pagos |

## Patrones de Diseño Aplicados

- **Hexagonal Architecture** — dominio aislado del framework
- **Builder** — construcción de entidades complejas con Lombok
- **Adapter** — MercadoPago, Cloudinary, FastAPI AI como adaptadores
- **State** — máquina de estados de órdenes
- **Strategy** — evaluación de recomendaciones por restricciones dietéticas
- **Observer** — notificaciones WebSocket al cambiar estado de orden
- **Factory** — creación de eventos de notificación

## Principios SOLID Aplicados

- **S** — cada handler tiene una sola responsabilidad
- **O** — nuevos adaptadores sin modificar el dominio
- **L** — los puertos son intercambiables
- **I** — interfaces pequeñas y específicas por caso de uso
- **D** — el dominio depende de abstracciones, nunca de implementaciones

## Proyecto Relacionado

- [foodv-ai-service](https://github.com/xavierdev25/foodv-ai-service) — Microservicio de recomendaciones con FastAPI + phi3