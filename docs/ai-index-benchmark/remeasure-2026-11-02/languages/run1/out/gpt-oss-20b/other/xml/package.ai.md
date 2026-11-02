### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:26:03Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A set of configuration, schema, and transformation files that support an invoice‑generation service, a reminder scheduler, and shipment/statement processing.

#### Purpose
- Build and package the Invoice Service JAR with database migration and optional PDF support.
- Wire a Spring‑based reminder scheduler for overdue invoices.
- Validate shipment notices and transform monthly statements to HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM with Flyway migrations and PDF profile.
- **Scheduling** – Spring XML for data source, repository, mail gateway, job, and cron trigger.
- **Data Validation** – XML Schema for shipment notices.
- **Presentation** – XSLT stylesheet for rendering statements as HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema for `<shipmentNotice>` with `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`, `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT for `<st:statement>` → HTML, parameters `currencySymbol`, `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations, packages JAR; optional PDFBox dependency added in `native-pdf` profile.
2. **Scheduling**: Spring context loads `reminderDataSource` → `reminderRepository` → `overdueReminderJob`; scheduler triggers `sendDueReminders` every weekday at 7 AM, sending emails via `mailGateway`.
3. **Shipment**: External system sends XML validated against `shipment.xsd`; data consumed by downstream services.
4. **Statement**: XML `<st:statement>` is transformed by `statement-to-html.xsl` into an HTML page, highlighting large bookings.

#### Dependencies
- **Build**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (profile).
- **Scheduler**: HikariCP, Spring Task Scheduler, `org.example.reminder` classes, `reminder.properties`.
- **Schema**: No external dependencies.
- **XSLT**: Requires input XML in `urn:example:statement` namespace.

#### Cross-cutting
- Consistent use of `${...}` property placeholders in Maven and Spring.
- Centralized database configuration via `reminder.db.*` properties.
- Default values in XSD (`country="DE"`) and XSLT (`currencySymbol="EUR"`, `highlightAbove=1000`).
- Error handling implied by Flyway migration failures and XSLT transformation errors, but not explicitly defined in summaries.
