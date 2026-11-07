### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:05Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides infrastructure for invoicing, shipment notifications, and statement rendering, with build, scheduling, and XML schema/XSLT support.

#### Purpose
- Build and package the Invoice Service module.
- Configure a reminder scheduler for overdue invoices.
- Define XML schema for shipment notices.
- Transform monthly statements to HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM with dependencies, plugins, and profiles.
- **Scheduling** – Spring XML context wiring a reminder job, data source, mail gateway, and cron trigger.
- **Data Validation** – XML Schema (`shipment.xsd`) for shipment notices.
- **Presentation** – XSLT (`statement-to-html.xsl`) to render statements as HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema types: `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`, `ShipmentNoticeType`; root element `shipmentNotice`.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, booking rows, and total.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations from `src/main/resources/db/migration`, packages JAR, optionally adds PDFBox for PDF generation.
2. **Scheduling**: Spring context loads data source → repository → job → scheduler; cron triggers `overdueReminderJob.sendDueReminders` which queries repository, formats emails via `mailGateway`, and sends reminders.
3. **Shipment**: External systems generate XML matching `shipmentNotice`; XML is validated against `shipment.xsd`.
4. **Statement Rendering**: XML statement (namespace `urn:example:statement`) is transformed by `statement-to-html.xsl` into an HTML page, highlighting amounts above `highlightAbove`.

#### Dependencies
- **Internal**: `org.example.reminder` classes, `org.example.billing` parent POM.
- **External**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (profile), HikariCP, Spring `task` and `context` modules.
- **Schema**: No external imports; uses standard XML Schema namespace.

#### Cross-cutting
- **Configuration**: Property placeholders (`reminder.properties`) for DB and SMTP settings; Maven properties for compiler, Jackson, Flyway.
- **Error handling**: Not explicitly defined in summaries; assumed standard Spring and Flyway exception propagation.
- **Threading**: Scheduler pool size set to 2; HikariCP pool max 8.
- **Naming conventions**: XML namespaces (`urn:example:shipment:1`, `urn:example:statement`) and element qualification enforced.
