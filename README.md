# 🛡️ Resilience Patterns POC

> **Proof of Concept** para implementación de patrones de resiliencia en microservicios usando **Spring Boot** y **Resilience4j**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-2.0.2-blue.svg)](https://resilience4j.readme.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

---

## 📋 Tabla de Contenidos

- [🎯 Objetivo](#-objetivo)
- [🏗️ Arquitectura](#️-arquitectura)
- [🛠️ Patrones Implementados](#️-patrones-implementados)
- [🚀 Inicio Rápido](#-inicio-rápido)
- [⚙️ Configuración](#️-configuración)
- [🧪 Testing](#-testing)
- [📊 Monitoreo](#-monitoreo)
- [🔧 Desarrollo](#-desarrollo)

---

## 🎯 Objetivo

Este proyecto demuestra la implementación de **patrones de resiliencia** en aplicaciones distribuidas, proporcionando:

- **Tolerancia a fallos** en servicios externos
- **Degradación elegante** ante errores
- **Monitoreo y observabilidad** en tiempo real
- **Arquitectura hexagonal** con separación de responsabilidades

---

## 🏗️ Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │    │   Use Cases     │    │   External API  │
│   (REST/MQ)     │◄──►│   (Business)    │◄──►│   (Resilient)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Validation    │    │   Domain Model  │    │  Circuit Breaker│
│   Exception     │    │   Entities      │    │  Rate Limiter   │
│   Handling      │    │   Value Objects │    │  Retry Pattern  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 🏛️ Hexagonal Architecture

- **Domain**: Lógica de negocio pura - Define lo que necesita
- **Application**: Casos de uso y orquestación - Coordina todo
- **Infrastructure**: Adaptadores externos - Maneja conversiones

> **Enfoque**: Hexagonal Original (Alistair Cockburn) - Domain define necesidades, Infrastructure convierte, respetando DIP

---

## 🛠️ Patrones Implementados

### 🔄 Circuit Breaker
```yaml
resilience4j:
  circuitbreaker:
    instances:
      external-api:
        failure-rate-threshold: 50%
        minimum-number-of-calls: 5
        wait-duration-in-open-state: 30s
```

### 🚦 Rate Limiter
```yaml
resilience4j:
  ratelimiter:
    instances:
      external-api:
        limit-for-period: 20
        limit-refresh-period: 60s
```

### 🔁 Retry Pattern
```yaml
resilience4j:
  retry:
    instances:
      external-api:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
```

---

## 🚀 Inicio Rápido

### 📋 Prerrequisitos

- **Java 21+**
- **Maven 3.6+**
- **Docker & Docker Compose**

### 🏃♂️ Ejecución

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd resilience-patterns-poc
   ```

2. **Levantar servicios de desarrollo**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Verificar funcionamiento**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### 🌐 Servicios Disponibles

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Aplicación** | http://localhost:8080 | - |
| **RabbitMQ Management** | http://localhost:15672 | admin/admin |
| **WireMock** | http://localhost:8082 | - |
| **Redis** | localhost:6379 | - |

---

## ⚙️ Configuración

### 🌍 Perfiles de Entorno

| Perfil | Propósito | Base de Datos | Logging |
|--------|-----------|---------------|---------|
| `dev` | Desarrollo | H2 (memoria) | DEBUG |
| `test` | Testing | H2 (memoria) | WARN |
| `prod` | Producción | Configurable | INFO |

### 🔧 Variables de Entorno

```bash
# API Externa
EXTERNAL_API_URL=http://localhost:8082
EXTERNAL_API_TIMEOUT=5000

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin

# Actuator
MANAGEMENT_ENDPOINTS_ENABLED=true
```

### ⚙️ Configuración de Resilience4j

```yaml
resilience4j:
  circuitbreaker:
    instances:
      external-api:
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        sliding-window-size: 10
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 3
        
  ratelimiter:
    instances:
      external-api:
        limit-for-period: 20
        limit-refresh-period: 60s
        timeout-duration: 30s
        
  retry:
    instances:
      external-api:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
```

---

## 🧪 Testing

### 🏃♂️ Ejecutar Tests

```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw test -Dtest="*IntegrationTest"

# Tests con cobertura
./mvnw test jacoco:report

# Tests específicos de resiliencia
./mvnw test -Dtest="UserRegistrationAdapterCircuitBreakerIntegrationTest"
```

### 🎯 Tipos de Tests

- **Unit Tests**: Lógica de dominio
- **Integration Tests**: Patrones de resiliencia con WireMock y Testcontainers
- **Contract Tests**: Validación de APIs externas

### 📊 Cobertura

Los reportes de cobertura se generan en `target/site/jacoco/`

### 🐳 Testcontainers

Los tests de integración utilizan:
- **WireMock**: Simulación de APIs externas
- **RabbitMQ**: Tests de mensajería
- **H2**: Base de datos en memoria

---

## 📊 Monitoreo

### 🔍 Endpoints de Actuator

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado de la aplicación |
| `/actuator/metrics` | Métricas de rendimiento |
| `/actuator/circuitbreakers` | Estado de circuit breakers |
| `/actuator/ratelimiters` | Estado de rate limiters |
| `/actuator/retries` | Estadísticas de reintentos |

### 📈 Métricas Clave

- **Circuit Breaker**: Estado (OPEN/CLOSED/HALF_OPEN)
- **Rate Limiter**: Requests permitidos/rechazados
- **Retry**: Intentos exitosos/fallidos
- **Response Time**: Latencia de APIs externas

### 🔍 Ejemplo de Monitoreo

```bash
# Estado de circuit breakers
curl http://localhost:8080/actuator/circuitbreakers

# Métricas de rate limiter
curl http://localhost:8080/actuator/ratelimiters

# Health check completo
curl http://localhost:8080/actuator/health
```

---

## 🔧 Desarrollo

### 🏗️ Estructura del Proyecto

```
src/
├── main/java/com/resiliencepatterns/poc/
│   ├── application/          # Casos de uso y DTOs
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── mapper/          # Mappers de aplicación
│   │   ├── port/            # Puertos (interfaces)
│   │   └── usecases/        # Casos de uso
│   ├── domain/              # Entidades y lógica de negocio
│   │   ├── exceptions/      # Excepciones de dominio
│   │   ├── model/           # Entidades y Value Objects
│   │   └── services/        # Servicios de dominio
│   └── infrastructure/      # Adaptadores externos
│       ├── adapters/        # Adaptadores de entrada y salida
│       ├── clients/         # Clientes HTTP (Feign)
│       ├── config/          # Configuraciones
│       ├── exceptions/      # Manejo de excepciones
│       ├── persistence/     # Persistencia (JPA)
│       └── resilience/      # Monitoreo de resiliencia
├── main/resources/
│   ├── application.yml      # Configuración base
│   ├── application-dev.yml  # Configuración desarrollo
│   └── logback-spring.xml   # Configuración de logs
└── test/
    ├── java/               # Tests
    └── resources/
        └── application-test.yml
```

### 🛠️ Tecnologías

- **Spring Boot 3.5.6**: Framework principal
- **Resilience4j 2.0.2**: Patrones de resiliencia
- **OpenFeign**: Cliente HTTP declarativo
- **H2 Database**: Base de datos en memoria
- **RabbitMQ**: Mensajería asíncrona
- **WireMock**: Simulación de APIs externas
- **Testcontainers**: Tests de integración
- **MapStruct**: Mapeo de objetos
- **Lombok**: Reducción de boilerplate

### 📝 Convenciones

- **Arquitectura Hexagonal**: Separación clara de capas
- **Domain-Driven Design**: Modelado centrado en el dominio
- **Clean Code**: Código legible y mantenible
- **Test-Driven Development**: Tests como documentación

### 🔄 Flujo de Desarrollo

1. **Crear feature branch** desde `main`
2. **Implementar funcionalidad** siguiendo arquitectura hexagonal
3. **Escribir tests** unitarios e integración
4. **Verificar patrones de resiliencia** funcionan correctamente
5. **Crear Pull Request** con descripción detallada

---

## 🚀 Despliegue

### 🐳 Docker

```bash
# Construir imagen
docker build -t resilience-patterns-poc .

# Ejecutar contenedor
docker run -p 8080:8080 resilience-patterns-poc
```

### ☁️ Producción

Para producción, configurar:

1. **Base de datos externa** (PostgreSQL/MySQL)
2. **RabbitMQ cluster**
3. **Redis para cache**
4. **Monitoreo con Prometheus/Grafana**
5. **Logs centralizados con ELK Stack**

---

## 👥 Contribución

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### 📋 Checklist para PRs

- [ ] Tests unitarios pasan
- [ ] Tests de integración pasan
- [ ] Cobertura de código > 80%
- [ ] Documentación actualizada
- [ ] Patrones de resiliencia funcionan
- [ ] No hay vulnerabilidades de seguridad


---

## 👤 Autor

**Ángela Carolina Castillo Rodríguez**
- Email: angiekroll@gmail.com
- LinkedIn: [perfil de LinkedIn]
- GitHub: [perfil de GitHub]

---

---

<div align="center">

**⭐ Si este proyecto te fue útil, considera darle una estrella ⭐**


PENDIENTE BORRAR O REFINAR
</div>
Proyecto con Hexagonal con enfoque en (HEXAGONAL Original - Alistair Cockburn) --> Domain define lo que necesita,
NO ROMPE DIP  DIP claro y respetado, Infrastructure maneja conversiones
Conversiones en lugar correcto (Infrastructure)
Domain define necesidades
"Application coordina todo"
Application solo coordina


Otro enfoque --> (Clean Architecture - Uncle Bob) -->
Domain puro, sin dependencias
"Application coordina todo"
NO define puertos (esa es la diferencia)
LA CAPA DE APLICACION DEFINE LOS PUERTOS
INFRA CONVIERTE, APPLICATION NO CONVIERTE NADA

DIP ambiguo
Conversiones en lugar incierto
Puede romper DIP fácilmente
PARA EVITAR ROMPER DIP TOCARIA CONVERTIR EN INFRA