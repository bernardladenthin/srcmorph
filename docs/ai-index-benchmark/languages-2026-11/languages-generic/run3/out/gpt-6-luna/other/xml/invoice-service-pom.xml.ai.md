### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:08Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the invoice service build for creating, numbering, and archiving customer invoices.

#### Purpose
- Defines Maven build settings and dependencies for the `invoice-service` module.
- Configures database migration and test execution, with optional PDFBox support.

#### Type
- Maven POM XML project; `jar` packaging.
- Inherits from `org.example.billing:billing-parent:2.7.0`.

#### Input
- Maven project properties and inherited parent configuration.
- Flyway migration files from `src/main/resources/db/migration`.
- Test system property `invoice.archive.dir`.

#### Output
- Builds the `invoice-service` JAR using Java release 17.
- Sets the invoice archive directory to `${project.build.directory}/archive`.
- Configures Flyway migrations and Surefire tests.

#### Core logic
- Declares Jackson Databind and Flyway Core as application dependencies.
- Provides PostgreSQL at runtime and JUnit Jupiter plus Testcontainers PostgreSQL for tests.
- Configures the Flyway Maven plugin to load migrations from the module’s filesystem resources.
- Passes the archive directory property to tests.
- Adds Apache PDFBox only when the `native-pdf` profile is active.

#### Constants and configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- `native-pdf`: profile enabling `org.apache.pdfbox:pdfbox:3.0.5`.
- `filesystem:src/main/resources/db/migration`: Flyway migration location.
- `invoice.archive.dir`: Surefire test system property, sourced from the matching Maven property.

#### Dependencies
- `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`.
- `org.flywaydb:flyway-core:${flyway.version}`.
- `org.postgresql:postgresql` — runtime scope.
- `org.junit.jupiter:junit-jupiter` — test scope.
- `org.testcontainers:postgresql` — test scope.
- `org.apache.pdfbox:pdfbox:3.0.5` — `native-pdf` profile only.
- Build plugins: `org.flywaydb:flyway-maven-plugin:${flyway.version}`, `org.apache.maven.plugins:maven-surefire-plugin`.
