### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:37:59Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service JAR, configures database migrations and tests, and provides an optional PDFBox dependency.

#### Purpose
- Defines the `invoice-service` Maven module for creating, numbering, and archiving customer invoices.
- Configures Flyway migrations, test execution, and an optional `native-pdf` profile.

#### Type
- Maven POM; root element: `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi` → `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: inherited `groupId` `org.example.billing`; `artifactId` `invoice-service`; `version` inherited from parent; `packaging` `jar`.
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
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- `flyway-maven-plugin` configuration: `locations` contains `filesystem:src/main/resources/db/migration`.
- `maven-surefire-plugin` configuration: `systemPropertyVariables.invoice.archive.dir` is `${invoice.archive.dir}`.
- `native-pdf` profile dependency `org.apache.pdfbox:pdfbox`: version `3.0.5`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Libraries and test dependencies: `com.fasterxml.jackson.core:jackson-databind`, `org.flywaydb:flyway-core`, `org.postgresql:postgresql`, `org.junit.jupiter:junit-jupiter`, `org.testcontainers:postgresql`, `org.apache.pdfbox:pdfbox`.
- Plugins: `org.flywaydb:flyway-maven-plugin`, `org.apache.maven.plugins:maven-surefire-plugin`.
- Migration directory: `src/main/resources/db/migration`.
