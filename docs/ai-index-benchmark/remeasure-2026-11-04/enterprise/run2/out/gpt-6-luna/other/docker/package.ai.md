### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:15:15Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages the Orders Backoffice Java application as a production container that serves HTTP traffic on port 8080.

#### Purpose
- Builds `orders-backoffice.jar` with Maven and runs it on a Java 21 JRE.
- Provides a production runtime configured for non-root execution and HTTP liveness monitoring.

#### Responsibilities
- **Build and packaging:** Resolves Maven dependencies and packages the application from `pom.xml` and `src`, skipping tests.
- **Runtime setup:** Creates the `backoffice` user in the `shop` group, configures the Java process, and exposes port `8080`.
- **Health monitoring:** Checks `http://localhost:8080/health/live` every 30 seconds.

#### Key units
- `Dockerfile`: Two-stage build using `maven:3.9-eclipse-temurin-21` and `eclipse-temurin:21-jre-alpine`; copies the packaged JAR to `/opt/shop/orders-backoffice.jar` and launches it with Java.

#### Data flow
- `pom.xml` and `src` → Maven dependency resolution and packaging → `/src/target/orders-backoffice.jar` → runtime image → Java process serving on port `8080`.

#### Dependencies
- Build inputs: `pom.xml`, `src`; build image: `maven:3.9-eclipse-temurin-21`.
- Runtime image: `eclipse-temurin:21-jre-alpine`; health check uses `wget` against `/health/live`.

#### Cross-cutting
- Runtime configuration: `APP_VERSION=2.1.0`, `SHOP_PROFILE=production`, and `JAVA_OPTS="-XX:MaxRAMPercentage=75"`.
- Runs as non-root user `backoffice`; Java starts via an `exec`-based shell entrypoint.
