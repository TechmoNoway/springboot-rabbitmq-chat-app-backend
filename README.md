# Civic Messager Backend

A Spring Boot backend for a real-time chat application using RabbitMQ, JWT authentication, and Redis caching. This project provides REST APIs and WebSocket endpoints for user authentication, messaging, and friend management.

## Features
- User registration and login (including Google OAuth)
- JWT-based authentication and refresh tokens
- Real-time messaging with RabbitMQ
- Friend request and management system
- Redis caching for performance
- RESTful APIs for chat, user, and friend operations
- WebSocket support for live chat

## Technologies Used
- Java 17+
- Spring Boot
- RabbitMQ
- Redis
- JWT
- Gradle
- Docker support

## Getting Started

### Prerequisites
- Java 17 or higher
- Gradle
- RabbitMQ server
- Redis server

### Setup
1. Clone the repository:
   ```bash
   git clone <repo-url>
   ```
2. Configure environment variables in `src/main/resources/app.env` and application YAML files.
3. Start RabbitMQ and Redis servers.
4. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```
5. (Optional) Use Docker Compose:
   ```bash
   docker compose up
   ```

## API Endpoints
- `/api/auth` - Authentication APIs
- `/api/user` - User management APIs
- `/api/friend` - Friend management APIs
- `/api/message` - Messaging APIs

## License
MIT

## Author
trickynguci

