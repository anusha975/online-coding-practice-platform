# -----------------------------------------------------------------------------
# Stage 1: Build the Spring Boot application using Maven
# -----------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first for efficient layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------------------
# Stage 2: Production runtime image with Java 17, Python 3, and GCC/G++
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install Python 3 and GCC/G++ for the Judge Execution Engine
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    g++ \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for embedded H2 database persistence if needed
RUN mkdir -p /app/data

# Expose default Spring Boot port
EXPOSE 8080

# Configure production environment defaults
ENV SPRING_PROFILES_ACTIVE=h2
ENV PORT=8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
