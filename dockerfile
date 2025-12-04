# ===========================
# Stage 1 — Build the project
# ===========================
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build Spring Boot fat JAR with proper manifest
RUN mvn clean package spring-boot:repackage -DskipTests

# ===========================
# Stage 2 — Run the project
# ===========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
ENV MONGODB_URI="mongodb+srv://rudreshcsr:DNHYb15BHSN3Lo0Q@cluster0.shfwcil.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
