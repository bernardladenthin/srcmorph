### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:22:31Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds the Orders Backoffice Java application and packages it as a production-oriented container serving on port 8080.

#### Purpose
- Compiles `orders-backoffice.jar` in a Maven build stage and runs it in a Java 21 JRE container.

#### Type
- Dockerfile; Maven 3.9 with Eclipse Temurin 21 for build, Eclipse Temurin 21 JRE Alpine for runtime.

#### Definitions
- `build` stage: based on `maven:3.9-eclipse-temurin-21`; works in `/src`; copies `pom.xml` and `src`; runs `mvn -B dependency:go-offline` and `mvn -B package -DskipTests`.
- Runtime stage: based on `eclipse-temurin:21-jre-alpine`; copies `/src/target/orders-backoffice.jar` to `/opt/shop/orders-backoffice.jar`.
- Creates group `shop` and user `backoffice`; runs as `backoffice`.
- Exposes port `8080`; healthcheck runs `wget -qO- http://localhost:8080/health/live || exit 1` every 30 seconds.
- Entrypoint: `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`.

#### Configuration
- `APP_VERSION=2.1.0` build argument.
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`.
- `SHOP_PROFILE=production`.
- `WORKDIR=/src`.

#### Dependencies
- Build image: `maven:3.9-eclipse-temurin-21`.
- Runtime image: `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`.
- Runtime artifact: `/src/target/orders-backoffice.jar`.
