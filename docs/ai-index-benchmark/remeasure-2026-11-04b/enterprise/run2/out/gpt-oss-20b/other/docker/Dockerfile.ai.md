### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:32:39Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds and runs the `orders-backoffice` Java application in a Docker container for the shop backend.

#### Purpose
- Compile the Java project with Maven.
- Package the application into a JAR.
- Run the JAR in a lightweight Alpine JRE container.

#### Type
- Dockerfile  
  Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`  
  Build stage: `build`  
  Runtime stage: default

#### Definitions
- **Stages**  
  - `build`: `maven:3.9-eclipse-temurin-21`  
  - default: `eclipse-temurin:21-jre-alpine`
- **Build commands**  
  - `RUN mvn -B dependency:go-offline`  
  - `RUN mvn -B package -DskipTests`
- **Runtime commands**  
  - `RUN addgroup -S shop && adduser -S backoffice -G shop`  
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
- Maven dependencies resolved in build stage (`dependency:go-offline`).
- Runtime JRE: `eclipse-temurin:21-jre-alpine`.
- Application JAR: `orders-backoffice.jar` (built from `pom.xml` and `src/`).
- Health endpoint: `http://localhost:8080/health/live`.
