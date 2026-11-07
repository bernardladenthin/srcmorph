### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:30Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Builds the invoice service JAR, configuring database migrations, test dependencies and an optional PDFBox profile.

#### Purpose
- Defines the `invoice-service` Maven module for creating, numbering and archiving customer invoices.
- Configures Flyway migrations and passes the archive directory to tests.

#### Type
- Maven POM; root element `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service:2.7.0` (group and version inherited); packaging `jar`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — compile, version `${jackson.version}`.
  - `org.flywaydb:flyway-core` — compile, version `${flyway.version}`.
  - `org.postgresql:postgresql` — runtime.
  - `org.junit.jupiter:junit-jupiter` — test.
  - `org.testcontainers:postgresql` — test.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release=17`; `jackson.version=2.19.2`; `flyway.version=11.4.0`; `invoice.archive.dir=${project.build.directory}/archive`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox`, version `3.0.5` (compile scope).

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`.
- Flyway `locations`: `filesystem:src/main/resources/db/migration`.
- Surefire system property `invoice.archive.dir`: `${invoice.archive.dir}`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Migration files: `src/main/resources/db/migration`.
