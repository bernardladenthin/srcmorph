### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:24:08Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures the `invoice-service` Maven JAR for invoice creation, numbering, and archiving, with database migrations, tests, and optional PDF support.

#### Purpose
- Builds the `invoice-service` invoice-processing service.
- Configures Flyway migrations, test settings, and an optional `native-pdf` dependency.

#### Type
- Maven POM; root element `project`.
- Namespace declarations: default `http://maven.apache.org/POM/4.0.0`; `xsi` → `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: groupId inherited as `org.example.billing`; artifactId `invoice-service`; version inherited as `2.7.0`; packaging `jar`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — default scope (`compile`); version `${jackson.version}`.
  - `org.flywaydb:flyway-core` — default scope (`compile`); version `${flyway.version}`.
  - `org.postgresql:postgresql` — `runtime`.
  - `org.junit.jupiter:junit-jupiter` — `test`.
  - `org.testcontainers:postgresql` — `test`.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release` = `17`; `jackson.version` = `2.19.2`; `flyway.version` = `11.4.0`; `invoice.archive.dir` = `${project.build.directory}/archive`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox`, version `3.0.5`.
- Project name: `Invoice service`; description: `Creates, numbers and archives customer invoices.`

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Flyway migration files: `src/main/resources/db/migration`.
- External database: PostgreSQL, through `org.postgresql:postgresql` and test-scoped `org.testcontainers:postgresql`.
