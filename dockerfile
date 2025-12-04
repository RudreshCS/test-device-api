# ===========================
# Stage 1 - Build the project
# ===========================
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build Spring Boot fat JAR with proper manifest
RUN mvn clean package spring-boot:repackage -DskipTests

# ===========================
# Stage 2 - Run the project
# ===========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV PORT=8080
ENV MONGODB_URI="mongodb+srv://rudreshcsr:DNHYb15BHSN3Lo0Q@cluster0.shfwcil.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"

# JVM configuration for cloud deployment and AWS IoT MQTT connections
ENV JAVA_OPTS="-server \
	-Xss2m \
	-Xms256m \
	-Xmx512m \
	-XX:+UseContainerSupport \
	-XX:MaxRAMPercentage=75.0 \
	-XX:+UseG1GC \
	-XX:MaxGCPauseMillis=200 \
	-XX:+HeapDumpOnOutOfMemoryError \
	-XX:HeapDumpPath=/tmp/heapdump.hprof \
	-Djava.security.egd=file:/dev/./urandom \
	-Dspring.profiles.active=production"

EXPOSE 8080

# Use shell form to allow environment variable expansion
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
