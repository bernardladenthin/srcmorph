### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:28:23Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service for creating, numbering, and archiving customer invoices, with database migration and optional PDF support.

#### Purpose
- Defines the Maven build for the `invoice-service` module.
- Configures Java compilation, dependencies, Flyway migrations, tests, and an optional PDF profile.

#### Type
- Maven POM project; `jar` packaging.
- Inherits from `org.example.billing:billing-parent:2.7.0`.

#### Input
- Maven parent configuration and project properties.
- Flyway migration scripts from `src/main/resources/db/migration`.
- Test configuration reads `invoice.archive.dir`.

#### Output
- Builds the `invoice-service` JAR.
- Sets the invoice archive directory to `${project.build.directory}/archive`.
- Configures Flyway Maven migrations and passes `invoice.archive.dir` to Surefire tests.

#### Core logic
- Targets Java release 17.
- Declares Jackson databind and Flyway Core as compile dependencies; PostgreSQL as a runtime dependency.
- Adds JUnit Jupiter and Testcontainers PostgreSQL for tests.
- Configures the Flyway Maven plugin to load migrations from the project filesystem.
- Adds PDFBox 3.0.5 only when the `native-pdf` profile is enabled.

#### Constants and configuration
- `maven.compiler.release`: `17`
- `jackson.version`: `2.19.2`
- `flyway.version`: `11.4.0`
- `invoice.archive.dir`: `${project.build.directory}/archive`
- `native-pdf`: optional profile enabling `org.apache.pdfbox:pdfbox:3.0.5`
- Flyway migration location: `filesystem:src/main/resources/db/migration`
- Surefire system property: `invoice.archive.dir`, set from `${invoice.archive.dir}`

#### Dependencies
- `com.fasterxml.jackson.core:jackson-databind` — version `${jackson.version}`
- `org.flywaydb:flyway-core` — version `${flyway.version}`
- `org.postgresql:postgresql` — runtime
- `org.junit.jupiter:junit-jupiter` — test
- `org.testcontainers:postgresql` — test
- `org.apache.pdfbox:pdfbox` — version `3.0.5`, `native-pdf` profile only
- Build plugins: `org.flywaydb:flyway-maven-plugin`, `org.apache.maven.plugins:maven-surefire-plugin`
