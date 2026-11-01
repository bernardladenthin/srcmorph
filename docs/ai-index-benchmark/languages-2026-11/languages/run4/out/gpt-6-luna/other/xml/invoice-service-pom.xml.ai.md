### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:27:38Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service JAR for creating, numbering, and archiving customer invoices, with optional PDF support.

#### Purpose
- Defines the `invoice-service` Maven module and its build dependencies.
- Configures Flyway migrations, test execution, and an optional PDF dependency.

#### Type
- Maven POM; root element `project`.
- Namespace `http://maven.apache.org/POM/4.0.0` (default); `xsi` → `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service:2.7.0` (version inherited from parent); packaging `jar`.
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
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox` version `3.0.5`.
- Name: `Invoice service`; description: `Creates, numbers and archives customer invoices.`

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Migration files: `src/main/resources/db/migration`.
- External systems and libraries: PostgreSQL, Jackson Databind, Flyway, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox.
