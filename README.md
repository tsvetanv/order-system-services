# Order Processing System – Services

## Overview

`order-system-services` is the **executable backend implementation** of the Order Processing System.
It follows a **modular monolith** architecture with **explicit boundaries**, strong contracts, and
clear separation of concerns.

The system is designed to:

- Be **architecture-driven**
- Use **OpenAPI as a source of truth**
- Enforce **domain rules explicitly**
- Be **testable, evolvable, and deployment-ready**

This repository is part of a larger **Architecture as Code (AaC)** initiative and aligns with
the architectural decisions documented in the system ADRs.

---

## Architectural Principles

The system is built on the following principles:

### 1. Architecture as Code (AaC)

- Architecture is versioned, reviewed, and evolved like code
- OpenAPI, ADRs, and diagrams are treated as **first-class artifacts**
- Code must conform to architecture, not the other way around

### 2. Modular Monolith

- Single deployable unit
- Strong internal module boundaries
- Explicit dependencies between modules
- Clear migration path toward microservices if needed

### 3. Contract-First API Design

- OpenAPI is the **source of truth**
- API models are generated
- Controllers implement generated interfaces
- No handwritten REST contracts

### 4. Explicit Domain Modeling

- Business rules live in the **application/domain layer**
- Enums, value objects, and services model real concepts

---

## High-Level Module Structure

The `order-system-services` repository is organized as a **modular monolith**
consisting of a single executable Spring Boot application and several internal modules.

```
order-system-services
│
├── order-api
├── order-service
├── order-database
├── integration-service
└── pom.xml (parent)
```

Each module has a **single, well-defined responsibility**.

### Module Roles & Runtime Characteristics

| Module                | Role                                          | Runtime Type                 |
|-----------------------|-----------------------------------------------|------------------------------|
| `order-api`           | Public API contract (OpenAPI source of truth) | Contract module (no runtime) |
| `order-service`       | Application & domain logic                    | **Spring Boot application**  |
| `order-database`      | Persistence, schema, migrations               | Library module               |
| `integration-service` | External system integrations                  | Library module               |

**Key Points**

- Exactly **one** module (`order-service`) is a Spring Boot application
- All other modules are **internal libraries** contributing beans via the classpath
- The system is deployed as a **single unit**, preserving modular boundaries internally
- Module responsibilities are enforced by **dependencies**, not by separate runtimes

---

## Module Overview

### 1. `order-api` – API Contract Module

**Responsibility**

- Defines the **public API contract**
- Contains the OpenAPI specification
- Generates API models and interfaces

**Key Characteristics**

- No business logic
- No persistence
- No service dependencies
- Pure contract module

**Contents**

- `openapi.yaml`
- Generated:
    - API interfaces (e.g. `OrdersApi`)
    - API models (`Order`, `OrderItem`, `Money`, etc.)

**Key Rule**
> Other modules depend on `order-api`.  
> `order-api` depends on nothing.

---

### 2. `order-service` – Application & Domain Logic

**Responsibility**

- Implements all **order-related use cases**
- Enforces business rules
- Orchestrates domain services

**Key Components**

- Controllers (implement generated APIs)
- Application services
- Domain services
- DTOs
- Mappers
- Exception handling

**Important Concepts**

- `OrderService` interface + `OrderServiceImpl`
- `OrderStatus` enum (domain-owned)
- `PricingService` – single source of monetary calculations
- `ProductPricingService` – pricing abstraction boundary

**Key Rules**

- Controllers are thin
- No pricing logic outside `PricingService`
- No persistence logic in controllers
- No REST annotations outside controllers

---

### 3. `order-database` – Persistence Layer

**Responsibility**

- Owns database schema
- Owns JPA entities
- Owns repositories
- Owns migrations

**Key Characteristics**

- Flyway-managed schema
- PostgreSQL-first design
- Testcontainers-based integration tests

**Contents**

- JPA entities (`OrderEntity`, `OrderItemEntity`)
- Repositories (`OrderRepository`)
- Flyway migrations
- Database integration tests

**Key Rule**
> `order-database` contains **no business logic**.

---

### 4. `integration-service` – External Integrations (Future)

**Responsibility**

- Integration with external systems (ERP, pricing, shipping, etc.)
- Currently a placeholder for future extensions

---

## Module Dependencies

```
order-service
├── depends on order-api
├── depends on order-database
└── depends on integration-service (future)

order-api
└── no dependencies

order-database
└── no dependency on order-service
```

**Important**

- Dependencies always point **inward**
- Database never depends on service logic
- API never depends on implementation

---

## Business Rules (Key Examples)

### Order Lifecycle

- Orders have a well-defined lifecycle via `OrderStatus`
- Status transitions are explicitly enforced

### Cancel Order Rules

- Allowed only in states: `CREATED`, `CONFIRMED`
- Forbidden in states: `PAID`, `SHIPPED`
- Idempotent if already `CANCELLED`

### Pricing Rules

- All monetary calculations are centralized
- `Money` is a value object
- No floating-point arithmetic
- Currency consistency enforced

---

## Pricing & Money Model

### Money

- Immutable value object
- BigDecimal amount
- ISO currency code
- Safe arithmetic operations

### PricingService

- Calculates:
    - Unit price
    - Line total
    - Order total
- Single source of pricing logic

### ProductPricingService

- Abstraction for future pricing integration
- Currently returns a baseline price
- Replaceable without touching domain logic

---

## Health Check

The `order-service` exposes a health endpoint via **Spring Boot Actuator**.
This endpoint is used by AWS for load balancer health checks and operational monitoring.

### Endpoint

```http
GET /actuator/health
```

### Example

```bash
curl http://<alb-dns-name>/actuator/health
```

Response:

```json
{
  "status": "UP"
}
```

---

## Testing Strategy

### order-database

- Flyway migration tests
- PostgreSQL Testcontainers
- Schema verification

### order-service

- Unit tests for:
    - Application services
    - Business rules
- Controller integration tests using:
    - `@WebMvcTest`
    - Raw JSON payloads
    - Mocked dependencies

---

## API Testing with Postman

A ready-to-use Postman collection is provided to test the deployed Order Service
running on AWS.

### Location

Postman artifacts are stored under the `order-service` module:

```
order-service/postman/
├── order-service-aws.postman_collection.json
└── order-service-aws.postman_environment.json
```

### Import Steps

1. Open **Postman**
2. Import both files:

- `order-service-aws.postman_collection.json`
- `order-service-aws.postman_environment.json`

3. Select environment: **order-service-aws**

### Covered Use Cases

The collection covers the core Order Processing use cases:

- Health check
- Create order
- Get order by ID
- Cancel order

### Important Contract Rules

- All identifiers (`customerId`, `productId`, `orderId`) are **UUIDs**
- Requests not matching the OpenAPI contract are rejected with **HTTP 400**
- Validation is intentionally strict to enforce API correctness

---

## Deployment Readiness

At this stage, the system is:

- Functionally complete
- Architecturally stable
- Test-covered
- Ready for:
    - Containerization
    - CI/CD
    - Environment configuration
    - Infrastructure as Code

## Local Development & Runtime Environment

This section focuses on **local execution only**. Cloud deployment concerns are intentionally
out of scope here and are addressed in the **Deployment Readiness** section.

The goal is to describe how to run and test the system locally using **Docker** and **Spring
profiles**.

## Local Database Setup (Docker)

For local development, a Docker Compose setup is provided to start a PostgreSQL instance
that matches the `local` Spring profile configuration.

---

### Local DB Lifecycle

```
docker compose up
      ↓
PostgreSQL container starts (localhost:5432)
      ↓
Spring Boot connects using `local` profile
      ↓
Flyway runs database migrations
      ↓
Application / tests are ready
```

---

### Start the Local Database

#### Linux / macOS / WSL

```bash
./scripts/db-up.sh
```

#### Windows

```bat
scripts\db-up.bat
```

---

### Stop the Local Database

#### Linux / macOS / WSL

```bash
./scripts/db-down.sh
```

#### Windows

```bat
scripts\db-down.bat
```

---

### Using the Database with `order-database`

#### Test profile (`test`)

- Uses PostgreSQL Testcontainers
- Fully isolated
- No local database required

```bash
./scripts/run-db-test.sh
```

#### Local profile (`local`)

- Uses local PostgreSQL Docker container
- Useful for debugging and schema inspection

```bash
./scripts/run-db-local.sh
```

Flyway migrations are executed automatically on startup.

---

### Docker Runtime Notes (Rancher Desktop on Windows)

If you are using **Rancher Desktop** instead of Docker Desktop on Windows,
you may encounter Docker context–related issues on first run.

#### Fix

```bash
rm -rf ~/.docker/contexts/meta
docker context use default
```

Then retry:

```bash
./scripts/db-up.sh
```

This is a one-time setup step.

---

## Running the Application (Docker)

In addition to running the database in isolation, the full Order Processing System
(application + database) can be started locally using Docker Compose.

This setup runs:

- `order-service` as a Docker container
- PostgreSQL as a Docker container
- The application using the `local` Spring profile

All configuration is provided via environment variables and Docker networking.

---

### Start Full Application Stack

#### Linux / macOS / WSL

```bash
./scripts/app-up.sh
```

#### Windows

```bash
scripts\app-up.bat
```

This will:

1. Stop any standalone database containers (if running)
2. Build all modules and the executable application JAR
3. Build the Docker image for order-service
4. Start the application and database containers
5. Apply Flyway migrations automatically on startup

The API will be available at:

```code
http://localhost:8081
```

---

### Stop Full Application Stack

#### Linux / macOS / WSL

```bash
./scripts/app-down.sh
```

#### Windows

```bash
scripts\app-down.bat
```

This stops all containers and cleans up Docker networks and volumes
used by the local application stack.

---

### Docker Compose Structure

The project uses two Docker Compose configurations:

- `docker-compose.db.yml`  
  Starts only PostgreSQL (useful for local development and database testing)
- `docker-compose.yml`  
  Starts the full system (application + database)

This separation keeps concerns clear and supports multiple development workflows.

---

## CI/CD Pipeline (GitHub Actions)

This repository defines the CI pipeline for the Order Processing System
application runtime.

The pipeline is responsible for:

- Building the application
- Running unit and integration tests
- Enforcing architectural guardrails (ArchUnit)
- Building a Docker image
- Publishing the image to Amazon ECR

### Why This Matters

Architectural correctness is enforced *before deployment*.
If a rule is violated, the application is never deployed to AWS.

### Pipeline Overview

GitHub Actions → Maven build → ArchUnit → Docker build → ECR push


---

## Final Notes

This repository serves as both:

- A production-ready system
- A reference implementation for Architecture as Code
