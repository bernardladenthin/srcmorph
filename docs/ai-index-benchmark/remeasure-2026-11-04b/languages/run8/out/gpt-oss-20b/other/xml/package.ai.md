### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:52Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation files that support the Invoice Service, a reminder scheduler, shipment notifications, and statement rendering.

#### Purpose
- Build and configure the Invoice Service module.
- Wire a Spring‑based reminder scheduler.
- Define XML contracts for shipment notices.
- Provide XSLT for rendering account statements to HTML.

#### Responsibilities
- **Build & Migration** – Maven POM with Flyway and PDFBox profiles.
- **Scheduling** – Spring XML context for a reminder job that emails overdue invoices.
- **Data Validation** – XSD for shipment notices sent by warehouses.
- **Presentation** – XSLT stylesheet that turns statement XML into styled HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration location, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema for `<shipmentNotice>` with `ShipmentNoticeType`, `ParcelType`, `AddressType`, and enumerated `CarrierCode`.
- `statement-to-html.xsl` – XSLT that outputs `<html>` from `<st:statement>`, with parameters `currencySymbol` and `highlightAbove`.

#### Data flow
1. **Invoice Service**: Maven builds JAR → Flyway applies DB migrations → optional PDFBox profile generates PDFs.
2. **Reminder Scheduler**: Spring context loads data source → repository reads overdue invoices → job composes emails via `SmtpMailGateway` → scheduled by cron.
3. **Shipment Notice**: Warehouse emits XML → validated against `shipment.xsd` before processing.
4. **Statement Rendering**: XML statement → XSLT transforms to HTML, highlighting large amounts.

#### Dependencies
- **External libs**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (profile), HikariCP, Spring Task Scheduler.
- **Internal**: `org.example.reminder` package classes (`JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`).
- **Schema**: `urn:example:shipment:1` for shipment notices; `urn:example:statement` for statements.

#### Cross-cutting
- Spring property placeholders (`reminder.properties`) for DB and SMTP settings.
- Consistent use of XML namespaces (`xs`, `shp`, `st`) across schemas and XSLT.
- Parameterization in XSLT (`currencySymbol`, `highlightAbove`) for flexible rendering.
