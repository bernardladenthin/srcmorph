### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:30:53Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages the Shop orders backoffice Java application for production deployment and runtime health monitoring.

#### Purpose
- Builds `orders-backoffice.jar` with Maven and runs it on a Java 21 runtime image.
- Exposes the service on port `8080` as the non-root `backoffice` user.

#### Responsibilities
- Multi-stage container build: resolves Maven dependencies, packages the application with tests skipped, and copies the JAR into a lightweight runtime image.
- Runtime setup: sets production profile and JVM memory configuration, and checks `/health/live` every 30 seconds.

#### Key units
- `Dockerfile`: defines the `build` and runtime stages, service user, port, healthcheck, and Java entrypoint.

#### Data flow
- `pom.xml` and `src` → Maven package step → `/src/target/orders-backoffice.jar` → `/opt/shop/orders-backoffice.jar` → Java process on port `8080`.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21` and `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml` and `src`; runtime healthcheck depends on `http://localhost:8080/health/live`.

#### Cross-cutting
- Runtime runs as non-root user `backoffice`, in group `shop`.
- Configuration includes `JAVA_OPTS="-XX:MaxRAMPercentage=75"` and `SHOP_PROFILE=production`; `APP_VERSION=2.1.0` is declared but otherwise unused.
