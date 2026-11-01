### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:34:49Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the Maven project for the Invoice Service, configuring dependencies, build plugins, and a PDF export profile.

#### Purpose
- Defines the Maven build for the `invoice-service` module.
- Configures runtime and test dependencies, compiler settings, and build plugins.
- Provides a profile for PDF generation.

#### Type
- Maven POM  
  Root element: `project`  
  Namespaces:  
  - `xmlns="http://maven.apache.org/POM/4.0.0"`  
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`

#### Definitions
- **Coordinates**:  
  - `groupId`: *org.example.billing* (inherited from parent)  
  - `artifactId`: `invoice-service`  
  - `version`: *2.7.0* (inherited from parent)  
  - `packaging`: `jar`  
- **Parent**: `org.example.billing:billing-parent:2.7.0`
- **Properties**:  
  - `maven.compiler.release`: `17`  
  - `jackson.version`: `2.19.2`  
  - `flyway.version`: `11.4.0`  
  - `invoice.archive.dir`: `${project.build.directory}/archive`
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
- **Profile** `native-pdf`:  
  - Adds dependency `org.apache.pdfbox:pdfbox:3.0.5`

#### Configuration
- `maven.compiler.release` → `17`
- `invoice.archive.dir` → `${project.build.directory}/archive`
- Flyway plugin location → `filesystem:src/main/resources/db/migration`
- Surefire system property `invoice.archive.dir` → `${invoice.archive.dir}`

#### Dependencies
- External libraries: `jackson-databind`, `flyway-core`, `postgresql`, `junit-jupiter`, `testcontainers:postgresql`, `pdfbox`
- Schema: Maven POM 4.0.0
- Parent project: `org.example.billing:billing-parent:2.7.0`
