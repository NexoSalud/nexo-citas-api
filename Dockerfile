# Stage 1: Build Stage
# Construcción autocontenida para Coolify. No depende de imágenes Maven previas.
# La dependencia nexo-core-spring se resuelve desde GitHub Packages usando
# GITHUB_ACTOR/GITHUB_TOKEN (build args de Coolify).
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Credenciales de GitHub Packages (pasadas como build args en Coolify).
# Se exportan como ENV para que settings.xml los lea via ${env.GITHUB_ACTOR}.
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=${GITHUB_ACTOR} \
    GITHUB_TOKEN=${GITHUB_TOKEN}

# Cache de dependencias Maven para builds incrementales rápidos en Coolify
# (requiere BuildKit / docker-container driver de Coolify).
RUN --mount=type=cache,target=/root/.m2 mkdir -p /root/.m2/repository

COPY settings.xml pom.xml ./
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -s settings.xml -B dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
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