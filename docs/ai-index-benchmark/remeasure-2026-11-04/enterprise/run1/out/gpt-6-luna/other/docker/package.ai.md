### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:08:44Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages and runs the Orders Backoffice Java application as a production-configured container.

#### Purpose
- Builds `orders-backoffice.jar` with Maven and runs it on Java 21.
- Exposes the application on port `8080` and checks its `/health/live` endpoint.

#### Responsibilities
- Build stage: resolves Maven dependencies and packages the application with tests skipped.
- Runtime stage: installs the JAR, configures a non-root `backoffice` user, and launches the application.

#### Key units
- `Dockerfile`: defines the multi-stage build, production runtime, Java entrypoint, and healthcheck.

#### Data flow
- `pom.xml` and `src` → Maven build → `/src/target/orders-backoffice.jar` → `/opt/shop/orders-backoffice.jar` → Java process.

#### Dependencies
- Build image: `maven:3.9-eclipse-temurin-21`; runtime image: `eclipse-temurin:21-jre-alpine`.
- Runtime configuration: `SHOP_PROFILE=production`, `JAVA_OPTS="-XX:MaxRAMPercentage=75"`.

#### Cross-cutting
- Runs as non-root user `backoffice` in group `shop`.
- Healthcheck polls `http://localhost:8080/health/live` every `30s`.
