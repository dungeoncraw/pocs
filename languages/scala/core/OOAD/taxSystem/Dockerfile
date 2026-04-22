# Stage 1: Build
FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.10.1_3.4.2 AS build

WORKDIR /app
COPY . .
RUN sbt stage

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/universal/stage /app

EXPOSE 8080
ENTRYPOINT ["bin/tax-system"]
