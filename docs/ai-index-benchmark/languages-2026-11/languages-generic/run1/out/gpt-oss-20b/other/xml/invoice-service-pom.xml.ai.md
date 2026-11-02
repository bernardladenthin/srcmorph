### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:30:59Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the Invoice Service Maven project, defining dependencies, build plugins, and profiles for PDF generation.

#### Purpose
- Builds the Invoice Service JAR.
- Configures database migration and test runtime.

#### Type
- Maven POM (XML) for Java project.

#### Input
- Parent POM `billing-parent` (groupId `org.example.billing`).
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Dependencies: Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers.
- Build plugins: Flyway Maven Plugin, Maven Surefire Plugin.
- Profile `native-pdf` adds PDFBox dependency.

#### Output
- JAR artifact `invoice-service`.
- Flyway migration scripts located at `src/main/resources/db/migration`.
- Test environment variable `invoice.archive.dir` set for Surefire.

#### Core logic
- Sets Java 17 compiler release.
- Declares runtime PostgreSQL driver.
- Configures Flyway to run migrations from filesystem location.
- Passes `invoice.archive.dir` to tests via system property.
- Enables PDFBox dependency when `native-pdf` profile activated.

#### Public API
- N/A (POM configuration only).

#### Constants and configuration
- `maven.compiler.release = 17`
- `jackson.version = 2.19.2`
- `flyway.version = 11.4.0`
- `invoice.archive.dir = ${project.build.directory}/archive`

#### Dependencies
- `com.fasterxml.jackson.core:jackson-databind:${jackson.version}`
- `org.flywaydb:flyway-core:${flyway.version}`
- `org.postgresql:postgresql` (runtime)
- `org.junit.jupiter:junit-jupiter` (test)
- `org.testcontainers:postgresql` (test)
- `org.apache.pdfbox:pdfbox:3.0.5` (profile `native-pdf`)

#### Exceptions / Errors
- None (configuration file).
