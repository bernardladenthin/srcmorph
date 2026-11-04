### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:22:59Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds a Java back‑office service, compiles it with Maven, and runs the resulting JAR in an Alpine JRE container with health checks.

#### Purpose
- Compile and package a Maven project into a JAR.
- Deploy the JAR in a lightweight Java runtime.
- Expose the service on port 8080 with health monitoring.

#### Type
- `Dockerfile`  
  Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`

#### Definitions
- **Build stage** (`build`):  
  - `WORKDIR /src`  
  - `COPY pom.xml .`  
  - `RUN mvn -B dependency:go-offline`  
  - `COPY src ./src`  
  - `RUN mvn -B package -DskipTests`  
- **Runtime stage**:  
  - `ARG APP_VERSION=2.1.0`  
  - `ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" SHOP_PROFILE=production`  
  - `RUN addgroup -S shop && adduser -S backoffice -G shop`  
  - `COPY --from=build /src/target/orders-backoffice.jar /opt/shop/orders-backoffice.jar`  
  - `EXPOSE 8080`  
  - `USER backoffice`  
  - `HEALTHCHECK --interval=30s CMD wget -qO- http://localhost:8080/health/live || exit 1`  
  - `ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"]`

#### Configuration
- `APP_VERSION=2.1.0`
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`
- `SHOP_PROFILE=production`
- `EXPOSE 8080`
- Health check URL: `http://localhost:8080/health/live`

#### Dependencies
- `maven:3.9-eclipse-temurin-21` (build image)
- `eclipse-temurin:21-jre-alpine` (runtime image)
- Maven project `pom.xml` and source `src/`
- JAR `orders-backoffice.jar` produced by Maven
- `wget` (used in health check)  
- User `backoffice` in group `shop`  
- Port `8080` exposed for the application.
