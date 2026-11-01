### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:33:19Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides configuration, schema, and transformation assets for an invoicing and shipment system, including a Spring reminder scheduler, XML shipment schema, and XSLT statement renderer.

#### Purpose
- Supply build, runtime, and data‑validation artifacts for the Invoice service.
- Enable scheduled reminder jobs and shipment notice handling.
- Render financial statements to HTML.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with Flyway, Jackson, PDFBox profile.
- **Scheduling & Email** – Spring XML wiring for reminder jobs, data source, mail gateway.
- **Data Validation** – XML Schema for shipment notices.
- **Presentation** – XSLT stylesheet converting statement XML to HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration location, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – `ShipmentNoticeType` schema with `ParcelType`, `AddressType`, carrier enumeration.
- `statement-to-html.xsl` – XSLT stylesheet with templates `statementHeader`, `booking`, `statementTotal`; parameters `currencySymbol`, `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles, tests, applies Flyway migrations, optionally packages PDFBox for native‑PDF profile.
2. **Runtime**: Spring context loads `scheduler-context.xml`; `reminderScheduler` triggers `overdueReminderJob.sendDueReminders()` per cron, which queries `reminderRepository` (via `reminderDataSource`) and sends emails via `mailGateway`.
3. **Data Exchange**: Shipment notices are validated against `shipment.xsd` before processing.
4. **Presentation**: `statement-to-html.xsl` transforms `<st:statement>` XML into an HTML page, highlighting large bookings.

#### Dependencies
- **External libraries**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox (profile), HikariCP, Spring Framework.
- **External resources**: `classpath:reminder.properties`, `src/main/resources/db/migration` for Flyway.
- **Schema namespaces**: `urn:example:shipment:1`, `urn:example:statement`.

#### Cross-cutting
- Consistent use of property placeholders (`${...}`) for configuration.
- XML namespaces (`xs`, `shp`, `st`) standardize schema and XSLT.
- Default values and constraints (e.g., `WeightGrams` max 31500, `AddressType.country` default `DE`).
- Scheduling uses Spring Task Scheduler with cron expression and pool size.
