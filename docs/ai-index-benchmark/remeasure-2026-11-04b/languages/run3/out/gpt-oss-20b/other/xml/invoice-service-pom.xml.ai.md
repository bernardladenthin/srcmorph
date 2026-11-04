### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:33Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures the Maven build for the Invoice Service module, defining dependencies, compiler settings, and build plugins.

#### Purpose
- Builds the `invoice-service` JAR.
- Manages database migrations with Flyway.
- Supplies test dependencies and optional PDF support.

#### Type
- Maven POM  
  Root element: `project`  
  Namespaces: `xmlns="http://maven.apache.org/POM/4.0.0"`, `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`

#### Definitions
- **Parent**: `org.example.billing:billing-parent:2.7.0`
- **Artifact**: `invoice-service` (jar)
- **Properties**:
  - `maven.compiler.release` = `17`
  - `jackson.version` = `2.19.2`
  - `flyway.version` = `11.4.0`
  - `invoice.archive.dir` = `${project.build.directory}/archive`
- **Dependencies**:
  - `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`
  - `org.flywaydb:flyway-core:${flyway.version}`
  - `org.postgresql:postgresql` (runtime)
  - `org.junit.jupiter:junit-jupiter` (test)
  - `org.testcontainers:postgresql` (test)
- **Build Plugins**:
  - `org.flywaydb:flyway-maven-plugin:${flyway.version}`  
    - `locations`: `filesystem:src/main/resources/db/migration`
  - `org.apache.maven.plugins:maven-surefire-plugin`  
    - `systemPropertyVariables`: `invoice.archive.dir=${invoice.archive.dir}`
- **Profile** `native-pdf`  
  - Adds dependency `org.apache.pdfbox:pdfbox:3.0.5`

#### Configuration
- `maven.compiler.release` = `17`
- `invoice.archive.dir` = `${project.build.directory}/archive`
- Flyway `locations` = `filesystem:src/main/resources/db/migration`
- Surefire `systemPropertyVariables.invoice.archive.dir` = `${invoice.archive.dir}`

#### Dependencies
- `org.example.billing:billing-parent:2.7.0`
- `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`
- `org.flywaydb:flyway-core:${flyway.version}`
- `org.postgresql:postgresql`
- `org.junit.jupiter:junit-jupiter`
- `org.testcontainers:postgresql`
- `org.apache.pdfbox:pdfbox:3.0.5` (profile `native-pdf`)
