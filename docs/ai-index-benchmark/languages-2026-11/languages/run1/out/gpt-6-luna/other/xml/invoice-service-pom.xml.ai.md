### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:11Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the `invoice-service` JAR for creating, numbering, and archiving customer invoices, with database migrations and an optional PDF dependency.

#### Purpose
- Defines the Maven build and dependencies for the invoice service.
- Configures Flyway migrations, test settings, and an optional PDF profile.

#### Type
- Maven POM; root element `project`.
- Declares the default namespace `http://maven.apache.org/POM/4.0.0` and the `xsi` namespace `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service:2.7.0` (group inherited from parent); packaging `jar`; name `Invoice service`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — default scope; version `${jackson.version}`.
  - `org.flywaydb:flyway-core` — default scope; version `${flyway.version}`.
  - `org.postgresql:postgresql` — `runtime`.
  - `org.junit.jupiter:junit-jupiter` — `test`.
  - `org.testcontainers:postgresql` — `test`.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release=17`, `jackson.version=2.19.2`, `flyway.version=11.4.0`, `invoice.archive.dir=${project.build.directory}/archive`.
- Profile: `native-pdf`.

#### Configuration
- `invoice.archive.dir` defaults to `${project.build.directory}/archive`; passed to tests as the `invoice.archive.dir` system property.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Compiler release: `17`.
- Version properties: `jackson.version=2.19.2`, `flyway.version=11.4.0`.
- Profile `native-pdf` adds `org.apache.pdfbox:pdfbox` version `3.0.5`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- External artifacts: `com.fasterxml.jackson.core:jackson-databind`, `org.flywaydb:flyway-core`, `org.postgresql:postgresql`, `org.junit.jupiter:junit-jupiter`, `org.testcontainers:postgresql`, `org.apache.pdfbox:pdfbox`.
- Migration files are read from `src/main/resources/db/migration`.
