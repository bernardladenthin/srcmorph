### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:19Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, configuration, and data‑exchange support for an invoice‑processing service, including Maven build setup, a reminder scheduler, shipment XML schema, and statement‑to‑HTML transformation.

#### Purpose
- Build and package the `invoice-service` JAR with database migrations and optional PDF generation.
- Configure a Spring‑based reminder scheduler that sends overdue notifications via email.
- Define XML contracts for shipment notices and monthly account statements.
- Transform statement XML into styled HTML for reporting.

#### Responsibilities
- **Build & Deployment** – Maven POM with Flyway migrations, compiler settings, and PDF profile.
- **Scheduling & Notifications** – Spring context for a daily reminder job using HikariCP, JDBC repository, and SMTP gateway.
- **Data Exchange** – XML Schema for shipment notices; XSLT for rendering account statements.
- **Configuration Management** – Property placeholders for database, SMTP, and job parameters.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; `reminderScheduler` with cron trigger.
- `shipment.xsd` – `shipmentNotice` schema with `ParcelType`, `AddressType`, `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT stylesheet transforming `st:statement` XML to HTML, highlighting large amounts.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations, packages JAR; optional PDFBox dependency added in `native-pdf` profile.
2. **Runtime**: Spring context loads `scheduler-context.xml`; `reminderScheduler` triggers `overdueReminderJob.sendDueReminders` every weekday at 7 AM.
3. **Job Execution**: `OverdueReminderJob` queries `JdbcReminderRepository` (via `reminderDataSource`), formats emails with `SmtpMailGateway`, and sends reminders.
4. **External Interaction**: Warehouse systems send `shipmentNotice` XML validated against `shipment.xsd`; accounting systems generate `st:statement` XML transformed by `statement-to-html.xsl` into HTML reports.

#### Dependencies
- **Internal**: `org.example.reminder` classes (`JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`).
- **External**: HikariCP, PostgreSQL JDBC, Flyway, Jackson Databind, JUnit/Testcontainers, PDFBox (profile), Spring `task`/`context`.
- **Schema**: Standard XML Schema namespace; no external schema imports.

#### Cross-cutting
- Property placeholders (`reminder.*`) centralize configuration.
- Spring task scheduling and cron expressions standardize job timing.
- XML schemas enforce strict data contracts for shipment and statement documents.
- XSLT parameters (`currencySymbol`, `highlightAbove`) allow runtime customization of report rendering.
