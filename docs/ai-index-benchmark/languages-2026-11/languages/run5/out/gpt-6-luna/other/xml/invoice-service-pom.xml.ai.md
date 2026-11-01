### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:34:25Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the invoice service as a Java 17 JAR with database migrations, test dependencies, and an optional PDFBox profile.

#### Purpose
- Packages the invoice service, described as creating, numbering, and archiving customer invoices.
- Configures dependencies, Flyway migrations, test properties, and optional PDF support.

#### Type
- Maven POM; root element `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service`; version inherited from parent; packaging `jar`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — compile (default).
  - `org.flywaydb:flyway-core` — compile (default).
  - `org.postgresql:postgresql` — runtime.
  - `org.junit.jupiter:junit-jupiter` — test.
  - `org.testcontainers:postgresql` — test.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release`, `jackson.version`, `flyway.version`, `invoice.archive.dir`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox` version `3.0.5`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway `locations`: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Schema: `http://maven.apache.org/POM/4.0.0`; schema location `https://maven.apache.org/xsd/maven-4.0.0.xsd`.
- Migration files under `src/main/resources/db/migration`.
- Referenced build property: `${project.build.directory}`.
