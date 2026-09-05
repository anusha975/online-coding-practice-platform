# -----------------------------------------------------------------------------
# Stage 1: Build the Spring Boot application using Maven from repo root
# -----------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy backend pom and source code
COPY backend/pom.xml ./
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Production runtime image with Java 17, Python 3, and GCC/G++
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install Python 3 and GCC/G++ for the Judge Execution Engine
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    g++ \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for embedded database persistence
RUN mkdir -p /app/data

# Expose port
EXPOSE 8080

# Environment defaults
ENV SPRING_PROFILES_ACTIVE=h2
ENV PORT=8080

# Use shell form so ${PORT} environment variable is properly evaluated by Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
