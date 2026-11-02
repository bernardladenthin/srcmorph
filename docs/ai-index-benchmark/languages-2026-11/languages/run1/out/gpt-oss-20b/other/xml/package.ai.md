### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:13:19Z
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
- Build and package the Invoice Service with PDF export support.
- Configure a Spring reminder scheduler for overdue invoices.
- Define XML schema for shipment notices.
- Transform monthly account statements to HTML.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with compiler, Jackson, Flyway, PostgreSQL, PDFBox.
- **Scheduling & Reminders** – Spring XML wiring of data source, repository, mail gateway, job, and cron trigger.
- **Shipment Validation** – XSD for shipment notices with carrier, weight, address, and parcel constraints.
- **Statement Rendering** – XSLT stylesheet converting statement XML to styled HTML, highlighting large amounts.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, native‑PDF profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; scheduler `reminderScheduler`.
- `shipment.xsd` – `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.
- `statement-to-html.xsl` – XSLT with parameters `$currencySymbol`, `$highlightAbove`; templates `statementHeader`, `statementTotal`, booking rows.

#### Data flow
1. **Build**: Maven compiles Java, runs Flyway migrations, packages JAR, optionally adds PDFBox for PDF export.
2. **Scheduling**: Spring loads `reminderDataSource` → `reminderRepository` → `overdueReminderJob`; scheduler triggers `sendDueReminders` every weekday at 7 AM.
3. **Shipment**: Warehouse emits XML validated against `shipment.xsd`; downstream services consume the notice.
4. **Statement**: XML statement fed to `statement-to-html.xsl`; XSLT outputs HTML with highlighted rows.

#### Dependencies
- **External libs**: Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFBox.
- **Spring**: task scheduler, HikariCP, component scanning.
- **XML**: XSD for shipment, XSLT 1.0 for statement rendering.

#### Cross-cutting
- Property placeholders (`reminder.properties`) for database and SMTP settings.
- Common naming conventions: `reminder*`, `shipment*`, `statement*`.
- Error handling via Spring’s task scheduler and XSD validation (implicit).
- Configuration files (`pom.xml`, `scheduler-context.xml`, `shipment.xsd`, `statement-to-html.xsl`) are the primary entry points for build, runtime, and data interchange.
