### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:10:22Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds a Java back‑office application, packages it with Maven, and runs it in an Alpine JRE container exposing port 8080.

#### Purpose
- Compile and package a Maven project into a JAR.
- Deploy the JAR in a lightweight Java runtime.
- Expose a health endpoint and run the application with configurable JVM options.

#### Type
- `Dockerfile`  
  Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`.

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

#### Dependencies
- Maven build image: `maven:3.9-eclipse-temurin-21`
- Runtime image: `eclipse-temurin:21-jre-alpine`
- JAR artifact: `orders-backoffice.jar` (built in `/src/target/`)
