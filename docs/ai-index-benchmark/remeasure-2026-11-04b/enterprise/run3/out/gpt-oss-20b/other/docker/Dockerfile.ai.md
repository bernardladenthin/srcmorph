### Dockerfile
- H: 1.0
- C: FEDB5F79
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:41:13Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 7854BFD9
---
**Facts (exact, whole file):** build stages: 2; RUN instructions: 3; COPY and ADD instructions: 3; environment entries: 2; exposed ports: 1; users: 1; entrypoints and commands: 1; healthchecks: 1

> Builds and runs the `orders-backoffice` Java application in a lightweight Alpine JRE container.

#### Purpose
- Compile a Maven project and package it into a JAR.
- Deploy the JAR as a Docker image for the `orders-backoffice` service.

#### Type
- `Dockerfile`  
  Base image: `maven:3.9-eclipse-temurin-21` (build stage)  
  Runtime image: `eclipse-temurin:21-jre-alpine`  
  Dockerfile API: Dockerfile syntax 1.4

#### Definitions
- **Stages**  
  - `build`: uses `maven:3.9-eclipse-temurin-21`; sets `/src` as workdir; copies `pom.xml`, resolves dependencies, copies source, packages JAR.  
  - `runtime`: uses `eclipse-temurin:21-jre-alpine`; creates group `shop` and user `backoffice`; copies JAR from `build` stage; exposes port `8080`; sets user; defines healthcheck; sets entrypoint.
- **ARG**  
  - `APP_VERSION=2.1.0`
- **ENV**  
  - `JAVA_OPTS="-XX:MaxRAMPercentage=75"`  
  - `SHOP_PROFILE=production`
- **User**  
  - `backoffice` in group `shop`
- **Healthcheck**  
  - `wget -qO- http://localhost:8080/health/live || exit 1` every 30s
- **Entrypoint**  
  - `sh -c "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"`

#### Configuration
- `APP_VERSION=2.1.0`
- `JAVA_OPTS="-XX:MaxRAMPercentage=75"`
- `SHOP_PROFILE=production`
- Exposed port: `8080`

#### Dependencies
- Build image: `maven:3.9-eclipse-temurin-21`
- Runtime image: `eclipse-temurin:21-jre-alpine`
- Maven project: `pom.xml` (not shown)
- JAR: `orders-backoffice.jar` (produced by Maven)
