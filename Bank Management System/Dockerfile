# ==================== DOCKERFILE FOR BACKEND ====================
# Build stage
FROM maven:3.8.1-openjdk-17-slim as builder
WORKDIR /build

# Copy pom.xml
COPY backend/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY backend/src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/auth/customer/login || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
