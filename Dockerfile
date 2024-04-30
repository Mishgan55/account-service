FROM gradle:8.7 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src src
RUN gradle build --no-daemon
# gradle build -x test --no-daemon - for running without tests

FROM openjdk:21-jdk-slim
COPY --from=build /app/build/libs/*.jar /app/build/libs/app.jar
ENTRYPOINT ["java", "-jar", "/app/build/libs/app.jar"]