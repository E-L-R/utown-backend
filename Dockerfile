# syntax=docker/dockerfile:1

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and POM first for layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build the production artifact
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal runtime environment
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Run as an unprivileged service account for security (UID 1000 for standard container & cloud runner compatibility)
RUN addgroup -g 1000 utown && adduser -u 1000 -G utown -s /bin/sh -D utown
USER utown:utown

# Copy the packaged jar from the builder stage
COPY --from=builder --chown=utown:utown /app/target/*.jar app.jar

EXPOSE 7860 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
