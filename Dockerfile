FROM gradle:7.6.0-jdk23 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:23-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]