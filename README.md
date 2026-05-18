# FoodV Backend

Backend principal de FoodV, marketplace de delivery universitario para la Universidad César Vallejo, sede Lima Norte. Está desarrollado con Spring Boot 4.x y Java 21 bajo Arquitectura Hexagonal (Ports & Adapters), integrando autenticación JWT, PostgreSQL, Redis, MercadoPago, Cloudinary, Firebase FCM, WebSocket y el microservicio `foodv-ai-service`.

[![CI](https://github.com/xavier25dev/foodv-backend-main/actions/workflows/ci.yml/badge.svg)](https://github.com/xavier25dev/foodv-backend-main/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-green)
![Tests](https://img.shields.io/badge/tests-32%20passing-brightgreen)

## Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---:|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework base |
| PostgreSQL | 16 | Base de datos principal |
| Redis | 7 | Caché, rate limiting y blacklist de tokens |
| Flyway | Gestionado por Spring Boot | Migraciones de base de datos |
| Spring Security + JWT | Spring Security 7.x / JJWT 0.13.0 | Autenticación y autorización |
| MercadoPago SDK | 2.9.2 | Pagos en línea |
| Cloudinary SDK | 2.3.2 | Almacenamiento de imágenes |
| Firebase Admin SDK | 9.4.2 | Push notifications |
| Micrometer + Prometheus | Gestionado por Spring Boot | Métricas de negocio |
| Bucket4j | 8.10.1 | Rate limiting |
| SpringDoc OpenAPI | 2.8.8 | Documentación interactiva |
| MapStruct | 1.6.3 | Mapeo de objetos |
| Lombok | Gestionado por Spring Boot | Reducción de boilerplate |
| Docker + Docker Compose | 3.9 | Infraestructura local |

## Arquitectura

```text
src/main/java/com/foodv/backend/
├── domain/                  # Núcleo sin dependencias de framework
│   ├── model/               # Entidades de dominio y enums
│   ├── port/
│   │   ├── in/              # Casos de uso
│   │   └── out/             # Repositorios y servicios externos
│   └── service/             # Lógica de negocio pura
├── application/             # Handlers que implementan casos de uso
└── infrastructure/          # Adaptadores
    ├── ai/                  # Cliente del microservicio FastAPI
    ├── common/              # Utilidades de infraestructura
    ├── config/              # Security, CORS, Firebase, WebSocket
    ├── metrics/             # Métricas de negocio
    ├── notification/        # WebSocket/STOMP y FCM
    ├── payment/             # MercadoPago
    ├── persistence/         # JPA entities, repositories, adapters
    ├── scheduler/           # Tareas programadas
    ├── security/            # JWT, rate limit, ownership, blacklist
    ├── storage/             # Cloudinary
    └── web/                 # Controllers, DTOs y mappers
```

## Módulos

| Módulo | Descripción |
|---|---|
| `auth` | Registro, login, logout y refresh token con JWT |
| `users` | Gestión de usuarios, roles y preferencias gastronómicas |
| `aulas` | Catálogo de aulas universitarias |
| `universidades` | Datos institucionales para segmentar campus |
| `campus` | Campus con ubicación GeoJSON/JSONB y metadatos |
| `stores` | Tiendas de comercios dentro del campus |
| `products` | Catálogo con búsqueda, filtros y caché Redis |
| `favorites` | Productos y tiendas favoritas por usuario |
| `orders` | Pedidos y máquina de estados |
| `payments` | MercadoPago Checkout Pro y webhook HMAC-SHA256 |
| `ratings` | Calificaciones de pedidos, tiendas y repartidores |
| `repartidor` | Perfil, KYC, verificación y asignación de entregas |
| `ai` | Recomendaciones personalizadas y feedback |
| `images` | Subida de imágenes a Cloudinary |
| `notifications` | WebSocket STOMP y Firebase FCM |

## Seguridad

- **JWT**: access token y refresh token con blacklist en Redis.
- **RBAC**: roles `ESTUDIANTE`, `REPARTIDOR`, `COMERCIO`, `ADMIN`.
- **Rate limiting distribuido**: Redis por IP o token.
- **Límites por tipo**: 100 req/min general, 30 req/min auth, 60 req/min pagos, 5 req/min IA.
- **Brute force protection**: bloqueo temporal después de intentos fallidos.
- **Security headers**: cabeceras defensivas vía filtro dedicado.
- **Ownership checks**: validación de acceso por usuario, tienda, orden o rol.
- **Webhook signature**: verificación HMAC-SHA256 para MercadoPago.
- **Soft delete**: usuarios, productos y tiendas con `deleted_at`.

## Motor de IA

El módulo de IA conecta con `foodv-ai-service` para generar recomendaciones:

- Lee preferencias, restricciones y presupuesto del usuario autenticado.
- Enriquece el contexto con historial de órdenes entregadas.
- Envía productos disponibles al microservicio FastAPI.
- Recibe recomendaciones ordenadas por score.
- Permite registrar feedback para mejorar futuras recomendaciones.

```http
GET  /api/ai/recommendations
POST /api/ai/recommendations/feedback
```

Variables relacionadas:

```env
AI_SERVICE_URL=http://localhost:8001
AI_SERVICE_SECRET_KEY=your-ai-service-secret-key
```

`AI_SERVICE_SECRET_KEY` debe coincidir con `API_SECRET_KEY` configurado en `foodv-ai-service`.

## Máquina de Estados de Órdenes

```text
PENDIENTE
  ├── CANCELADO
  └── PREPARANDO
        └── LISTO_PARA_RECOGER
              └── EN_CAMINO
                    └── ENTREGADO
```

Estados terminales: `ENTREGADO`, `CANCELADO`.

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
| V9 | Columnas `deleted_at` para soft delete |
| V10 | Preferencias gastronómicas en `users` |
| V11 | Tabla `ai_recommendation_feedback` |
| V12 | Campos de repartidor en órdenes |
| V13 | Perfil de repartidor y datos KYC |
| V14 | Universidades, campus y ubicación GeoJSON/JSONB |
| V15 | Hardening de seguridad y performance |
| V16 | Productos y tiendas favoritas |
| V17 | Ratings y calificaciones de tienda/repartidor |
| V18 | Horarios de atención de tiendas |

## Métricas de Negocio

Disponibles en perfil de desarrollo en:

```text
http://localhost:8080/api/actuator/prometheus
```

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

## Perfiles Spring

| Perfil | Uso | Infraestructura |
|---|---|---|
| `dev` | Desarrollo local | PostgreSQL, Redis y servicios levantados con Docker Compose; valores placeholder para integraciones externas |
| `prod` | Producción | PostgreSQL/Redis gestionados por infraestructura de despliegue o Docker; Swagger y métricas sensibles restringidas |

El perfil activo por defecto es `dev`:

```yaml
spring.profiles.active: ${SPRING_PROFILES_ACTIVE:dev}
```

Nota: este proyecto no usa H2 como base de datos de desarrollo. El perfil `dev` apunta a PostgreSQL local y Redis local/Docker.

## Requisitos Previos

- Java 21
- Docker Desktop o Docker Engine
- Maven Wrapper incluido (`./mvnw`)
- Ollama opcional si se quiere usar IA local desde `foodv-ai-service`

## Instalación

```bash
git clone https://github.com/xavier25dev/foodv-backend-main.git
cd foodv-backend-main
cp .env.example .env
```

Edita `.env` con tus credenciales reales.

## Variables de Entorno

| Variable | Ejemplo | Descripción |
|---|---|---|
| `SERVER_PORT` | `8080` | Puerto HTTP del backend |
| `DB_HOST` | `localhost` | Host PostgreSQL |
| `DB_PORT` | `5432` | Puerto PostgreSQL |
| `DB_NAME` | `foodv_db` | Base de datos |
| `DB_USERNAME` | `foodv_user` | Usuario PostgreSQL |
| `DB_PASSWORD` | `your-db-password` | Password PostgreSQL |
| `DB_POOL_SIZE` | `10` | Tamaño máximo del pool Hikari |
| `PGADMIN_EMAIL` | `admin@foodv.com` | Email de pgAdmin; Docker lo mapea a `PGADMIN_DEFAULT_EMAIL` |
| `PGADMIN_PASSWORD` | `your-pgadmin-password` | Password de pgAdmin; Docker lo mapea a `PGADMIN_DEFAULT_PASSWORD` |
| `PGADMIN_PORT` | `5050` | Puerto host para pgAdmin |
| `REDIS_HOST` | `localhost` | Host Redis |
| `REDIS_PORT` | `6380` | Puerto Redis en host local; dentro de Docker es `6379` |
| `JWT_SECRET` | `your-256-bit-secret-key-here-min-32-chars` | Secreto JWT |
| `JWT_EXPIRATION` | `86400000` | Duración access token en ms |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Duración refresh token en ms |
| `JWT_ISSUER` | `foodv-backend` | Issuer JWT |
| `CLOUDINARY_CLOUD_NAME` | `your-cloud-name` | Cloudinary cloud |
| `CLOUDINARY_API_KEY` | `your-api-key` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | `your-api-secret` | Cloudinary API secret |
| `MERCADOPAGO_ACCESS_TOKEN` | `TEST-...` | Access token MercadoPago |
| `MERCADOPAGO_PUBLIC_KEY` | `TEST-...` | Public key MercadoPago |
| `MERCADOPAGO_WEBHOOK_SECRET` | `secret` | Secreto para validar webhook |
| `MERCADOPAGO_NOTIFICATION_URL` | `http://localhost:8080/api/payments/webhook` | URL pública del webhook |
| `WS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Orígenes permitidos para WebSocket |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Orígenes permitidos para REST |
| `LOG_LEVEL` | `DEBUG` | Nivel para `com.foodv.backend` |
| `JPA_SHOW_SQL` | `false` | Mostrar SQL de Hibernate |
| `HIBERNATE_LOG_LEVEL` | `WARN` | Nivel de logs Hibernate |
| `AI_SERVICE_URL` | `http://localhost:8001` | URL del microservicio IA |
| `AI_SERVICE_SECRET_KEY` | `your-ai-service-secret-key` | API key compartida con `foodv-ai-service` |
| `FIREBASE_ENABLED` | `false` | Habilita/deshabilita FCM |
| `FIREBASE_CREDENTIALS_PATH` | `` | Ruta del JSON de Firebase |
| `FIREBASE_PROJECT_ID` | `` | Project ID de Firebase |

Nota: `.env.example` usa `PGADMIN_EMAIL` y `PGADMIN_PASSWORD`. Docker Compose los inyecta en el contenedor como `PGADMIN_DEFAULT_EMAIL` y `PGADMIN_DEFAULT_PASSWORD`, que son los nombres esperados por la imagen oficial de pgAdmin.

Ejemplo mínimo:

```env
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_NAME=foodv_db
DB_USERNAME=foodv_user
DB_PASSWORD=your-db-password
DB_POOL_SIZE=10
PGADMIN_EMAIL=admin@foodv.com
PGADMIN_PASSWORD=your-pgadmin-password
PGADMIN_PORT=5050
REDIS_HOST=localhost
REDIS_PORT=6380
JWT_SECRET=your-256-bit-secret-key-min-32-bytes
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
JWT_ISSUER=foodv-backend
MERCADOPAGO_ACCESS_TOKEN=your-mercadopago-access-token
MERCADOPAGO_PUBLIC_KEY=your-mercadopago-public-key
MERCADOPAGO_WEBHOOK_SECRET=your-mercadopago-webhook-secret
MERCADOPAGO_NOTIFICATION_URL=http://localhost:8080/api/payments/webhook
CLOUDINARY_CLOUD_NAME=placeholder
CLOUDINARY_API_KEY=placeholder
CLOUDINARY_API_SECRET=your-cloudinary-api-secret
WS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
LOG_LEVEL=DEBUG
JPA_SHOW_SQL=false
HIBERNATE_LOG_LEVEL=WARN
AI_SERVICE_URL=http://localhost:8001
AI_SERVICE_SECRET_KEY=your-ai-service-secret-key
FIREBASE_ENABLED=false
FIREBASE_CREDENTIALS_PATH=
FIREBASE_PROJECT_ID=
```

## Ejecución

```bash
# Levanta PostgreSQL, Redis, pgAdmin y foodv-ai-service
docker-compose up -d

# Arranca Spring Boot
./mvnw spring-boot:run
```

Docker Compose levanta:

| Servicio | Puerto host | Puerto contenedor |
|---|---:|---:|
| PostgreSQL | `5432` | `5432` |
| Redis | `6380` | `6379` |
| pgAdmin | `5050` | `80` |
| `foodv-ai-service` | `8001` | `8001` |

## URLs de Acceso

| URL | Descripción |
|---|---|
| `http://localhost:8080/api` | API REST con context path `/api` |
| `http://localhost:8080/api/swagger-ui.html` | Swagger UI cuando el perfil `dev` lo permite |
| `http://localhost:8080/api/api-docs` | OpenAPI JSON |
| `http://localhost:8080/api/actuator/health` | Health check |
| `http://localhost:8080/api/actuator/prometheus` | Métricas Prometheus en desarrollo |
| `http://localhost:5050` | pgAdmin |

## Swagger / OpenAPI

Swagger UI está configurado en:

```text
http://localhost:8080/api/swagger-ui.html
```

En el perfil `dev` queda disponible para desarrollo local. En producción debe restringirse desde configuración de seguridad o infraestructura.

## Tests

```bash
./mvnw test
```

Suite actual: **32 tests**.

| Suite | Tests | Tipo |
|---|---:|---|
| `OrderDomainServiceTest` | 12 | Unitario, máquina de estados |
| `LoginHandlerTest` | 5 | Unitario, autenticación |
| `CreateOrderHandlerTest` | 4 | Unitario, creación de órdenes |
| `CreateUserHandlerTest` | 3 | Unitario, registro de usuarios |
| `BackendApplicationTests` | 1 | Integración, contexto Spring |
| `ProductRepositoryIntegrationTest` | 4 | Integración, repositorio |
| `UserRepositoryIntegrationTest` | 3 | Integración, repositorio |

## CI/CD

GitHub Actions ejecuta automáticamente:

1. **Tests** con PostgreSQL 16 y Redis 7 como servicios.
2. **Build** del JAR con Maven.
3. Publicación de reportes de tests y artifact del build.

Workflow:

```text
.github/workflows/ci.yml
```

## Troubleshooting

### Docker no levanta por variables requeridas

Si `docker-compose up -d` falla por variables faltantes, revisa que exista `.env` y que incluya al menos:

```env
DB_NAME=foodv_db
DB_USERNAME=foodv_user
DB_PASSWORD=your-db-password
PGADMIN_EMAIL=admin@foodv.com
PGADMIN_PASSWORD=your-pgadmin-password
AI_SERVICE_SECRET_KEY=your-ai-service-secret-key
```

### Redis no conecta

En desarrollo local con Docker, Redis se publica en host `6380`:

```env
REDIS_HOST=localhost
REDIS_PORT=6380
```

Dentro de la red Docker, Redis usa `6379`.

### MercadoPago no llama al webhook

`MERCADOPAGO_NOTIFICATION_URL` debe apuntar a una URL pública alcanzable por MercadoPago:

```env
MERCADOPAGO_NOTIFICATION_URL=https://tu-dominio.com/api/payments/webhook
```

Para pruebas locales se requiere un túnel como `ngrok` o equivalente.

### Swagger no aparece

Verifica que el backend esté corriendo y usa la ruta con context path:

```text
http://localhost:8080/api/swagger-ui.html
```

## Proyectos Relacionados

- `foodv-mobile` — Frontend móvil con React Native + Expo SDK 54.
- `foodv-ai-service` — Microservicio de recomendaciones con FastAPI, Ollama y Groq.
