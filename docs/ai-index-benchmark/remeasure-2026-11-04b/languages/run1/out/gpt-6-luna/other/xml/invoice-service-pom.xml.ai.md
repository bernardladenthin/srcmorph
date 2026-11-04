### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:20Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Builds the `invoice-service` JAR for creating, numbering, and archiving customer invoices, with database migrations and an optional PDFBox dependency.

#### Purpose
- Defines Maven build dependencies, plugins, properties, and the `native-pdf` profile for the invoice service.

#### Type
- Maven POM; root element `project`.
- Namespaces: default `http://maven.apache.org/POM/4.0.0`; `xsi` = `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: `org.example.billing:invoice-service:2.7.0` (group and version inherited); packaging `jar`; name `Invoice service`.
- Parent: `org.example.billing:billing-parent:2.7.0`.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — compile; version `${jackson.version}`.
  - `org.flywaydb:flyway-core` — compile; version `${flyway.version}`.
  - `org.postgresql:postgresql` — runtime.
  - `org.junit.jupiter:junit-jupiter` — test.
  - `org.testcontainers:postgresql` — test.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release=17`; `jackson.version=2.19.2`; `flyway.version=11.4.0`; `invoice.archive.dir=${project.build.directory}/archive`.
- Profile `native-pdf`: adds `org.apache.pdfbox:pdfbox` version `3.0.5`.

#### Configuration
- `maven.compiler.release`: `17`.
- `jackson.version`: `2.19.2`.
- `flyway.version`: `11.4.0`.
- `invoice.archive.dir`: `${project.build.directory}/archive`; passed to Surefire as a system property.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Migration files under `src/main/resources/db/migration`.
- External systems and libraries: PostgreSQL, Jackson, Flyway, JUnit Jupiter, Testcontainers, and PDFBox (profile-dependent).
