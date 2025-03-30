# Stage 1: Build stage
FROM maven:3-amazoncorretto-21 AS builder

# Set the working directory for the build
WORKDIR /app

# Copy the Maven configuration and project files
COPY pom.xml Dockerfile ./
# Copy the entire project into the container
COPY . .

# Run the Maven build to create the JAR file
RUN mvn clean package -DskipTests

# Stage 2: Production stage
FROM amazoncorretto:21-alpine AS production

# Install required packages including fonts
RUN apk add --no-cache \
    tzdata \
    fontconfig \
    ttf-dejavu \
    && fc-cache -f

# Set the timezone to Asia/Colombo
ENV TZ=Asia/Colombo

# Set the working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=builder /app/data-service/target/data-service-0.0.1-SNAPSHOT.jar /app/data-service.jar

# Copy any runtime-specific configuration files
COPY data-service/src/main/resources/application-prod.properties application-prod.properties
COPY data-service/src/main/resources/application-qa.properties application-qa.properties
COPY data-service/src/main/resources/application.properties application.properties

# Expose application and debug ports
EXPOSE 8080
EXPOSE 8080 5005

# Specify the command to run on container startup
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/data-service.jar"]
