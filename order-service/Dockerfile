# Stage 1: Runtime
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the fat JAR using a wildcard for the version
# Renamed to a more descriptive name as requested
COPY target/order-service-*.jar order-processing-application.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "order-processing-application.jar"]
