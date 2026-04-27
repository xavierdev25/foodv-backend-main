# FoodV Backend

Backend del sistema de delivery universitario FoodV. Desarrollado con Spring Boot 4.x bajo Arquitectura Hexagonal (Ports & Adapters) para la plataforma que conecta estudiantes con comercios dentro del campus de la Universidad César Vallejo.

[![CI](https://github.com/TU_USUARIO/foodv-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/TU_USUARIO/foodv-backend/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-green)
![Tests](https://img.shields.io/badge/tests-30%20passing-brightgreen)

## Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework base |
| PostgreSQL | 16 | Base de datos principal |
| Redis | 7 | Caché y blacklist de tokens |
| Flyway | 11.14.1 | Migraciones de BD |
| Spring Security + JWT | 7.0.5 / 0.13.0 | Autenticación y autorización |
| MercadoPago SDK | 2.9.2 | Pagos en línea |
| Cloudinary SDK | 2.3.2 | Almacenamiento de imágenes |
| Firebase Admin SDK | 9.4.2 | Push notifications |
| Micrometer + Prometheus | — | Métricas de negocio |
| Bucket4j | 8.10.1 | Rate limiting |
| SpringDoc OpenAPI | 2.8.8 | Documentación interactiva |
| MapStruct | 1.6.3 | Mapeo de objetos |
| Lombok | 1.18.46 | Reducción de boilerplate |
| Docker + Docker Compose | — | Contenedores |

## Arquitectura

```
src/main/java/com/foodv/backend/
├── domain/                  # Núcleo — sin dependencias de framework
│   ├── model/               # Entidades de dominio y enums
│   ├── port/
│   │   ├── in/              # Casos de uso (puertos de entrada)
│   │   └── out/             # Repositorios y servicios externos
│   └── service/             # Lógica de negocio pura
├── application/             # Handlers — implementan los casos de uso
└── infrastructure/          # Adaptadores
    ├── persistence/         # JPA entities, repositories, adapters
    ├── web/                 # Controllers, DTOs, mappers
    ├── payment/             # Adaptador MercadoPago
    ├── notification/        # Adaptador WebSocket
    ├── ai/                  # Adaptador FastAPI AI Service
    ├── storage/             # Adaptador Cloudinary
    ├── metrics/             # BusinessMetricsService
    ├── scheduler/           # Tareas programadas (cleanup)
    ├── security/            # JWT filter, rate limiting, brute force
    └── config/              # Spring Security, CORS, Firebase, WebSocket
```

## Módulos

| Módulo | Descripción |
|---|---|
| `auth` | Registro, login, logout, refresh token con JWT |
| `users` | Gestión de usuarios con preferencias gastronómicas y roles |
| `aulas` | Catálogo de aulas universitarias |
| `stores` | Tiendas de los comercios dentro del campus |
| `products` | Catálogo con búsqueda, filtros y caché Redis |
| `orders` | Máquina de estados: PENDIENTE → PREPARANDO → EN_CAMINO → ENTREGADO |
| `payments` | Integración MercadoPago Checkout Pro con webhook HMAC-SHA256 |
| `ai` | Recomendaciones personalizadas + feedback de usuario |
| `images` | Subida de imágenes a Cloudinary |
| `notifications` | WebSocket STOMP + Firebase FCM (push notifications) |

## Seguridad

- **JWT** — access token (24h) + refresh token (7 días) con blacklist en Redis
- **RBAC** — roles: `ESTUDIANTE`, `REPARTIDOR`, `COMERCIO`, `ADMIN`
- **Rate Limiting diferenciado** — 100 req/min general, 5 req/min para IA
- **Brute Force Protection** — bloqueo 15 min tras 5 intentos fallidos
- **Security Headers** — X-Content-Type-Options, X-Frame-Options, HSTS, CSP
- **Política de contraseñas** — mínimo 8 caracteres, mayúscula, minúscula y número
- **Webhook signature** — verificación HMAC-SHA256 para MercadoPago
- **Soft Delete** — usuarios, productos y tiendas con `deleted_at`

## Motor de IA

El módulo de IA conecta con [foodv-ai-service](https://github.com/TU_USUARIO/foodv-ai-service) para generar recomendaciones personalizadas:

- Lee el perfil del usuario autenticado (preferencias, restricciones, presupuesto)
- Enriquece con historial de órdenes de los últimos 3 meses
- Añade contexto temporal (desayuno / almuerzo / snack según la hora)
- Llama al microservicio y retorna recomendaciones ordenadas por score
- Permite registrar feedback (👍 / 👎) para mejorar futuras recomendaciones

```
GET  /api/ai/recommendations          # Recomendaciones personalizadas
POST /api/ai/recommendations/feedback # Like / dislike de un producto
```

## Migraciones de Base de Datos

| Versión | Descripción |
|---|---|
| V1 | Tabla `users` |
| V2 | Tabla `aulas` |
| V3 | Tabla `stores` |
| V4 | Tabla `products` |
| V5 | Tablas `orders` y `order_items` |
| V6 | Tabla `payments` |
| V7 | Tabla `refresh_tokens` |
| V8 | Tabla `order_status_history` |
| V9 | Columnas `deleted_at` (soft delete) |
| V10 | Columnas de preferencias gastronómicas en `users` |
| V11 | Tabla `ai_recommendation_feedback` |

## Métricas de Negocio

Disponibles en `/actuator/prometheus` para integrar con Grafana:

| Métrica | Descripción |
|---|---|
| `foodv.orders.created` | Total de órdenes creadas |
| `foodv.orders.completed` | Total de órdenes entregadas |
| `foodv.orders.cancelled` | Total de órdenes canceladas |
| `foodv.payments.completed` | Total de pagos exitosos |
| `foodv.payments.failed` | Total de pagos fallidos |
| `foodv.users.registered` | Total de usuarios registrados |
| `foodv.stores.created` | Total de tiendas creadas |
| `foodv.orders.processing.time` | Tiempo de procesamiento de órdenes |

## Requisitos Previos

- Java 21
- Docker Desktop
- Maven (incluido via `./mvnw`)

## Instalación

```bash
git clone https://github.com/TU_USUARIO/foodv-backend.git
cd foodv-backend
cp .env.example .env
# Edita .env con tus credenciales
```

### Variables de entorno (`.env`)

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
AI_SERVICE_SECRET_KEY=change-me-in-production

# Opcional — Firebase FCM
FIREBASE_ENABLED=false
FIREBASE_CREDENTIALS_PATH=
FIREBASE_PROJECT_ID=
```

## Ejecución

```bash
# Levantar PostgreSQL, Redis y pgAdmin
docker-compose up -d

# Arrancar la aplicación (Flyway aplica migraciones automáticamente)
./mvnw spring-boot:run
```

| URL | Descripción |
|---|---|
| `http://localhost:8080/api` | API REST |
| `http://localhost:8080/api/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/api/actuator/health` | Health check |
| `http://localhost:8080/api/actuator/prometheus` | Métricas Prometheus |

## Tests

```bash
./mvnw test
```

**30 tests — 0 fallos:**

| Suite | Tests | Tipo |
|---|---|---|
| `OrderDomainService` | 10 | Unitario — máquina de estados |
| `LoginHandler` | 5 | Unitario — autenticación |
| `CreateOrderHandler` | 4 | Unitario — creación de órdenes |
| `CreateUserHandler` | 3 | Unitario — registro de usuarios |
| `BackendApplicationTests` | 1 | Integración — contexto Spring |
| `ProductRepositoryIntegrationTest` | 4 | Integración — repositorio |
| `UserRepositoryIntegrationTest` | 3 | Integración — repositorio |

## CI/CD

GitHub Actions corre automáticamente en cada push a `main` y `develop`:

1. **Test** — PostgreSQL 16 + Redis 7 como servicios, 30 tests
2. **Build** — JAR artifact subido como artefacto de GitHub

## Proyecto Relacionado

[foodv-ai-service](https://github.com/xavierdev25/foodv-ai-service) — Microservicio de recomendaciones con FastAPI + Ollama + Groq