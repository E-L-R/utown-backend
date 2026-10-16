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

# Run as an unprivileged service account for security
RUN addgroup -S utown && adduser -S utown -G utown
USER utown:utown

# Copy the packaged jar from the builder stage
COPY --from=builder --chown=utown:utown /app/target/*.jar app.jar

EXPOSE 8080

# Faster cold starts on a small, shared CPU (Render free tier): C1-only JIT and the serial GC
# trade a little peak throughput for noticeably quicker boot and a smaller memory footprint.
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
