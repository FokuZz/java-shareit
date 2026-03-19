# 🤝 ShareIt

### Backend for a peer-to-peer item sharing platform.

## Architecture — 3 microservices:

### 🚪 Gateway
Accepts all incoming requests and handles basic validation (no business logic).
Valid requests are forwarded to the server; responses are passed back as-is.

### 💻 Server
Receives requests from the Gateway, executes business logic, communicates with the database, and returns results.

### 💾 Database
Stores all application data.

---

## ⭐ Features

- Users can post requests for items they need
- Users can list their own items for sharing — in response to a request or independently
- Items can be booked for a specific time period, with the following rules enforced:
  - No overlapping bookings
  - No backdated bookings
  - End date must be after start date
- Users can view their listed items along with the last and next booking for each
- Users can view their bookings filtered by status:
  - `ALL` — all bookings
  - `CURRENT` — active bookings
  - `PAST` — completed bookings
  - `FUTURE` — upcoming bookings
  - `WAITING` — pending owner approval
  - `REJECTED` — declined bookings
- After a booking ends, users can leave a review on the item

---

## ⚙️ Stack
Java 11 · Spring Boot · Hibernate · PostgreSQL · Docker Compose · Maven · JUnit · Mockito · Lombok

---

## 🚀 Running locally

1. Clone the repository
2. Make sure port `8080` is free
3. Build:
```shell
docker compose build
```
4. Run:
```shell
docker compose up
```
