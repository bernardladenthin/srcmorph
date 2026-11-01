### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:40:58Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the Maven build for the Invoice Service, defining dependencies, plugins, and build properties.

#### Purpose
- Sets up build configuration for Invoice Service.
- Declares project metadata, dependencies, and build plugins.

#### Type
- Maven POM (XML) for Java project.

#### Input
- Parent POM `billing-parent` (groupId `org.example.billing`, artifactId `billing-parent`, version `2.7.0`).
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Dependencies: Jackson Databind, Flyway Core, PostgreSQL driver, JUnit Jupiter, Testcontainers PostgreSQL.
- Build plugins: Flyway Maven Plugin, Maven Surefire Plugin.
- Profile `native-pdf` adds PDFBox dependency.

#### Output
- Compiled JAR (`invoice-service.jar`) in target directory.
- Flyway migration scripts applied to database.
- Test results via Surefire.
- Archive directory set as system property `invoice.archive.dir`.

#### Core logic
- Inherits common settings from `billing-parent`.
- Uses Java 17 compiler.
- Configures Flyway to run migrations from `src/main/resources/db/migration`.
- Passes `invoice.archive.dir` to tests via system property.
- Allows optional PDF generation via `native-pdf` profile.

#### Public API
- N/A (POM is build configuration).

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
- N/A (configuration file).
