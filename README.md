# Box Delivery Service

> A production-minded RESTful API built with **Java 21 and Spring Boot 4.0.7** for managing delivery boxes, validating loading operations, enforcing battery and weight constraints, and exposing box availability and status information.

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.7-brightgreen?style=for-the-badge\&logo=springboot)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge\&logo=apachemaven)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge\&logo=junit5)
![REST API](https://img.shields.io/badge/API-REST-blue?style=for-the-badge)

---

## Overview

**Box Delivery Service** is a Spring Boot REST API designed to manage delivery boxes and the items loaded into them.

The application focuses on the core business requirements of the assessment:

* Creating and managing boxes
* Loading items into boxes
* Enforcing box weight capacity
* Preventing loading when battery capacity is below **25%**
* Validating item and box data
* Determining available boxes
* Retrieving loaded items
* Retrieving box battery capacity
* Providing structured API errors
* Persisting domain data using JPA and H2

The implementation prioritizes **clean architecture, separation of concerns, business-rule isolation, testability, and maintainability** without introducing unnecessary infrastructure beyond the scope of the assessment.

---

# Assessment Objectives

The API provides the required box-management capabilities:

| Capability            | HTTP Method | Endpoint                     |
| --------------------- | ----------: | ---------------------------- |
| Create a box          |      `POST` | `/api/boxes`                 |
| Load items into a box |      `POST` | `/api/boxes/{txref}/items`   |
| Get loaded items      |       `GET` | `/api/boxes/{txref}/items`   |
| Get available boxes   |       `GET` | `/api/boxes/available`       |
| Get battery capacity  |       `GET` | `/api/boxes/{txref}/battery` |

---

# Architecture

The application follows a layered architecture designed to keep responsibilities clearly separated.

```text
                    ┌───────────────────────┐
                    │      REST API         │
                    │      Controller       │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │       Service         │
                    │   Business Logic      │
                    │   Business Rules      │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │      Repository       │
                    │    Data Access        │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │      H2 Database      │
                    └───────────────────────┘
```

### Layer Responsibilities

**Controller**

Handles:

* HTTP requests
* Request/response mapping
* API endpoints

**Service**

Handles:

* Business rules
* Box loading logic
* Capacity calculations
* Battery eligibility
* State validation
* Domain-specific exceptions

**Repository**

Handles:

* Database access
* Entity persistence
* Query operations

**Entity**

Represents the persistent domain model.

**DTO**

Defines the API request and response contracts.

**Exception Layer**

Provides domain-specific exceptions and centralized API error handling.

---

# Technology Stack

| Technology                  | Purpose                         |
| --------------------------- | ------------------------------- |
| **Java 21**                 | Application language            |
| **Spring Boot 4.0.7**       | Application framework           |
| **Spring Web**              | REST API                        |
| **Spring Data JPA**         | Persistence abstraction         |
| **H2 Database**             | Lightweight relational database |
| **Jakarta Bean Validation** | Request validation              |
| **Maven**                   | Build and dependency management |
| **JUnit 5**                 | Automated testing               |
| **Mockito**                 | Unit testing                    |
| **Lombok**                  | Boilerplate reduction           |

---

# Domain Model

## Box

A box contains:

```text
Box
├── id
├── txref
├── weightLimit
├── batteryCapacity
├── state
└── items
```

### Box State

The domain supports the following states:

```text
IDLE
LOADING
LOADED
DELIVERING
DELIVERED
RETURNING
```

The current assessment scope focuses on box creation, availability, loading, item retrieval, and battery information. Physical delivery and hardware communication are intentionally outside the implemented API scope.

---

## Item

An item contains:

```text
Item
├── id
├── name
├── weight
├── code
└── box
```

A box can contain multiple items through a one-to-many relationship.

```text
Box 1 ───────────────── * Item
```

---

# Core Business Rules

## ⚖️ Weight Capacity

The total weight of items loaded into a box must never exceed the box's configured weight limit.

```text
total existing item weight
+
total incoming item weight
≤
box weight limit
```

For example:

```text
Box capacity: 500g

Laptop: 200g
Mouse: 50g

Total: 250g

250g ≤ 500g ✅
```

An operation that exceeds the configured capacity is rejected.

---

## 🔋 Minimum Battery Requirement

A box must have at least **25% battery capacity** before it can accept items.

```text
85%  → ✅
60%  → ✅
25%  → ✅
24%  → ❌
```

The minimum threshold is represented as a service-level business rule rather than being treated merely as request validation.

---

## Box Availability

A box is considered available for loading when:

```text
state = IDLE
AND
batteryCapacity >= 25%
```

This rule is used by:

```http
GET /api/boxes/available
```

---

## Transaction Reference

Each box has a unique `txref` used as its business/reference identifier.

Example:

```text
BOX001
BOX002
BOX003
```

The database entity maintains both:

```text
id    → internal database identifier
txref → business/reference identifier
```

`txref` is unique and limited to a maximum of 20 characters.

---

# ✅ Validation

The application uses Jakarta Bean Validation for request-level constraints.

### Box Validation

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

### Item Validation

Item names support:

```text
letters
numbers
-
_
```

Item codes support:

```text
uppercase letters
numbers
_
```

Business rules such as battery eligibility, box state, and total weight are enforced within the service layer.

---

# API Documentation

## 1. Create a Box

### `POST /api/boxes`

Creates a new delivery box.

### Request

```json
{
  "txref": "BOX003",
  "weightLimit": 500,
  "batteryCapacity": 80
}
```

### Response

```json
{
  "id": 3,
  "txref": "BOX003",
  "weightLimit": 500,
  "batteryCapacity": 80,
  "state": "IDLE"
}
```

---

# 2. Load Items Into a Box

### `POST /api/boxes/{txref}/items`

Loads one or more items into the specified box.

The service validates:

1. Box existence
2. Battery level
3. Box state
4. Item data
5. Total item weight
6. Box capacity

### Request

```json
[
  {
    "name": "Laptop",
    "weight": 200,
    "code": "ITEM001"
  },
  {
    "name": "Mouse",
    "weight": 50,
    "code": "ITEM002"
  }
]
```

### Successful Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "weight": 200,
    "code": "ITEM001"
  },
  {
    "id": 2,
    "name": "Mouse",
    "weight": 50,
    "code": "ITEM002"
  }
]
```

---

# 3. Get Loaded Items

### `GET /api/boxes/{txref}/items`

Returns the items currently associated with a box.

### Example

```http
GET /api/boxes/BOX001/items
```

### Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "weight": 200,
    "code": "ITEM001"
  },
  {
    "id": 2,
    "name": "Mouse",
    "weight": 50,
    "code": "ITEM002"
  }
]
```

---

# 4. Get Available Boxes

### `GET /api/boxes/available`

Returns boxes currently eligible for loading.

### Example

```http
GET /api/boxes/available
```

A box must satisfy:

```text
IDLE
+
battery >= 25%
```

---

# 5. Get Battery Capacity

### `GET /api/boxes/{txref}/battery`

Returns the current battery capacity of a box.

### Example

```http
GET /api/boxes/BOX001/battery
```

### Response

```json
{
  "txref": "BOX001",
  "batteryCapacity": 85
}
```

---

# 🚨 Error Handling

The application uses domain-specific exceptions instead of generic runtime errors.

Examples include:

```text
BoxNotFoundException
BoxAlreadyExistsException
InvalidBoxStateException
InsufficientBatteryException
InsufficientCapacityException
```

This allows the API to communicate failures clearly and provides a foundation for centralized exception handling.

Example error:

```json
{
  "timestamp": "2026-08-19T10:30:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Box battery must be at least 25%",
  "path": "/api/boxes/BOX001/items"
}
```

---

# Testing

Automated tests are included to verify both successful operations and business-rule failures.

The current implementation has:

```text
16 tests
16 passing
0 failures
```

Run the complete test suite with:

```bash
./mvnw clean test
```

Expected result:

```text
Tests run: 16
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

### Test Areas

The test suite covers scenarios including:

* Box creation
* Duplicate transaction references
* Box validation
* Item validation
* Successful item loading
* Weight-capacity enforcement
* Battery threshold enforcement
* Box state validation
* Available box retrieval
* Loaded item retrieval
* Battery retrieval

---

# ▶️ Quick Start

## Prerequisites

Make sure you have:

* Java 21+
* Git
* Maven, or use the included Maven Wrapper

Verify Java:

```bash
java -version
```

---

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd box-delivery-service
```

---

## 2. Run Tests

```bash
./mvnw clean test
```

---

## 3. Start the Application

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

---

# 🔎 API Testing With cURL

The following examples can be used to manually verify the running API.

## Create a Box

```bash
curl -X POST http://localhost:8081/api/boxes \
  -H "Content-Type: application/json" \
  -d '{
    "txref": "BOX003",
    "weightLimit": 500,
    "batteryCapacity": 80
  }'
```

## Get Available Boxes

```bash
curl http://localhost:8081/api/boxes/available
```

## Get Battery Capacity

```bash
curl http://localhost:8081/api/boxes/BOX001/battery
```

## Load Items

```bash
curl -X POST http://localhost:8081/api/boxes/BOX001/items \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "Laptop",
      "weight": 200,
      "code": "ITEM001"
    },
    {
      "name": "Mouse",
      "weight": 50,
      "code": "ITEM002"
    }
  ]'
```

## Get Loaded Items

```bash
curl http://localhost:8081/api/boxes/BOX001/items
```

---

# Database

The application uses **H2** as a lightweight relational database for the assessment.

This keeps the project:

* Self-contained
* Easy to run
* Reproducible
* Free from external database setup

Spring Data JPA provides the persistence abstraction, making the persistence layer easier to evolve toward a production database such as PostgreSQL if required.

---

# Seed Data

The application includes initial box data to make the API immediately testable after startup.

Example seeded scenarios include:

```text
BOX001
├── Weight limit: 500g
├── Battery: 85%
└── State: IDLE

BOX002
├── Weight limit: 400g
├── Battery: 60%
└── State: IDLE

BOX003
├── Weight limit: 300g
├── Battery: 20%
└── State: IDLE

BOX004
├── Weight limit: 500g
├── Battery: 90%
└── State: LOADED
```

This provides useful scenarios for testing both available and unavailable boxes.

---

# Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ipaye/
│   │           └── box_delivery_service/
│   │               │
│   │               ├── controller/
│   │               │   └── BoxController.java
│   │               │
│   │               ├── service/
│   │               │   ├── BoxService.java
│   │               │   └── BoxServiceImpl.java
│   │               │
│   │               ├── repository/
│   │               │   ├── BoxRepository.java
│   │               │   └── ItemRepository.java
│   │               │
│   │               ├── entity/
│   │               │   ├── Box.java
│   │               │   └── Item.java
│   │               │
│   │               ├── dto/
│   │               │   ├── CreateBoxRequest.java
│   │               │   ├── ItemRequest.java
│   │               │   ├── BoxResponse.java
│   │               │   ├── ItemResponse.java
│   │               │   ├── BatteryResponse.java
│   │               │   └── ErrorResponse.java
│   │               │
│   │               ├── enums/
│   │               │   └── BoxState.java
│   │               │
│   │               ├── exception/
│   │               │   ├── BoxNotFoundException.java
│   │               │   ├── BoxAlreadyExistsException.java
│   │               │   ├── InvalidBoxStateException.java
│   │               │   ├── InsufficientBatteryException.java
│   │               │   ├── InsufficientCapacityException.java
│   │               │   └── GlobalExceptionHandler.java
│   │               │
│   │               └── BoxDeliveryServiceApplication.java
│   │
│   └── resources/
│       └── application.yml
│
└── test/
    └── java/
        └── com/
            └── ipaye/
                └── box_delivery_service/
```

---

# Design Decisions & Assumptions

The assessment allows reasonable assumptions, so the following design decisions were made deliberately.

### Unique Transaction Reference

`txref` is treated as the business/reference identifier for a box and is unique.

### Weight Limit

A box cannot have a maximum weight capacity greater than **500g**.

### Battery Range

Battery capacity is represented as a percentage between:

```text
0% ─────────────── 100%
```

### Minimum Loading Battery

The minimum battery threshold for loading is:

```text
25%
```

### Loading State

A box must be in an appropriate state before accepting items. The current loading workflow uses `IDLE` as the eligible starting state.

### Atomic Loading

Loading items is treated as a single transactional operation. If validation fails, the operation should not leave the database in a partially updated state.

### Physical Hardware

Communication with the physical delivery box is outside the scope of this backend assessment.

### Battery Consumption

The assessment defines the minimum battery threshold but does not provide a specific formula for battery consumption during loading.

Therefore, the implementation does **not invent an arbitrary battery-consumption formula**. It enforces the explicitly defined battery eligibility rule.

---

# Validation vs Business Logic

A deliberate distinction is maintained between input validation and business logic.

### Request Validation

Handled using Jakarta Bean Validation:

```text
Required fields
String length
Numeric ranges
String patterns
Positive values
```

### Business Rules

Handled within the service layer:

```text
Does the box exist?
Is the box available for loading?
Is the battery at least 25%?
Will the items exceed the box capacity?
Is the box in the correct state?
Does the transaction reference already exist?
```

This separation keeps controllers lightweight and makes the business logic easier to test independently.

---

# 🔐 Transactional Consistency

The loading operation is treated as a transactional business operation.

Conceptually:

```text
Find Box
    ↓
Validate Battery
    ↓
Validate State
    ↓
Calculate Weight
    ↓
Validate Capacity
    ↓
Create Items
    ↓
Persist Items
    ↓
Update Box State
```

If a business rule fails during the operation, the transaction should not leave partially persisted loading data.

---

# Future Improvements

If this service were developed beyond the assessment scope, potential improvements could include:

* PostgreSQL production profile
* Flyway or Liquibase database migrations
* OpenAPI/Swagger documentation
* Docker containerization
* Integration testing with Testcontainers
* CI/CD pipeline
* Structured application logging
* Metrics and health monitoring
* Authentication and authorization
* Pagination for larger datasets
* Integration with physical delivery hardware
* Complete delivery and return workflows

These are intentionally outside the current assessment scope to keep the implementation focused and maintainable.

---

# Engineering Focus

This project demonstrates the ability to translate business requirements into a structured backend service.

The implementation focuses on:

```text
Requirements
     ↓
Domain Modeling
     ↓
Validation
     ↓
Business Rules
     ↓
Persistence
     ↓
REST API
     ↓
Automated Testing
     ↓
Documentation
```

Rather than introducing unnecessary distributed infrastructure, the application focuses on implementing the requested business capabilities correctly and providing a clean foundation for future expansion.

---

# 💼 What This Project Demonstrates

### Backend Engineering

* Java 21
* Spring Boot
* REST API development
* Spring Data JPA
* Relational persistence

### Software Design

* Layered architecture
* Separation of concerns
* DTO-based API contracts
* Service-oriented business logic
* Repository abstraction
* Domain-specific exceptions

### Quality

* Bean validation
* Business-rule validation
* Transaction management
* Automated testing
* Centralized error handling

### Engineering Judgment

* Explicit design assumptions
* Minimal unnecessary complexity
* Clear API contracts
* Reproducible local setup
* Focus on maintainability and testability

---

# Author

## Olabowale Babatunde Ipaye

**Backend Software Engineer**

Interested in building reliable, maintainable backend systems with:

```text
Java
Spring Boot
REST APIs
SQL
Database Design
Software Architecture
Testing
Backend Engineering
```

---

# Final Notes

This project was developed as a technical assessment with an emphasis on **correctness, clean architecture, business-rule enforcement, automated testing, and maintainability**.

The implementation intentionally stays within the assessment scope while providing a foundation that can be extended into a production-grade delivery management service.

---

## 📄 License

This project was developed for technical assessment purposes (Polaris Digitech Limited)
