### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:41:56Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures the Maven build for the Invoice Service, defining dependencies, build plugins, and a native‑PDF profile.

#### Purpose
- Build a JAR for invoice creation, numbering, and archiving.
- Apply database migrations via Flyway.
- Provide test dependencies for JUnit and Testcontainers.

#### Type
- Maven POM
- Root element: `project`
- Namespaces:
  - `xmlns="http://maven.apache.org/POM/4.0.0"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`

#### Definitions
- **Parent**: `org.example.billing:billing-parent:2.7.0`
- **Artifact**: `invoice-service` (jar)
- **Properties**:
  - `maven.compiler.release` = `17`
  - `jackson.version` = `2.19.2`
  - `flyway.version` = `11.4.0`
  - `invoice.archive.dir` = `${project.build.directory}/archive`
- **Dependencies** (5):
  - `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`
  - `org.flywaydb:flyway-core:${flyway.version}`
  - `org.postgresql:postgresql` (runtime)
  - `org.junit.jupiter:junit-jupiter` (test)
  - `org.testcontainers:postgresql` (test)
- **Build Plugins** (2):
  - `org.flywaydb:flyway-maven-plugin:${flyway.version}`
  - `org.apache.maven.plugins:maven-surefire-plugin`
- **Profile**: `native-pdf`
  - Adds dependency `org.apache.pdfbox:pdfbox:3.0.5`

#### Configuration
- **Flyway Plugin**:
  - `locations` → `filesystem:src/main/resources/db/migration`
- **Surefire Plugin**:
  - `systemPropertyVariables` → `invoice.archive.dir=${invoice.archive.dir}`

#### Dependencies
- External libraries: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox (profile).
- Schema: Maven POM 4.0.0.
