### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:47:49Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, scheduling, and XML/XSLT utilities for a billing and logistics system.

#### Purpose
- Maven build configuration for the invoice service.
- Spring context for a reminder scheduler.
- XML schema for shipment notices.
- XSLT stylesheet for rendering monthly statements.

#### Responsibilities
- **Build & Packaging** – Maven POM defines dependencies, compiler settings, Flyway migrations, and a PDF export profile.
- **Scheduling** – Spring XML wires a data source, repository, mail gateway, and a job that sends overdue reminders on weekdays.
- **Data Validation** – XSD defines the structure of shipment notices, including carrier codes, parcel weights, and recipient addresses.
- **Presentation** – XSLT transforms statement XML into an HTML page, highlighting large bookings and computing totals.

#### Key units
- `invoice-service-pom.xml` – Maven project descriptor for the invoice service.
- `scheduler-context.xml` – Spring bean definitions for the reminder scheduler.
- `shipment.xsd` – XML Schema for `shipmentNotice` documents.
- `statement-to-html.xsl` – XSLT stylesheet converting `<st:statement>` XML to HTML.

#### Data flow
1. **Build**: Maven compiles Java sources, runs Flyway migrations, and packages the jar; optional PDF export profile adds PDFBox.
2. **Scheduling**: Spring context loads `reminderDataSource`, `reminderRepository`, `mailGateway`, and `overdueReminderJob`; the scheduler triggers `sendDueReminders` every weekday at 7 AM.
3. **Shipment**: External systems generate `shipmentNotice` XML that validates against `shipment.xsd`.
4. **Statement Rendering**: An XML `<st:statement>` is fed to `statement-to-html.xsl`, producing an HTML report with highlighted rows.

#### Dependencies
- **External libs**: Jackson, Flyway, PostgreSQL driver, JUnit, Testcontainers, PDFBox (profile).
- **Spring**: beans, context, task schemas.
- **Database**: PostgreSQL via HikariDataSource.
- **Mail**: SMTP via `SmtpMailGateway`.
- **XML**: XSD self‑contained; XSLT 1.0.

#### Cross-cutting
- Configuration via property placeholders (`reminder.properties`).
- Scheduler uses a fixed thread pool (size 2) and cron expression.
- XSLT uses parameters for currency symbol and highlight threshold.
- XML schema enforces defaults (country `DE`) and value constraints (weight ≤ 31500 g).
