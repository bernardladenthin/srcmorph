### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:16:09Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service JAR with database migrations, runtime database access, and test support; optionally adds PDFBox for native PDF handling.

#### Purpose
- Defines the `invoice-service` Maven module for creating, numbering, and archiving customer invoices.
- Configures dependencies, Flyway migrations, test execution, and the `native-pdf` profile.

#### Type
- Maven POM; root element: `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi`: `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service:2.7.0` (groupId and version inherited); packaging `jar`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — default compile scope.
  - `org.flywaydb:flyway-core` — default compile scope.
  - `org.postgresql:postgresql` — `runtime`.
  - `org.junit.jupiter:junit-jupiter` — `test`.
  - `org.testcontainers:postgresql` — `test`.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Profiles: `native-pdf` — adds `org.apache.pdfbox:pdfbox`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.
- Profile dependency `org.apache.pdfbox:pdfbox` version: `3.0.5`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- External artifacts: `com.fasterxml.jackson.core:jackson-databind`, `org.flywaydb:flyway-core`, `org.postgresql:postgresql`, `org.junit.jupiter:junit-jupiter`, `org.testcontainers:postgresql`, `org.apache.pdfbox:pdfbox`.
- Migration files under `src/main/resources/db/migration`.
- Maven property reference: `${project.build.directory}`.
