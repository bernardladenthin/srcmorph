### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:21:28Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages the Orders Backoffice Java application as a production-oriented container that exposes its HTTP service and reports liveness.

#### Purpose
- Builds `orders-backoffice.jar` with Maven and runs it on a Java 21 runtime.
- Provides a production container configuration for the Orders Backoffice application.

#### Responsibilities
- **Build:** Resolves Maven dependencies and packages the application from `pom.xml` and `src`, skipping tests.
- **Runtime:** Runs the JAR on port `8080` with configured JVM options and a `/health/live` healthcheck.
- **Container security:** Creates and uses the non-root `backoffice` user in the `shop` group.

#### Key units
- `Dockerfile` — multi-stage build that compiles the application and assembles its runtime image.

#### Data flow
- `pom.xml` and `src` → Maven dependency resolution and packaging → `orders-backoffice.jar` → Java 21 runtime container.

#### Dependencies
- Build images: `maven:3.9-eclipse-temurin-21` and `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`; runtime artifact: `/src/target/orders-backoffice.jar`.

#### Cross-cutting
- Runtime configuration includes `APP_VERSION=2.1.0`, `JAVA_OPTS="-XX:MaxRAMPercentage=75"`, and `SHOP_PROFILE=production`.
- The container runs as non-root and checks liveness every 30 seconds via `http://localhost:8080/health/live`.
