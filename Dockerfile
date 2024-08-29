FROM gradle:8.9-jdk17 AS builder

WORKDIR /app

COPY . .

RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8000

CMD ["java", "-jar", "/app/app.jar"]
