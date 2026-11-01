### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:56:11Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the `invoice-service` JAR for creating, numbering, and archiving customer invoices, with database migrations and optional PDF support.

#### Purpose
- Defines the Maven build and dependencies for the invoice service.
- Configures Flyway migrations, test execution, and an optional `native-pdf` profile.

#### Type
- Maven POM; root element `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi` is `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `artifactId` `invoice-service`; `packaging` `jar`. No project `groupId` or `version` is specified directly.
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
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox` version `3.0.5`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- `flyway-maven-plugin` migration location: `filesystem:src/main/resources/db/migration`.
- `maven-surefire-plugin` system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Migration files: `src/main/resources/db/migration`.
- Maven property reference: `${project.build.directory}`.
