# ☕ Coffee Shop Management System

A production-like backend application built with **Java 21**, demonstrating **Virtual Threads**, **Clean Architecture**, and modern Spring Boot best practices.

[![CI](https://github.com/yourusername/coffee-shop/workflows/CI/badge.svg)](https://github.com/yourusername/coffee-shop/actions)
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Virtual Threads](#virtual-threads)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Observability](#observability)
- [Deployment](#deployment)
- [Trade-offs: Virtual Threads vs Reactive](#trade-offs-virtual-threads-vs-reactive)

## 🎯 Overview

This is a minimal yet production-ready coffee shop management system that handles:

- ✅ **Product Management**: CRUD operations for coffee products with inventory tracking
- ✅ **Order Processing**: Create orders with multiple items, payment processing, and cancellation
- ✅ **Inventory Management**: Automatic low-stock detection and restocking workflows
- ✅ **Authentication & Authorization**: JWT-based security with role-based access control
- ✅ **Observability**: Metrics, tracing, and monitoring with Prometheus and Grafana

## 🌟 Key Features

### Virtual Threads (Java 21)
- **Structured Concurrency**: Demonstrates `StructuredTaskScope` for parallel I/O operations
- **High Concurrency**: Handle thousands of concurrent requests efficiently
- **Blocking I/O Friendly**: Use traditional JDBC without the complexity of reactive programming

### Clean Architecture
- **Domain-Driven Design**: Pure domain logic without framework dependencies
- **Hexagonal Architecture**: Clear separation between domain, application, and infrastructure layers
- **Repository Pattern**: Abstract data access through ports and adapters

### Production-Ready
- **Security**: JWT authentication, BCrypt password hashing, role-based authorization
- **Validation**: Request validation with clear error messages (RFC 7807)
- **Testing**: Unit, integration, and slice tests with Testcontainers
- **Observability**: Micrometer metrics, OpenTelemetry tracing, Prometheus integration
- **CI/CD**: GitHub Actions workflow with automated testing and Docker builds

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      API Layer (REST)                       │
│  Controllers, DTOs, Exception Handlers, OpenAPI/Swagger    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                          │
│     Use Cases, Services, Mappers, Domain Events             │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                            │
│  Entities, Value Objects, Repository Ports, Business Logic  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                        │
│   JPA Adapters, Security, Configuration, External Services  │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
src/main/java/com/coffeeshop/
├── api/                      # REST API layer
│   ├── controller/           # REST controllers
│   ├── dto/                  # API request/response DTOs
│   └── exception/            # Global exception handling
├── application/              # Application layer
│   ├── dto/                  # Application DTOs
│   ├── event/                # Event handlers
│   ├── exception/            # Business exceptions
│   ├── mapper/               # MapStruct mappers
│   └── service/              # Use case implementations
├── domain/                   # Domain layer (pure business logic)
│   ├── order/                # Order aggregate
│   ├── product/              # Product aggregate
│   └── shared/               # Shared domain objects
└── infrastructure/           # Infrastructure layer
    ├── config/               # Spring configurations
    ├── persistence/          # JPA entities and adapters
    └── security/             # Security implementation
```

## 🛠️ Tech Stack

| Category | Technologies |
|----------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.4.0 |
| **Build Tool** | Maven 3.9+ |
| **Database** | PostgreSQL 15+ |
| **Migration** | Flyway |
| **Security** | Spring Security + JWT (HS256) |
| **Validation** | Jakarta Validation |
| **Mapping** | MapStruct |
| **Testing** | JUnit 5, Mockito, Testcontainers, WireMock |
| **Observability** | Micrometer, Prometheus, OpenTelemetry |
| **Documentation** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Code Quality** | Spotless, Checkstyle, SpotBugs |
| **Containerization** | Docker, Docker Compose |
| **CI/CD** | GitHub Actions |

## 🚀 Virtual Threads

### Why Virtual Threads?

Virtual Threads (Project Loom) provide the simplicity of thread-per-request programming with the scalability of asynchronous programming:

- **High Throughput**: Handle millions of concurrent tasks
- **Simple Code**: Write synchronous, blocking code that's easy to understand
- **Efficient I/O**: Virtual threads are parked during I/O operations, freeing up OS threads
- **No Callbacks**: Avoid callback hell and complex reactive operators

### Enabling Virtual Threads

In `application.yaml`:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

### Structured Concurrency Example

The `OrderService.createOrder()` method demonstrates Structured Concurrency with `StructuredTaskScope`:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    // Fork parallel tasks
    var productsFuture = scope.fork(() -> loadProducts(productIds));
    var discountFuture = scope.fork(() -> discountService.calculateDiscount(customerId));
    var shippingFuture = scope.fork(() -> shippingService.estimateShipping(customerId));
    
    // Wait for all to complete
    scope.join();
    scope.throwIfFailed();
    
    // Get results
    Map<UUID, Product> products = productsFuture.get();
    double discount = discountFuture.get();
    double shipping = shippingFuture.get();
    
    // Process order...
}
```

**Benefits:**
- All tasks run in parallel on virtual threads
- If any task fails, all others are automatically cancelled
- Clear, structured error handling
- No need for CompletableFuture chains

### Debugging Virtual Threads

Use `jcmd` to see virtual threads in action:

```bash
# Get the PID
jps

# Dump threads
jcmd <PID> Thread.dump_to_file -format=json threads.json

# Count virtual threads
jcmd <PID> Thread.print | grep "VirtualThread" | wc -l
```

## 🏃 Getting Started

### Prerequisites

- **Java 21** or higher
- **Docker** and **Docker Compose** (for database)
- **Maven 3.9+** (or use included wrapper)

### Option 1: Quick Start with Docker Compose

```bash
# Start all services (app, postgres, prometheus, grafana)
docker compose up -d

# View logs
docker compose logs -f app

# Stop all services
docker compose down
```

The application will be available at:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Option 2: Run Locally

```bash
# 1. Start PostgreSQL
docker compose up -d postgres

# 2. Run the application
./mvnw spring-boot:run

# Or use make
make dev
```

### Option 3: Build and Run JAR

```bash
# Build
./mvnw clean package

# Run
java -jar target/coffee-shop-1.0.0-SNAPSHOT.jar
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/coffee` | PostgreSQL connection URL |
| `DATABASE_USERNAME` | `coffee` | Database username |
| `DATABASE_PASSWORD` | `coffee` | Database password |
| `JWT_SECRET` | (see config) | JWT signing secret (change in production!) |
| `DB_POOL_SIZE` | `32` | HikariCP connection pool size |

## 📚 API Documentation

### Authentication

#### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@local",
    "password": "Admin#123"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Products

#### Create Product (Admin only)

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Espresso",
    "sku": "COFFEE-ESP-001",
    "price": 2.50,
    "initialStock": 100
  }'
```

#### Get All Products

```bash
curl http://localhost:8080/api/v1/products?page=0&size=20&sort=name,asc
```

#### Restock Product (Admin only)

```bash
curl -X PATCH http://localhost:8080/api/v1/products/{id}/restock \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50
  }'
```

### Orders

#### Create Order

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "c0000000-0000-0000-0000-000000000001",
        "quantity": 2
      },
      {
        "productId": "c0000000-0000-0000-0000-000000000003",
        "quantity": 1
      }
    ]
  }'
```

#### Pay Order

```bash
curl -X POST http://localhost:8080/api/v1/orders/{id}/pay \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Cancel Order

```bash
curl -X POST http://localhost:8080/api/v1/orders/{id}/cancel \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get All Orders (Admin only)

```bash
curl "http://localhost:8080/api/v1/orders?status=CREATED&page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Default Users

| Email | Password | Roles |
|-------|----------|-------|
| `admin@local` | `Admin#123` | ADMIN, USER |
| `user@local` | `User#123` | USER |

### Interactive API Documentation

Open Swagger UI: **http://localhost:8080/swagger-ui.html**

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

### Run Integration Tests with Testcontainers

```bash
./mvnw verify
```

### Run Specific Test

```bash
./mvnw test -Dtest=ProductServiceTest
```

### Test Coverage

The project includes:
- **Unit Tests**: Domain logic and service layer (no Spring context)
- **Integration Tests**: Repository layer with real PostgreSQL (Testcontainers)
- **Slice Tests**: Controller layer with MockMvc and security
- **Application Tests**: Full Spring Boot context with Testcontainers

### Code Quality Checks

```bash
# Check code formatting
./mvnw spotless:check

# Apply code formatting
./mvnw spotless:apply

# Run Checkstyle
./mvnw checkstyle:check

# Run SpotBugs
./mvnw spotbugs:check
```

## 📊 Observability

### Actuator Endpoints

- **Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Info**: http://localhost:8080/actuator/info

### Prometheus Metrics

Start Prometheus with Docker Compose:

```bash
docker compose up -d prometheus
```

Access Prometheus UI: **http://localhost:9090**

**Key Metrics:**
- `http_server_requests_seconds` - HTTP request duration
- `jvm_threads_states_threads{state="runnable"}` - Thread count
- `hikaricp_connections_active` - Active DB connections
- `process_cpu_usage` - CPU usage
- `jvm_memory_used_bytes` - Memory usage

### Grafana Dashboards

Start Grafana with Docker Compose:

```bash
docker compose up -d grafana
```

Access Grafana: **http://localhost:3000** (admin/admin)

Import Spring Boot dashboard:
1. Go to Dashboards → Import
2. Enter dashboard ID: **4701** (JVM Micrometer)
3. Select Prometheus datasource

### OpenTelemetry Tracing

Traces are logged to console. To export to Zipkin:

1. Uncomment Zipkin dependency in `pom.xml`
2. Add to `application.yaml`:
```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

## 🚢 Deployment

### Build Docker Image

```bash
docker build -t coffee-shop:latest .
```

The Docker image uses:
- **Multi-stage build** for smaller image size
- **Distroless base** for security
- **Non-root user** (65532)
- **Optimized JVM settings** for containers

### Connection Pool Sizing

For Virtual Threads, the connection pool should be sized based on:

```
Pool Size = Number of CPU cores × 2 to 4
```

**Example:**
- 4 CPU cores → 16-32 connections
- 8 CPU cores → 32-64 connections

Set via environment variable:
```bash
export DB_POOL_SIZE=32
```

### Performance Tuning

**JVM Options** (already in Dockerfile):
```bash
-XX:+UseZGC                    # Use ZGC garbage collector
-XX:+UseStringDeduplication    # Reduce memory usage
-Xms512m -Xmx1g                # Heap size
-XX:MaxRAMPercentage=75.0      # Use 75% of container memory
```

**Virtual Threads Best Practices:**
- Use blocking I/O (JDBC, REST clients)
- Avoid thread pools for I/O operations
- Don't use `synchronized` blocks (prefer `ReentrantLock`)
- Size connection pools appropriately

### Load Testing

Expected performance with Virtual Threads:
- **RPS**: 10,000+ requests per second (simple endpoints)
- **Concurrency**: Handle 100,000+ concurrent connections
- **Latency**: p99 < 100ms for database operations

## ⚖️ Trade-offs: Virtual Threads vs Reactive

### Virtual Threads (This Project)

**Pros:**
- ✅ Simple, readable code (synchronous style)
- ✅ Easy to debug with standard tools
- ✅ Works with existing libraries (JDBC, etc.)
- ✅ Lower learning curve
- ✅ Better IDE support

**Cons:**
- ❌ Requires Java 21+
- ❌ Still maturing (GA in Java 21, but evolving)
- ❌ Some edge cases with `synchronized` blocks

**Best For:**
- Traditional web applications
- Microservices with blocking I/O
- Teams familiar with imperative programming
- Projects with JDBC/JPA

### Reactive (Spring WebFlux)

**Pros:**
- ✅ Mature ecosystem (Reactor, RxJava)
- ✅ Better for streaming data
- ✅ Built-in backpressure
- ✅ Works on Java 8+

**Cons:**
- ❌ Steep learning curve
- ❌ Complex error handling
- ❌ Debugging is difficult
- ❌ Requires non-blocking drivers (R2DBC)
- ❌ "Viral" - affects entire codebase

**Best For:**
- Real-time data streaming
- High-throughput event processing
- Systems with backpressure requirements
- Already using reactive databases

### Recommendation

**Choose Virtual Threads when:**
- Building new applications on Java 21+
- Team prefers synchronous programming
- Using traditional databases (JDBC)
- Need high concurrency without complexity

**Choose Reactive when:**
- Need streaming/real-time data processing
- Require backpressure handling
- Stuck on Java 8-17
- Building reactive microservices ecosystem

## 📖 Additional Resources

### Virtual Threads & Project Loom
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 453: Structured Concurrency](https://openjdk.org/jeps/453)
- [Spring Boot 3.2 Virtual Threads](https://spring.io/blog/2023/09/09/all-together-now-spring-boot-3-2-graalvm-native-images-java-21-and-virtual)

### Architecture & Design
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)

### Spring Boot & Patterns
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [RFC 7807 Problem Details](https://www.rfc-editor.org/rfc/rfc7807)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Built with ❤️ to demonstrate modern Java backend development with Virtual Threads and Clean Architecture.

---

**Happy Coding! ☕**
