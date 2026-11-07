### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:12Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Builds the invoice service JAR, configuring database migrations, test execution, and an optional PDF-generation dependency.

#### Purpose
- Defines the `invoice-service` Maven module for creating, numbering, and archiving customer invoices.
- Configures Flyway migrations and passes the invoice archive directory to tests.

#### Type
- Maven POM; root element `project`.
- Declares the default namespace `http://maven.apache.org/POM/4.0.0` and the `xsi` namespace `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `artifactId` `invoice-service`, `packaging` `jar`; `groupId` is not declared in this file.
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
- Properties: `maven.compiler.release` = `17`; `jackson.version` = `2.19.2`; `flyway.version` = `11.4.0`; `invoice.archive.dir` = `${project.build.directory}/archive`.
- Profile `native-pdf` — adds `org.apache.pdfbox:pdfbox` version `3.0.5`.
- Project metadata: `name` `Invoice service`; description `Creates, numbers and archives customer invoices.`

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Migration files under `src/main/resources/db/migration`.
- External dependencies: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL; optional profile dependency PDFBox.
