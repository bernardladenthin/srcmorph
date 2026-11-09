### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:39:35Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages and runs the Shop orders back-office service as a production-configured Java container.

#### Purpose
- Builds `orders-backoffice.jar` with Maven and deploys it in a Java 21 JRE Alpine container.
- Exposes the service on port `8080` with a non-root runtime user and HTTP liveness check.

#### Responsibilities
- **Image build and runtime packaging:** Separates Maven build and Java runtime stages; copies the packaged artifact into the runtime image.
- **Container operation:** Sets production environment configuration, launches the JAR, and checks `/health/live`.

#### Key units
- `Dockerfile`: Defines the two-stage build, runtime user `backoffice`, port `8080`, health check, and Java entrypoint.

#### Data flow
- `pom.xml` and `src` → Maven package build → `/src/target/orders-backoffice.jar` → `/opt/shop/orders-backoffice.jar` → Java process in the runtime container.

#### Dependencies
- Build image: `maven:3.9-eclipse-temurin-21`; runtime image: `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`; runtime artifact: `orders-backoffice.jar`.

#### Cross-cutting
- Runtime configuration: `SHOP_PROFILE=production`, `JAVA_OPTS="-XX:MaxRAMPercentage=75"`, and `APP_VERSION=2.1.0`.
- Runs as non-root user `backoffice`; liveness check runs every `30s`.
