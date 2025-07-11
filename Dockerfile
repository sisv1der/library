FROM eclipse-temurin:24-jdk-alpine
LABEL authors="jarigo"

WORKDIR /app

COPY target/library-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]