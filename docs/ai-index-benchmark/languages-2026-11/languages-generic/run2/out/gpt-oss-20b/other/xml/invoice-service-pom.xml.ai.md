### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:34:46Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the Invoice Service Maven build, dependencies, and test environment.

#### Purpose
- Builds the Invoice Service JAR.
- Manages dependencies for JSON, database migration, PostgreSQL, testing, and optional PDF generation.
- Configures Flyway migrations and test properties.

#### Type
- Maven POM (XML) for Java project.

#### Input
- Parent POM `billing-parent` (groupId `org.example.billing`, version `2.7.0`).
- Properties: `maven.compiler.release=17`, `jackson.version=2.19.2`, `flyway.version=11.4.0`, `invoice.archive.dir`.
- Build plugins: Flyway, Surefire.
- Profiles: `native-pdf`.

#### Output
- Compiled JAR `invoice-service`.
- Flyway migration scripts applied to PostgreSQL.
- Test environment variables (e.g., `invoice.archive.dir`).

#### Core logic
- Declares project metadata (artifactId, packaging, description).
- Sets Java compiler release to 17.
- Defines dependencies: Jackson Databind, Flyway Core, PostgreSQL driver, JUnit Jupiter, Testcontainers PostgreSQL.
- Configures Flyway plugin to load migrations from `src/main/resources/db/migration`.
- Configures Surefire to expose `invoice.archive.dir` as a system property during tests.
- Adds optional PDFBox dependency under `native-pdf` profile.

#### Public API
- N/A (build configuration only).

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
