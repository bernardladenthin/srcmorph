### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:19:42Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds the Orders Backoffice Java application and packages it into a production-oriented runtime image.

#### Purpose
- Compiles `orders-backoffice.jar` with Maven, then runs it on a lightweight Java 21 runtime.
- Configures the container to run as a non-root user and expose application health at `/health/live`.

#### Type
- Multi-stage Dockerfile.

#### Definitions
- `build` stage: base image `maven:3.9-eclipse-temurin-21`; working directory `/src`.
  - Copies `pom.xml` to `/src/pom.xml`.
  - Runs `mvn -B dependency:go-offline`.
  - Copies `src` to `/src/src`.
  - Runs `mvn -B package -DskipTests`.
- Final stage: base image `eclipse-temurin:21-jre-alpine`.
  - Creates group `shop` and user `backoffice`, a member of `shop`.
  - Copies `/src/target/orders-backoffice.jar` from `build` to `/opt/shop/orders-backoffice.jar`.
  - Exposes port `8080`; runs as `backoffice`.
  - Healthcheck: every `30s`, runs `wget -qO- http://localhost:8080/health/live || exit 1`.
  - Entrypoint: `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`.

#### Configuration
- `APP_VERSION=2.1.0` build argument.
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`.
- `SHOP_PROFILE=production`.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`.
- Runtime artifact: `/src/target/orders-backoffice.jar`.
