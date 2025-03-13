# Use official OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Copy source code
COPY src ./src

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose port (Render sets PORT env var)
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/habit-tracker-0.0.1-SNAPSHOT.jar"]