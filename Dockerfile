FROM rabbitmq:4.2-management-alpine AS rabbitmq
RUN rabbitmq-plugins enable --offline rabbitmq_stomp rabbitmq_web_stomp rabbitmq_management

FROM eclipse-temurin:26-jdk-alpine AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:26-jre-alpine AS application
RUN addgroup -S lynqo && adduser -S lynqo -G lynqo
WORKDIR /app
COPY --from=build /workspace/build/libs/lynqo-backend.jar app.jar
USER lynqo
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
