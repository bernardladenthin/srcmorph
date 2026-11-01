### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:02Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service that creates, numbers, and archives customer invoices.

#### Purpose
- Defines the Maven build for the `invoice-service` module.
- Configures Java 17 compilation, database migrations, tests, and an optional PDFBox dependency.

#### Type
- Maven POM for a Java JAR module.
- Child of `org.example.billing:billing-parent:2.7.0`; artifact `org.example.billing:invoice-service`.

#### Input
- Parent Maven project configuration and module sources.
- Flyway migrations from `src/main/resources/db/migration`.
- Maven profile selection, including optional `native-pdf`.

#### Output
- Builds the `invoice-service` JAR.
- Passes `invoice.archive.dir` to Surefire tests; default path is `${project.build.directory}/archive`.
- Configures Flyway Maven plugin to access the module’s migration files.

#### Core logic
- Sets Java compiler release to 17.
- Declares Jackson Databind and Flyway Core as compile dependencies; PostgreSQL as runtime; JUnit Jupiter and Testcontainers PostgreSQL as test dependencies.
- Configures Flyway Maven plugin with filesystem migration location.
- Configures Surefire to expose the archive directory as a system property.
- Adds Apache PDFBox only when the `native-pdf` profile is active.

#### Constants and configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- `flyway-maven-plugin` `locations`: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.
- Profile `native-pdf`: enables `org.apache.pdfbox:pdfbox:3.0.5`.

#### Dependencies
- `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`.
- `org.flywaydb:flyway-core:${flyway.version}`.
- `org.postgresql:postgresql` — runtime.
- `org.junit.jupiter:junit-jupiter` — test.
- `org.testcontainers:postgresql` — test.
- `org.apache.pdfbox:pdfbox:3.0.5` — `native-pdf` profile only.
- Build plugins: `org.flywaydb:flyway-maven-plugin:${flyway.version}`, `org.apache.maven.plugins:maven-surefire-plugin`.
