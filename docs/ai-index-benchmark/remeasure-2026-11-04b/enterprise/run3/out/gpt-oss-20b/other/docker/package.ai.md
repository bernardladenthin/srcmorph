### other/docker
- H: 1.0
- C: E886D2BC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:42:30Z
- G: 
- A: 0.0.0
- X: package
- F: [Dockerfile](Dockerfile.ai.md)
---
> Builds and runs the `orders-backoffice` Java application in a lightweight Alpine JRE container.

#### Purpose
- Compile a Maven project and package it into a JAR.
- Deploy the JAR as a Docker image for the `orders-backoffice` service.

#### Responsibilities
- **Build stage**: resolve Maven dependencies, compile source, create executable JAR.
- **Runtime stage**: configure minimal Alpine JRE environment, set user/group, expose service port, provide healthcheck and entrypoint.

#### Key units
- **Dockerfile** – two‑stage build: `build` (Maven) → `runtime` (Alpine JRE).
- **ARG `APP_VERSION`** – version tag for the image.
- **ENV `JAVA_OPTS`** – JVM memory setting.
- **ENV `SHOP_PROFILE`** – Spring profile for production.
- **Healthcheck** – `wget` to `/health/live` every 30 s.
- **Entrypoint** – `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`.

#### Data flow
1. `build` stage copies `pom.xml` → resolves dependencies → copies source → runs `mvn package` → outputs `orders-backoffice.jar`.
2. `runtime` stage copies the JAR from `build` → sets up user `backoffice` in group `shop` → exposes port `8080` → starts the application via entrypoint.

#### Dependencies
- Base images: `maven:3.9-eclipse-temurin-21` (build), `eclipse-temurin:21-jre-alpine` (runtime).
- Maven project files (`pom.xml`, source code) – not shown but required for build.
- Generated artifact: `orders-backoffice.jar`.

#### Cross-cutting
- Two‑stage Dockerfile pattern for Java applications.
- Use of environment variables for JVM tuning and Spring profile.
- Healthcheck and entrypoint standardization for container readiness.
