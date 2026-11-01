### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:54:17Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the `invoice-service` JAR to create and archive customer invoices, manage database migrations, and optionally add PDF support.

#### Purpose
- Defines the invoice service’s Maven build, dependencies, and test configuration.
- Adds optional PDFBox support through the `native-pdf` profile.

#### Type
- Maven POM; root element: `project`.
- Declares the default namespace `http://maven.apache.org/POM/4.0.0` and the `xsi` namespace `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service`; version inherited from parent `org.example.billing:billing-parent:2.7.0`; packaging `jar`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — default scope.
  - `org.flywaydb:flyway-core` — default scope.
  - `org.postgresql:postgresql` — `runtime`.
  - `org.junit.jupiter:junit-jupiter` — `test`.
  - `org.testcontainers:postgresql` — `test`.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.
- `native-pdf` dependency `pdfbox` version: `3.0.5`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- External libraries: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL; optional PDFBox.
- Migration files: `src/main/resources/db/migration`.
- Maven plugins: Flyway Maven Plugin and Maven Surefire Plugin.
