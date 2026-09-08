# Stage 1: Build Stage
# Usa como base la imagen nexo-core-m2 (construida por core-builder) que ya
# contiene nexo-core-spring instalado en /root/.m2/repository, evitando el token de GitHub.
FROM nexo-core-m2:latest AS builder
WORKDIR /app

COPY . .

# Se elimina .mvn para no depender de credenciales de GitHub (core se resuelve
# del repositorio local heredado de la imagen base).
RUN rm -rf .mvn \
    && mvn -Dmaven.repo.local=/root/.m2/repository -DskipTests clean package

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8085

ENV SERVER_PORT=8085

ENTRYPOINT ["java", "-jar", "app.jar"]
