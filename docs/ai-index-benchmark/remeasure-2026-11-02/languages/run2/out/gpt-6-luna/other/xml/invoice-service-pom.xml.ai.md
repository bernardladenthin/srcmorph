### invoice-service-pom.xml
- H: 1.0
- C: 1C5813B5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:40:37Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures the `invoice-service` Maven module for invoice creation, database migrations, testing, and optional native PDF support.

#### Purpose
- Builds the invoice service as a JAR and declares its runtime and test dependencies.
- Configures Flyway migrations, Surefire test properties, and an optional PDFBox profile.

#### Type
- Maven POM; root element `project`.
- Namespace declarations: default `http://maven.apache.org/POM/4.0.0`; prefix `xsi` maps to `http://www.w3.org/2001/XMLSchema-instance`.

#### Definitions
- Coordinates: parent `org.example.billing:billing-parent:2.7.0`; artifactId `invoice-service`; packaging `jar`; no module-level `groupId` or `version` declared.
- Dependencies:
  - `com.fasterxml.jackson.core:jackson-databind` — default scope (`compile`); version `${jackson.version}`.
  - `org.flywaydb:flyway-core` — default scope (`compile`); version `${flyway.version}`.
  - `org.postgresql:postgresql` — `runtime`.
  - `org.junit.jupiter:junit-jupiter` — `test`.
  - `org.testcontainers:postgresql` — `test`.
- Plugins:
  - `flyway-maven-plugin` — version `${flyway.version}`; migration location `filesystem:src/main/resources/db/migration`.
  - `maven-surefire-plugin` — sets system property `invoice.archive.dir` to `${invoice.archive.dir}`.
- Properties: `maven.compiler.release=17`; `jackson.version=2.19.2`; `flyway.version=11.4.0`; `invoice.archive.dir=${project.build.directory}/archive`.
- Profile: `native-pdf` — adds `org.apache.pdfbox:pdfbox` version `3.0.5`.
- Name: `Invoice service`; description: `Creates, numbers and archives customer invoices.`

#### Configuration
- Java compiler release: `17`.
- Jackson version: `2.19.2`; Flyway version: `11.4.0`.
- Invoice archive directory: `${project.build.directory}/archive`; passed to tests as `invoice.archive.dir`.
- Flyway migration location: `filesystem:src/main/resources/db/migration`.
- PDFBox dependency is activated through profile `native-pdf`.

#### Dependencies
- Parent POM: `org.example.billing:billing-parent:2.7.0`.
- Schema URL: `https://maven.apache.org/xsd/maven-4.0.0.xsd`.
- Migration files: `src/main/resources/db/migration`.
