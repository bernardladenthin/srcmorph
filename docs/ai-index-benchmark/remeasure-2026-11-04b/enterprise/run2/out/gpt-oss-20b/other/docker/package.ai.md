### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:36:27Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Builds and runs the `orders-backoffice` Java application in a Docker container for the shop backend.

#### Purpose
- Compile the Java project with Maven.
- Package the application into a JAR.
- Run the JAR in a lightweight Alpine JRE container.

#### Responsibilities
- **Build pipeline**: Maven dependency resolution, compilation, and packaging.
- **Container runtime**: User creation, environment configuration, health checking, and application launch.

#### Key units
- **Dockerfile** – orchestrates multi‑stage build: `build` stage (`maven:3.9-eclipse-temurin-21`) and runtime stage (`eclipse-temurin:21-jre-alpine`).
- **Build commands** – `mvn -B dependency:go-offline`, `mvn -B package -DskipTests`.
- **Runtime commands** – `addgroup`, `adduser`, `COPY`, `EXPOSE 8080`, `USER backoffice`, `HEALTHCHECK`, `ENTRYPOINT`.

#### Data flow
Source → Maven build → JAR (`orders-backoffice.jar`) → Docker build → Runtime container → HTTP health endpoint.

#### Dependencies
- Maven dependencies resolved during build.
- Runtime JRE: `eclipse-temurin:21-jre-alpine`.
- Health endpoint: `http://localhost:8080/health/live`.

#### Cross-cutting
- Multi‑stage Docker build pattern.
- Environment variables: `JAVA_OPTS`, `SHOP_PROFILE`.
- Healthcheck via `wget` on `/health/live`.
