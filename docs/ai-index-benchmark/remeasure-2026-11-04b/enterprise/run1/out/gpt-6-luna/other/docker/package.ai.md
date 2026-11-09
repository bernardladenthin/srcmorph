### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:24:24Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Packages the Orders Backoffice Java application as a production container that serves on port 8080.

#### Purpose
- Builds `orders-backoffice.jar` and runs it in a Java 21 JRE container.
- Provides a non-root runtime with a liveness health check.

#### Responsibilities
- Build: resolves Maven dependencies and packages the application with tests skipped.
- Runtime: copies the JAR into the image and launches it with configurable JVM options.

#### Key units
- `Dockerfile`: defines the Maven build stage and production runtime stage.

#### Data flow
- `pom.xml` and `src` → Maven package build → `/src/target/orders-backoffice.jar` → `/opt/shop/orders-backoffice.jar` → Java process.

#### Dependencies
- Build image: `maven:3.9-eclipse-temurin-21`; runtime image: `eclipse-temurin:21-jre-alpine`.
- Build inputs: `pom.xml`, `src`.

#### Cross-cutting
- Runtime configuration: `APP_VERSION=2.1.0`, `JAVA_OPTS="-XX:MaxRAMPercentage=75"`, and `SHOP_PROFILE=production`.
- Runs as the `backoffice` user in the `shop` group; exposes port `8080`.
- Health check probes `/health/live` every 30 seconds.
