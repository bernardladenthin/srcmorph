### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:24:16Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Builds and deploys a Java back‑office service as a Docker image, compiling the Maven project into a JAR and running it in an Alpine JRE container with health monitoring.

#### Purpose
- Compile a Maven‑based Java back‑office application into a runnable JAR.
- Package the JAR into a lightweight Docker image for production deployment.
- Provide health checks and expose the service on port 8080.

#### Responsibilities
- **Build pipeline**: Maven dependency resolution, source compilation, and JAR packaging.
- **Containerization**: Multi‑stage Docker build, user isolation, environment configuration, and health‑check setup.
- **Runtime configuration**: JVM options, profile selection, and entrypoint execution.

#### Key units
- **Dockerfile** – orchestrates the two‑stage build, sets environment variables (`JAVA_OPTS`, `SHOP_PROFILE`), exposes port 8080, defines healthcheck, and runs the JAR.
- **orders-backoffice.jar** – the compiled application artifact produced by Maven.
- **Maven project** (`pom.xml`, `src/`) – source and build definition for the back‑office service.

#### Data flow
1. **Build stage**: `pom.xml` → Maven resolves dependencies → compiles `src/` → produces `orders-backoffice.jar`.
2. **Runtime stage**: `orders-backoffice.jar` is copied into `/opt/shop/`, environment variables are set, and the JAR is executed with `java $JAVA_OPTS -jar ...`.
3. Healthcheck periodically requests `http://localhost:8080/health/live` to verify service liveness.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21` (build) and `eclipse-temurin:21-jre-alpine` (runtime).
- Maven project files (`pom.xml`, `src/`).
- `wget` utility for the healthcheck.
- User `backoffice` in group `shop` for non‑root execution.

#### Cross-cutting
- Multi‑stage Docker build pattern for separating build and runtime environments.
- Consistent use of environment variables (`APP_VERSION`, `JAVA_OPTS`, `SHOP_PROFILE`) for configuration.
- Healthcheck integration using `wget` to expose a liveness endpoint.
- User/group isolation (`backoffice`/`shop`) for security.
