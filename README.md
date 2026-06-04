# Meal Plan Application Backend

A production-grade, clean-layered Spring Boot MVP backend for a Meal Plan tracking application. Users can plan their meals by date, track food items (entries), view daily/weekly completion progress, and secure their profiles with JWT Authentication.

---

## 🚀 Tech Stack

- **Java 17** & **Spring Boot 3.x**
- **Maven** for build management
- **Spring Security** & **JWT Authentication** for stateless security
- **Spring Data JPA** for ORM mapping
- **Flyway Migration** for database schema management
- **MySQL 8** for data persistence
- **Redis 7** for session & dashboard caching
- **Bean Validation** (Hibernate Validator)
- **Lombok** to eliminate boilerplate
- **Swagger UI / SpringDoc OpenAPI** for API documentation
- **Docker & Docker Compose** for containerization
- **JUnit 5 & Mockito** for testing

---

## 📂 Architecture Overview

The codebase is built as a modular monolith following a layered architecture:
- **controller**: Thin API endpoint layer.
- **service**: Contains all core business logic and transactional boundaries.
- **repository**: Handles standard DB query operations.
- **entity**: Maps database rows to rich domain model objects.
- **dto**: Ensures encapsulation of JPA models and handles input validations.
- **security**: Manages JWT token generation, parsing, filter context, and CORS/CSRF configurations.
- **config**: Manages caching definitions, Redis managers, and audit/JPA behaviors.

---

## 🔑 Environment Variables

| Variable Name | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL Server host name |
| `DB_PORT` | `3306` | MySQL Server port |
| `DB_NAME` | `meal_plan_db` | Target database name |
| `DB_USERNAME` | `mealplan_user` | MySQL login user |
| `DB_PASSWORD` | `mealplan_password` | MySQL login password |
| `REDIS_HOST` | `localhost` | Redis Server host name |
| `REDIS_PORT` | `6379` | Redis Server port |
| `JWT_SECRET` | `change-this-secret-key-to-a-secure-secret-and-make-it-long-enough-for-hs256-algorithms-to-work-properly-with-spring-security` | Signing key for JWTs |
| `JWT_EXPIRATION` | `86400000` | Token expiration duration (in ms) |

---

## 🛠️ How to Run

### Prerequisite
Ensure you have **Java 17**, **Maven**, and **Docker** installed.

### Run Locally (Development)
1. Boot a MySQL database (matching application configuration credentials).
2. Start the Spring Boot backend:
   ```bash
   mvn spring-boot:run
   ```

### Run using Docker Compose
Spin up the MySQL container and Spring Boot application together:
```bash
docker-compose up --build
```
The app will wait for the MySQL container healthcheck to succeed before starting on port `8080`.

---

## 📝 API Endpoint Reference & Swagger UI

- **Swagger UI URL**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Auth APIs
- `POST /api/v1/auth/register` (Public) - Create user
- `POST /api/v1/auth/login` (Public) - User login, returns JWT token

### Meal Plan APIs (Protected)
- `POST /api/v1/meal-plans` - Create a meal plan for a specific date
- `GET /api/v1/meal-plans/date/{date}` - Fetch meal plan details and all its meal entries
- `GET /api/v1/meal-plans` - Get meal plans (paginated & filterable by `startDate` / `endDate`)
- `PUT /api/v1/meal-plans/{id}` - Update meal plan title/notes/date
- `DELETE /api/v1/meal-plans/{id}` - Delete meal plan and all nested entries

### Meal Entry APIs (Protected)
- `POST /api/v1/meal-plans/{mealPlanId}/entries` - Add meal item to a plan
- `PUT /api/v1/meal-entries/{id}` - Edit details (calories, target time, etc.) of an entry
- `PATCH /api/v1/meal-entries/{id}/status` - Quick update for status (`PENDING`, `COMPLETED`, `SKIPPED`)
- `DELETE /api/v1/meal-entries/{id}` - Remove a meal entry

### Dashboard APIs (Protected)
- `GET /api/v1/dashboard/daily?date=yyyy-MM-dd` - Fetch completion rate statistics for a single day
- `GET /api/v1/dashboard/weekly?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd` - Fetch completion stats across a date range

---

## 🧪 Running Tests

To run the suite of unit and integration tests:
```bash
mvn clean test
```
