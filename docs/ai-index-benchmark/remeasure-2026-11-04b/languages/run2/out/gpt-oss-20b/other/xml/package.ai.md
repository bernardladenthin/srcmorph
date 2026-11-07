### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:32Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build configuration, scheduling, XML schema, and XSLT transformation for an invoicing and shipment system.

#### Purpose
- Build and package the Invoice Service JAR with database migration and optional PDF support.
- Configure a Spring reminder scheduler for overdue invoices.
- Define XML contracts for shipment notices and account statements.
- Transform statement XML into HTML for reporting.

#### Responsibilities
- **Build & Deployment** – Maven POM with dependencies, plugins, and PDF profile.
- **Scheduling** – Spring context wiring for data source, repository, mail gateway, job, and cron trigger.
- **Data Validation** – XML Schema for shipment notices (`shipment.xsd`).
- **Presentation** – XSLT stylesheet to render account statements as HTML (`statement-to-html.xsl`).

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; scheduler `reminderScheduler`.
- `shipment.xsd` – `shipmentNotice` root, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`, `ShipmentNoticeType`.
- `statement-to-html.xsl` – Parameters `currencySymbol`, `highlightAbove`; templates for header, booking rows, and total.

#### Data flow
1. **Build**: Maven compiles `invoice-service`, runs Flyway migrations, optionally bundles PDFBox for native PDF generation.
2. **Scheduling**: At 07:00 Mon‑Fri, `OverdueReminderJob` queries `JdbcReminderRepository` via `reminderDataSource`, sends emails through `SmtpMailGateway`.
3. **Shipment**: Warehouse emits `shipmentNotice` XML validated against `shipment.xsd`; consumer processes the notice.
4. **Statement**: Monthly statement XML (namespace `urn:example:statement`) is transformed by `statement-to-html.xsl` into an HTML report, highlighting amounts above `highlightAbove`.

#### Dependencies
- **Internal**: `org.example.reminder` classes, `org.example.billing` parent POM.
- **External**: Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFBox, HikariCP, Spring task/context modules.
- **Schema**: Standard XML Schema namespace; XSLT uses `urn:example:statement`.

#### Cross-cutting
- Configuration via property placeholders (`reminder.properties`).
- Consistent use of XML namespaces for data contracts.
- Parameterized XSLT for currency and threshold customization.
- Maven profiles enable optional features (PDF generation).
