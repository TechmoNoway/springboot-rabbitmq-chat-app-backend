# Lynqo Backend

Backend for Lynqo, a real-time messaging application built with Spring Boot, RabbitMQ, Redis, and MySQL.

## Technology baseline

- Java 26
- Spring Boot 4.1.1
- Gradle 9.7.1
- MySQL 8.4
- RabbitMQ 4.2 with STOMP and Web STOMP
- Redis 7.4

## Architecture

The codebase is organized by business feature rather than by technical layer:

```text
com.lynqo.backend
├── auth/          # login, registration, Google login, tokens
├── friendship/    # friend relationships
├── messaging/     # message persistence and real-time delivery
├── presence/      # online/away/offline state in Redis
├── user/          # profiles and user lookup
└── shared/        # cross-cutting configuration and security
```

Each feature owns its API, DTOs, domain model, repositories, and services. Public REST paths remain under `/api/v1` for frontend compatibility.

## Local development

1. Copy `.env.example` to `.env` and change the development credentials.
2. Start the full stack:

   ```bash
   docker compose up --build
   ```

The backend listens on `http://localhost:8081`. Swagger UI is available at `http://localhost:8081/swagger-ui.html`.

To run with locally installed infrastructure:

```bash
./gradlew bootRun
```

Gradle automatically provisions the Java 26 toolchain when it is not installed locally.
