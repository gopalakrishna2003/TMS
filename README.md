# Transport Management System (TMS) — Backend

> A Spring Boot + PostgreSQL backend for managing logistics loads, transporter bids, and truck bookings with real-world business rules and concurrency handling.
>
> Built as a self-learning project to practice Java, Spring Boot, JPA, and backend system design with complex business logic.

---

## 📋 Table of Contents

- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Business Rules](#business-rules)
- [Project Structure](#project-structure)
- [Exception Handling](#exception-handling)
- [Test Coverage](#test-coverage)
- [What I Learned](#what-i-learned)

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL 15**
- **SpringDoc OpenAPI** (Swagger UI)
- **JUnit 5** + **MockMvc**
- **Lombok**

---

## Database Schema

```
┌──────────────────────────────────────┐
│                LOAD                  │
│──────────────────────────────────────│
│ loadId (PK, UUID)                    │
│ shipperId                            │
│ loadingCity / unloadingCity          │
│ loadingDate / datePosted             │
│ productType / truckType              │
│ weight / weightUnit (KG|TON)         │
│ noOfTrucks / remainingTrucks         │
│ status (POSTED|OPEN_FOR_BIDS|        │
│         BOOKED|CANCELLED)            │
│ version  ← optimistic locking        │
└──────────────┬───────────────────────┘
               │ 1:N
┌──────────────▼───────────────────────┐
│                BID                   │
│──────────────────────────────────────│
│ bidId (PK, UUID)                     │
│ loadId (FK → LOAD)                   │
│ transporterId (FK → TRANSPORTER)     │
│ proposedRate / trucksOffered         │
│ status (PENDING|ACCEPTED|REJECTED)   │
│ submittedAt                          │
└──────────────┬───────────────────────┘
               │ 1:1
┌──────────────▼───────────────────────┐
│              BOOKING                 │
│──────────────────────────────────────│
│ bookingId (PK, UUID)                 │
│ loadId (FK → LOAD)                   │
│ bidId (FK → BID, UNIQUE)             │
│ transporterId (FK → TRANSPORTER)     │
│ allocatedTrucks / finalRate          │
│ status (CONFIRMED|COMPLETED|         │
│         CANCELLED)                   │
│ bookedAt                             │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│           TRANSPORTER                │
│──────────────────────────────────────│
│ transporterId (PK, UUID)             │
│ companyName                          │
│ rating (1–5)                         │
└──────────────┬───────────────────────┘
               │ 1:N
┌──────────────▼───────────────────────┐
│          TRUCK_CAPACITY              │
│──────────────────────────────────────│
│ id (PK)                              │
│ transporterId (FK → TRANSPORTER)     │
│ truckType                            │
│ count                                │
└──────────────────────────────────────┘
```

**Constraints & Indexes:**
- `UNIQUE` on `booking.bid_id` (one accepted bid per booking)
- `INDEX` on `bid(load_id)`, `bid(transporter_id)`, `bid(status)`
- `@Version` column on `Load` entity for optimistic locking

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 15+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/tms-backend.git
   cd tms-backend
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE tms_db;
   ```

3. **Configure credentials** in `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/tms_db
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Open Swagger UI**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## API Documentation

**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

### Quick API Reference

#### Load APIs (5)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/load` | Create a new load (status = POSTED) |
| GET | `/load?shipperId=&status=&page=0&size=10` | List loads with pagination |
| GET | `/load/{loadId}` | Get load with its active bids |
| PATCH | `/load/{loadId}/cancel` | Cancel a load (validates status) |
| GET | `/load/{loadId}/best-bids` | Get scored bid suggestions |

#### Transporter APIs (3)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transporter` | Register transporter with truck inventory |
| GET | `/transporter/{transporterId}` | Get transporter details |
| PUT | `/transporter/{transporterId}/trucks` | Update truck availability |

#### Bid APIs (4)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/bid` | Submit a bid (validates capacity and load status) |
| GET | `/bid?loadId=&transporterId=&status=` | Filter bids |
| GET | `/bid/{bidId}` | Get bid details |
| PATCH | `/bid/{bidId}/reject` | Reject a bid |

#### Booking APIs (3)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/booking` | Accept bid & create booking (with concurrency control) |
| GET | `/booking/{bookingId}` | Get booking details |
| PATCH | `/booking/{bookingId}/cancel` | Cancel booking (restores trucks, updates load) |

---

## Business Rules

This project implements several non-trivial logistics rules that were the core learning focus.

### Rule 1 — Capacity Validation
- A transporter can only bid if `trucksOffered ≤ availableTrucks` for the required truck type.
- When a booking is confirmed, `allocatedTrucks` is deducted from the transporter's pool.
- When a booking is cancelled, trucks are restored to the available pool.

### Rule 2 — Load Status Transitions
```
POSTED ──(first bid received)──► OPEN_FOR_BIDS
OPEN_FOR_BIDS ──(bid accepted)──► BOOKED  (when remainingTrucks == 0)
BOOKED ──(booking cancelled)──► OPEN_FOR_BIDS
* Cannot bid on CANCELLED or BOOKED loads
* Cannot cancel a load that is already BOOKED
```

### Rule 3 — Multi-Truck Allocation
- If `noOfTrucks > 1`, multiple bookings are allowed until fully allocated.
- `remainingTrucks = noOfTrucks - SUM(allocatedTrucks)`
- Load becomes `BOOKED` only when `remainingTrucks == 0`.

### Rule 4 — Concurrent Booking Prevention
- `@Version` optimistic locking on the `Load` entity prevents double-booking.
- If two transporters attempt to book simultaneously, the first transaction wins; the second receives a `409 Conflict`.

### Rule 5 — Best Bid Scoring
```
score = (1 / proposedRate) × 0.7 + (rating / 5) × 0.3
```
Higher score = better bid. A lower rate and a higher transporter rating both improve the score.

---

## Project Structure

```
src/
├── main/java/com/yourname/tms/
│   ├── controller/         # REST controllers (thin — no business logic)
│   ├── service/            # Business rules and transactions
│   ├── repository/         # Spring Data JPA interfaces
│   ├── entity/             # JPA entities + enums
│   ├── dto/
│   │   ├── request/        # Incoming request bodies
│   │   └── response/       # Outgoing response models
│   └── exception/          # Custom exceptions + @ControllerAdvice handler
└── test/                   # Unit + integration tests
```

**Architecture:** `Controller → DTO → Service → Repository → Entity`

---

## Exception Handling

All errors return a consistent JSON structure:

```json
{
  "error": "INVALID_STATUS",
  "message": "Cannot bid on load with status: BOOKED"
}
```

| Exception | HTTP Status |
|-----------|-------------|
| `ResourceNotFoundException` | 404 |
| `InvalidStatusTransitionException` | 400 |
| `InsufficientCapacityException` | 400 |
| `LoadAlreadyBookedException` | 409 |
| Validation errors | 400 |

---

## Test Coverage

> *Add a screenshot of your test coverage report here — IntelliJ: Run with Coverage, or run `mvn jacoco:report` and open `target/site/jacoco/index.html`*

Key scenarios tested:
- Create load → status is `POSTED`
- First bid → load transitions to `OPEN_FOR_BIDS`
- Bid on cancelled/booked load → `400` error
- Bid exceeding truck capacity → `400` error
- Accept bid → trucks deducted from transporter
- Multi-truck partial allocation → load stays `OPEN_FOR_BIDS`
- Concurrent booking → one succeeds (`201`), one gets `409 Conflict`
- Cancel booking → trucks restored, load re-opened

---

## What I Learned

- Designing normalized relational schemas with proper constraints and indexes
- Implementing complex state machine logic (Load status transitions)
- Handling concurrency with JPA optimistic locking (`@Version`)
- Building a clean layered architecture: Controller → Service → Repository
- Writing integration tests with MockMvc for REST APIs
- Global exception handling with `@ControllerAdvice`
