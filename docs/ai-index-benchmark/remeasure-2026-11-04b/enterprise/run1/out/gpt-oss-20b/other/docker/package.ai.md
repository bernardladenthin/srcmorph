### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:27:09Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> A Dockerfile that builds and deploys a Java back‑office service for order management.

#### Purpose
- Compile the `orders-backoffice` Java application with Maven.
- Package the resulting JAR into a lightweight Alpine JRE container.

#### Responsibilities
- **Build stage**: resolve dependencies, compile, and package the application.
- **Runtime stage**: set up a non‑root user, expose service port, provide health check, and launch the JAR.

#### Key units
- `orders-backoffice.jar` – the compiled Spring Boot application.
- `mvn -B dependency:go-offline` – pre‑fetch Maven dependencies.
- `mvn -B package -DskipTests` – build the JAR.
- `HEALTHCHECK` – verifies `/health/live` endpoint.
- `ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"]` – starts the application.

#### Data flow
Source code → Maven build → `orders-backoffice.jar` → copied to runtime image → container starts JAR → exposes port 8080 → health check monitors `/health/live`.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21` (build), `eclipse-temurin:21-jre-alpine` (runtime).
- Maven project files (`pom.xml`, `src/`).
- Environment variables: `JAVA_OPTS`, `SHOP_PROFILE`.

#### Cross-cutting
- Uses multi‑stage Docker build to keep runtime image small.
- Non‑root user (`backoffice`) for security.
- Health check ensures service readiness before traffic routing.
