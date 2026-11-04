### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:18:11Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Builds and deploys a Java back‑office service as a Docker image, compiling the Maven project and running the resulting JAR in an Alpine JRE container.

#### Purpose
- Compile a Maven‑based Java back‑office application into a JAR.
- Package the JAR into a lightweight Docker image.
- Expose the service on port 8080 with a health‑check endpoint.

#### Responsibilities
- **Build pipeline**: Maven dependency resolution, source compilation, and packaging.
- **Runtime configuration**: Java options, user permissions, and health‑check.
- **Container orchestration**: Exposing ports, setting entrypoint, and defining image layers.

#### Key units
- **Dockerfile**: Two‑stage build; `build` stage compiles with Maven, `runtime` stage runs the JAR.
- **Maven project** (`pom.xml` + `src/`): Defines dependencies and build lifecycle.
- **Java application** (`orders-backoffice.jar`): The back‑office service executable.

#### Data flow
`pom.xml` → Maven resolves dependencies → `src/` compiled → JAR produced → JAR copied to runtime image → Docker container starts JAR with `java $JAVA_OPTS -jar`.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21` (build), `eclipse-temurin:21-jre-alpine` (runtime).
- Maven project artifacts (`pom.xml`, `src/`).
- Runtime environment variables: `APP_VERSION`, `JAVA_OPTS`, `SHOP_PROFILE`.

#### Cross-cutting
- Two‑stage Docker build pattern.
- Environment variable configuration for Java and application profile.
- Non‑root user (`backoffice`) for running the service.
- Health‑check using `wget` on `/health/live`.
