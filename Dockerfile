FROM openjdk:17-jdk-slim

WORKDIR /app

VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY .env .env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
