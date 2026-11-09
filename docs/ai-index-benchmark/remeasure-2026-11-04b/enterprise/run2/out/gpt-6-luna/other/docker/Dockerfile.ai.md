### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:29:04Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds and runs the Shop orders backoffice Java application as a non-root service on port 8080.

#### Purpose
- Packages `orders-backoffice.jar` in a Maven build stage, then runs it in a lightweight Java 21 runtime image.

#### Type
- Multi-stage Dockerfile.

#### Definitions
- `build` stage: `maven:3.9-eclipse-temurin-21`; working directory `/src`.
  - Copies `pom.xml`; runs `mvn -B dependency:go-offline`.
  - Copies `src` to `/src/src`; runs `mvn -B package -DskipTests`.
- Runtime stage: `eclipse-temurin:21-jre-alpine`.
  - Creates group `shop` and user `backoffice` in that group.
  - Copies `/src/target/orders-backoffice.jar` to `/opt/shop/orders-backoffice.jar`.
  - Exposes port `8080`; runs as `backoffice`.
  - Healthcheck: every 30 seconds, `wget -qO- http://localhost:8080/health/live || exit 1`.
  - Entrypoint: `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`.

#### Configuration
- `ARG APP_VERSION=2.1.0` (declared but not otherwise used).
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`.
- `SHOP_PROFILE=production`.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`.
- Runtime artifact: `/src/target/orders-backoffice.jar`.
- Health endpoint: `http://localhost:8080/health/live`.
