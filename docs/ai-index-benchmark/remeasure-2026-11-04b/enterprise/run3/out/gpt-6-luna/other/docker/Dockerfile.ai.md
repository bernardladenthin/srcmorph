### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:37:45Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds and runs the Shop orders back-office application as a Java 21 container service on port 8080.

#### Purpose
- Packages `orders-backoffice.jar` with Maven, then runs it on a Java 21 JRE Alpine image.
- Configures a non-root runtime user and an HTTP liveness health check.

#### Type
- Dockerfile; Maven 3.9 with Eclipse Temurin 21 for building, Eclipse Temurin 21 JRE Alpine for runtime.

#### Definitions
- Build stage `build`, based on `maven:3.9-eclipse-temurin-21`:
  - `WORKDIR /src`
  - Copies `pom.xml` to `/src/pom.xml`; runs `mvn -B dependency:go-offline`.
  - Copies `src` to `/src/src`; runs `mvn -B package -DskipTests`.
- Runtime stage, based on `eclipse-temurin:21-jre-alpine`:
  - Creates group `shop` and user `backoffice` in that group.
  - Copies `/src/target/orders-backoffice.jar` to `/opt/shop/orders-backoffice.jar` from `build`.
  - Exposes port `8080`; runs as `backoffice`.
  - Health check: `wget -qO- http://localhost:8080/health/live || exit 1`, interval `30s`.
  - Entrypoint: `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`.

#### Configuration
- `ARG APP_VERSION=2.1.0`
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`
- `SHOP_PROFILE=production`

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`.
- Runtime artifact: `/src/target/orders-backoffice.jar` (copied to `/opt/shop/orders-backoffice.jar`).
