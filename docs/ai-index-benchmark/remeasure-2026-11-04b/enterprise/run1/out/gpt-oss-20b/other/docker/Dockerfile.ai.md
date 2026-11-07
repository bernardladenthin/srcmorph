### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:26:00Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds a Java back‑office application, packages it with Maven, and runs it in an Alpine JRE container exposing port 8080 with a health check.

#### Purpose
- Compile and package the `orders-backoffice` Java application.
- Deploy the resulting JAR as a containerized service.

#### Type
- `Dockerfile`  
  Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`  
  Build stage: `build`  
  Runtime stage: default

#### Definitions
- **Stages**  
  - `build`: uses `maven:3.9-eclipse-temurin-21`  
  - default: uses `eclipse-temurin:21-jre-alpine`
- **Build commands**  
  - `mvn -B dependency:go-offline`  
  - `mvn -B package -DskipTests`
- **Runtime commands**  
  - `addgroup -S shop && adduser -S backoffice -G shop`  
  - `COPY --from=build /src/target/orders-backoffice.jar /opt/shop/orders-backoffice.jar`  
  - `EXPOSE 8080`  
  - `USER backoffice`  
  - `HEALTHCHECK --interval=30s CMD wget -qO- http://localhost:8080/health/live || exit 1`  
  - `ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"]`

#### Configuration
- `ARG APP_VERSION=2.1.0`
- `ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"`
- `ENV SHOP_PROFILE=production`

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`  
- Maven project `pom.xml` and source `src/`  
- JAR produced: `orders-backoffice.jar` (copied from build stage)
