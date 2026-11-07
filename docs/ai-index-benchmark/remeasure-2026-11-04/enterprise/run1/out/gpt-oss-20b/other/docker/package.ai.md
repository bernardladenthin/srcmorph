### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:11:41Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Builds and deploys a Java back‑office service as a Docker image, compiling with Maven and running in an Alpine JRE container.

#### Purpose
- Compile a Maven project into a JAR.
- Package the JAR into a lightweight Docker image.
- Expose a health endpoint and run the service with configurable JVM options.

#### Responsibilities
- **Build stage**: resolve dependencies, compile, and package the application.
- **Runtime stage**: set up a non‑root user, configure environment, expose port, healthcheck, and launch the JAR.

#### Key units
- `Dockerfile` (build & runtime stages)
- `orders-backoffice.jar` (compiled artifact)
- `JAVA_OPTS`, `SHOP_PROFILE` (runtime configuration)

#### Data flow
`pom.xml → Maven dependencies → source → JAR → Docker image → container → HTTP health endpoint`

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21`, `eclipse-temurin:21-jre-alpine`
- Maven project files (`pom.xml`, `src/`)
- Runtime environment variables (`JAVA_OPTS`, `SHOP_PROFILE`)

#### Cross-cutting
- Multi‑stage Docker build pattern.
- Non‑root user (`backoffice`) for security.
- Healthcheck via `wget` on `/health/live`.
