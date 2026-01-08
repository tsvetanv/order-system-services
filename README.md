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

---

## Next Phase

The next logical phase is **deployment**, including:

- Dockerization
- Environment profiles
- Database provisioning
- CI/CD pipelines
- Runtime configuration
- Observability

---

## Final Notes

This repository serves as both:

- A production-ready system
- A reference implementation for Architecture as Code


