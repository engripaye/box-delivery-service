# 📦 Box Delivery Service

> A production-minded RESTful API built with **Java 21 and Spring Boot** for managing delivery boxes, validating loading operations, enforcing battery and weight constraints, and exposing box availability and status information.

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=for-the-badge\&logo=springboot)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge\&logo=apachemaven)](https://maven.apache.org/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge\&logo=junit5)](https://junit.org/junit5/)
[![REST API](https://img.shields.io/badge/API-REST-blue?style=for-the-badge)](https://spring.io/projects/spring-framework)

---

## 🚀 Overview

**Box Delivery Service** is a Spring Boot REST API designed to manage delivery boxes and the items loaded into them.

The application focuses on enforcing the core operational rules of a delivery-box system:

* A box cannot exceed its configured weight capacity.
* A box cannot be loaded when its battery level is below **25%**.
* Items must satisfy strict validation rules.
* Boxes must be in an appropriate state before loading.
* Each box has a unique transaction reference.
* API errors are returned through a consistent error-response structure.
* Business logic is isolated from HTTP and persistence concerns.

The implementation deliberately favors **clarity, maintainability, testability, and clean separation of responsibilities** over unnecessary complexity.

---

## 🎯 Assessment Objectives

The application provides the core capabilities required by the assessment:

| Capability            | Endpoint                            |
| --------------------- | ----------------------------------- |
| Create a box          | `POST /api/v1/boxes`                |
| Load items into a box | `POST /api/v1/boxes/{txref}/items`  |
| Get loaded items      | `GET /api/v1/boxes/{txref}/items`   |
| Get available boxes   | `GET /api/v1/boxes/available`       |
| Get box battery level | `GET /api/v1/boxes/{txref}/battery` |

---

## 🧠 Key Business Rules

### ⚖️ Weight Constraint

A box must never exceed its configured weight capacity.

For example:

```text
Box capacity: 500g

Laptop: 250g
Mouse: 100g
Keyboard: 120g

Total: 470g ✅
```

But:

```text
Box capacity: 500g

Item A: 300g
Item B: 250g

Total: 550g ❌
```

The loading operation is rejected before the items are persisted.

---

### 🔋 Battery Constraint

A box must have at least **25% battery capacity** before it can be loaded.

```text
Battery = 80%  → ✅ Load allowed
Battery = 50%  → ✅ Load allowed
Battery = 25%  → ✅ Load allowed
Battery = 24%  → ❌ Load rejected
```

This rule is implemented as business logic in the service layer rather than being treated as simple request validation.

---

### 📦 Box State

Boxes follow a defined lifecycle:

```text
IDLE
  │
  │ Load items
  ▼
LOADING
  │
  │ Successful operation
  ▼
LOADED
  │
  ▼
DELIVERING
  │
  ▼
DELIVERED
  │
  ▼
RETURNING
  │
  ▼
IDLE
```

Only boxes in an appropriate state can accept new items.

---

## 🏗️ Architecture

The project follows a layered architecture:

```text
                    ┌──────────────────────┐
                    │      REST API        │
                    │     Controllers      │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Service        │
                    │  Business Logic      │
                    │  Business Rules       │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repository       │
                    │   Data Access Layer  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Database       │
                    │         H2           │
                    └──────────────────────┘
```

### Why this architecture?

The application intentionally separates:

* **Controllers** — HTTP/API concerns
* **Services** — business rules and application logic
* **Repositories** — persistence
* **Entities** — database/domain representation
* **DTOs** — API contracts
* **Exceptions** — domain-specific failure handling
* **Validation** — request-level constraints

This makes the system easier to test, extend, and maintain.

---

## 🛠️ Technology Stack

| Technology                  | Purpose                         |
| --------------------------- | ------------------------------- |
| **Java 21**                 | Application language            |
| **Spring Boot**             | Application framework           |
| **Spring Web**              | REST API                        |
| **Spring Data JPA**         | Persistence abstraction         |
| **H2 Database**             | Lightweight relational database |
| **Jakarta Bean Validation** | Request validation              |
| **Maven**                   | Dependency management & build   |
| **JUnit 5**                 | Unit testing                    |
| **Mockito**                 | Mock-based testing              |

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com.example.boxdelivery/
│   │       ├── controller/
│   │       │   └── BoxController.java
│   │       │
│   │       ├── service/
│   │       │   ├── BoxService.java
│   │       │   └── BoxServiceImpl.java
│   │       │
│   │       ├── repository/
│   │       │   ├── BoxRepository.java
│   │       │   └── ItemRepository.java
│   │       │
│   │       ├── entity/
│   │       │   ├── Box.java
│   │       │   └── Item.java
│   │       │
│   │       ├── dto/
│   │       │   ├── CreateBoxRequest.java
│   │       │   ├── LoadItemsRequest.java
│   │       │   ├── ItemRequest.java
│   │       │   ├── BoxResponse.java
│   │       │   ├── ItemResponse.java
│   │       │   ├── BatteryResponse.java
│   │       │   └── ErrorResponse.java
│   │       │
│   │       ├── enums/
│   │       │   └── BoxState.java
│   │       │
│   │       ├── exception/
│   │       │   ├── BoxNotFoundException.java
│   │       │   ├── BoxAlreadyExistsException.java
│   │       │   ├── InvalidBoxStateException.java
│   │       │   ├── InsufficientBatteryException.java
│   │       │   ├── OverweightException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       └── BoxDeliveryApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com.example.boxdelivery/
```

---

# 📡 API Documentation

## 1. Create a Box

### `POST /api/v1/boxes`

Creates a new delivery box.

### Request

```json
{
  "txref": "BOX001",
  "weightLimit": 500,
  "batteryCapacity": 80
}
```

### Response

```json
{
  "id": 1,
  "txref": "BOX001",
  "weightLimit": 500,
  "batteryCapacity": 80,
  "state": "IDLE"
}
```

---

## 2. Load Items Into a Box

### `POST /api/v1/boxes/{txref}/items`

Loads one or more items into a box after validating all applicable business rules.

### Request

```json
{
  "items": [
    {
      "name": "Laptop-01",
      "weight": 250,
      "code": "LAPTOP_01"
    },
    {
      "name": "Mouse_01",
      "weight": 100,
      "code": "MOUSE_01"
    }
  ]
}
```

### Successful Response

```json
{
  "message": "Items loaded successfully",
  "txref": "BOX001",
  "state": "LOADED"
}
```

---

## 3. Get Loaded Items

### `GET /api/v1/boxes/{txref}/items`

Returns all items currently associated with the specified box.

### Response

```json
[
  {
    "name": "Laptop-01",
    "weight": 250,
    "code": "LAPTOP_01"
  },
  {
    "name": "Mouse_01",
    "weight": 100,
    "code": "MOUSE_01"
  }
]
```

---

## 4. Get Available Boxes

### `GET /api/v1/boxes/available`

Returns boxes that are currently eligible for loading.

A box is considered available when:

```text
state = IDLE
AND
battery >= 25%
```

### Response

```json
[
  {
    "txref": "BOX001",
    "weightLimit": 500,
    "batteryCapacity": 80,
    "state": "IDLE"
  }
]
```

---

## 5. Get Box Battery Level

### `GET /api/v1/boxes/{txref}/battery`

Returns the current battery capacity of a box.

### Response

```json
{
  "txref": "BOX001",
  "batteryCapacity": 80
}
```

---

# ✅ Validation

The API uses Jakarta Bean Validation for request-level constraints.

### Box

```text
txref
├── required
├── maximum 20 characters
└── unique

weightLimit
├── positive
└── maximum 500g

batteryCapacity
├── minimum 0%
└── maximum 100%
```

### Item

```text
name
└── letters, numbers, '-' and '_'

weight
└── positive

code
└── uppercase letters, numbers and '_'
```

Business rules such as battery eligibility and total box weight are enforced separately within the service layer.

---

# 🚨 Error Handling

The API provides consistent error responses through a centralized exception handler.

Example:

```json
{
  "timestamp": "2026-08-19T10:30:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Box battery level must be at least 25% to load items",
  "path": "/api/v1/boxes/BOX001/items"
}
```

Handled scenarios include:

* Box not found
* Duplicate transaction reference
* Invalid box state
* Insufficient battery
* Box overweight
* Invalid request data
* Invalid item data
* Invalid state transitions

---

# 🧪 Testing

The project includes automated tests covering both normal and failure scenarios.

### Test coverage includes

* Creating a box
* Duplicate box prevention
* Invalid transaction references
* Invalid battery values
* Invalid weight limits
* Loading valid items
* Rejecting overweight boxes
* Rejecting boxes below 25% battery
* Rejecting invalid item names
* Rejecting invalid item codes
* Retrieving loaded items
* Retrieving available boxes
* Retrieving battery level
* Invalid box states

Run the complete test suite:

```bash
./mvnw clean test
```

Or with Maven installed:

```bash
mvn clean test
```

---

# ▶️ Running the Application

## Prerequisites

Make sure you have installed:

* Java 21+
* Maven 3.9+ (optional if using Maven Wrapper)
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

## Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate into the project:

```bash
cd box-delivery-service
```

---

## Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

# 🗄️ Database

The application uses **H2** as a lightweight relational database for assessment purposes.

This keeps the project:

* Easy to run
* Self-contained
* Reproducible
* Free from external database dependencies

The persistence layer uses Spring Data JPA, allowing the underlying database to be replaced with PostgreSQL or another relational database with minimal application-level changes.

---

# 🧩 Design Assumptions

The assessment explicitly allows reasonable assumptions. The following decisions were made:

### 1. Transaction Reference

`txref` uniquely identifies a box and has a maximum length of 20 characters.

### 2. Box Weight

A box has a configurable maximum weight capacity, but that capacity cannot exceed 500g.

### 3. Battery

Battery capacity is represented as a percentage from 0 to 100.

### 4. Loading Eligibility

A box must be in an appropriate loading state and have at least 25% battery capacity before items can be loaded.

### 5. Atomic Loading

Loading is treated as a single business operation. If validation fails, the items are not partially persisted.

### 6. Physical Box Communication

Communication with physical delivery hardware is outside the scope of this service. The API therefore focuses on the backend domain and persistence layer.

### 7. Delivery Lifecycle

The box state model is represented in the domain, while physical delivery operations are intentionally not implemented because they are outside the requested API scope.

---

# 🔐 Business Logic vs Validation

A deliberate distinction is made between **input validation** and **business rules**.

### Input validation

Handled using Jakarta Bean Validation:

```text
Is the field present?
Is the value within the allowed range?
Does the string match the required pattern?
```

### Business validation

Handled within the service layer:

```text
Does the box have enough battery?
Can this box currently be loaded?
Would loading these items exceed capacity?
Does the requested box exist?
```

This separation keeps the application easier to test and maintain.

---

# 📈 Future Improvements

If this service were being evolved beyond the assessment, possible improvements would include:

* PostgreSQL production profile
* OpenAPI / Swagger documentation
* Docker containerization
* Authentication and authorization
* Audit logging
* Pagination for large item collections
* Flyway database migrations
* Structured application logging
* Metrics and health monitoring
* Integration with physical delivery hardware
* CI/CD deployment pipeline
* Testcontainers for database integration tests

These features are intentionally outside the current scope to keep the assessment implementation focused.

---

# 💡 Engineering Principles

The implementation follows several practical engineering principles:

* **Single Responsibility Principle**
* **Separation of concerns**
* **Dependency Injection**
* **DTO-based API contracts**
* **Centralized exception handling**
* **Declarative validation**
* **Business-rule isolation**
* **Repository abstraction**
* **Automated testing**
* **Meaningful domain-specific exceptions**
* **Minimal unnecessary complexity**

---

# 📊 Quality Goals

The project prioritizes:

```text
Maintainability
      ↓
Testability
      ↓
Correctness
      ↓
Clear API design
      ↓
Readable code
      ↓
Appropriate architecture
```

The goal is not to build the largest possible system.

The goal is to build a backend that another engineer can **understand, test, extend, and trust**.

---

# 👨‍💻 Author

**Olabowale Babatunde Ipaye**

Backend Software Engineer

### Focus Areas

```text
Java
Spring Boot
REST APIs
Backend Engineering
SQL
System Design
Software Architecture
```

---

## ⭐ Why This Project

This project demonstrates the ability to translate business requirements into a maintainable backend service while balancing:

**Requirements → Domain Modeling → Validation → Business Logic → Persistence → API Design → Testing**

It was intentionally designed as a focused backend service rather than an over-engineered distributed system, keeping the implementation aligned with the assessment requirements while leaving clear paths for future production enhancements.

---

This project was developed for technical assessment purposes.
