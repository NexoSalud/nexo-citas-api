# Stage 1: Build Stage
# Construcción autocontenida para Coolify. No depende de imágenes Maven previas.
# La dependencia nexo-core-spring se resuelve desde GitHub Packages usando
# GITHUB_ACTOR/GITHUB_TOKEN (build args de Coolify).
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Credenciales de GitHub Packages. Coolify debe proporcionarlas como build args,
# no solo como variables de entorno del contenedor.
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

COPY settings.xml pom.xml ./
COPY src ./src
RUN test -n "$GITHUB_ACTOR" || (echo "ERROR: falta el build arg GITHUB_ACTOR" >&2; exit 1)
RUN test -n "$GITHUB_TOKEN" || (echo "ERROR: falta el build arg GITHUB_TOKEN" >&2; exit 1)
RUN GITHUB_ACTOR="$GITHUB_ACTOR" GITHUB_TOKEN="$GITHUB_TOKEN" \
    mvn -s settings.xml -B -DskipTests clean package

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Herramientas de diagnóstico/healthcheck
RUN apk add --no-cache curl tzdata

COPY --from=builder /app/target/*.jar app.jar

# JVM optimizada para contenedores (límites de cgroup automáticos)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8085

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]