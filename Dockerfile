# Multi-stage build for Java 21 application

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM gcr.io/distroless/java21-debian12:nonroot

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for Virtual Threads and production
ENV JAVA_OPTS="-XX:+UseZGC \
    -XX:+UseStringDeduplication \
    -Xms512m \
    -Xmx1g \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof"

# Run as non-root user (distroless uses user 65532)
USER 65532:65532

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]



