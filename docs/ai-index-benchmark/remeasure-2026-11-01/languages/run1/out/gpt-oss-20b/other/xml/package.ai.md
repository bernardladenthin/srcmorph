### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:19:08Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides configuration, schema, and transformation resources for an invoice‑and‑shipment service, including a Spring scheduler, XML validation, and XSLT rendering.

#### Purpose
- Package the build, runtime, and data‑formatting assets for the invoice service.
- Supply a reminder scheduler, shipment notice schema, and statement‑to‑HTML transformation.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with compiler, Flyway, PDF export profile.
- **Scheduling** – Spring XML wiring for a reminder job that queries a JDBC repository and sends emails.
- **Data Validation** – XML Schema for shipment notices with carrier, weight, and address constraints.
- **Presentation** – XSLT stylesheet converting statement XML into an HTML report with highlighting.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDF export profile.
- `scheduler-context.xml` – Spring context: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.
- `statement-to-html.xsl` – XSLT for `st:statement` → HTML, parameters `currencySymbol`, `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles code, runs Flyway migrations, packages JAR, optionally builds PDF export.
2. **Runtime**: Scheduler triggers `OverdueReminderJob.sendDueReminders()` via cron; job queries `JdbcReminderRepository`, uses `SmtpMailGateway` to email reminders.
3. **Data exchange**: Warehouse emits XML matching `shipment.xsd`; consumer validates against the schema.
4. **Presentation**: Statement XML (`urn:example:statement`) is transformed by `statement-to-html.xsl` into an HTML page, highlighting large amounts.

#### Dependencies
- **Internal**: `org.example.reminder` classes, `org.example.billing` parent POM.
- **External**: Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFBox, HikariCP, Spring beans/task schemas.
- **Schema**: XML Schema standard (`http://www.w3.org/2001/XMLSchema`).

#### Cross-cutting
- Spring bean configuration via XML; component scanning for web layer.
- JDBC data source with HikariCP, repository pattern.
- XSLT 1.0 with parameters for internationalization (currency symbol) and business rules (highlight threshold).
- Consistent use of property placeholders (`${...}`) for database, SMTP, and file paths.
