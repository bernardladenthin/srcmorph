### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:42:18Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A set of configuration, schema, and transformation artifacts that support an invoice‑generation service, a reminder scheduler, and XML‑based shipment and statement processing.

#### Purpose
- Build and package the Invoice Service with database migration and optional PDF support.
- Wire a Spring reminder system that sends overdue notices via email.
- Define XML contracts for shipment notices and provide XSLT for rendering account statements.

#### Responsibilities
- **Build & Deployment** – Maven POM with dependencies, plugins, and a native‑PDF profile.
- **Scheduling & Email** – Spring XML context configuring data source, repository, mail gateway, and a weekly overdue‑reminder job.
- **Data Validation** – XSD for `shipmentNotice` documents, enforcing carrier codes, weights, and address structure.
- **Presentation** – XSLT stylesheet that transforms `<st:statement>` XML into an HTML report with totals and conditional highlighting.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDFBox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; scheduler `reminderScheduler`.
- `shipment.xsd` – Schema defining `shipmentNotice`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`, `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, booking rows, and total.

#### Data flow
1. **Invoice Service**  
   - Maven build → JAR → Flyway migrations applied to PostgreSQL → optional PDF generation via PDFBox.
2. **Reminder Scheduler**  
   - Scheduler triggers `OverdueReminderJob.sendDueReminders()` weekly → `JdbcReminderRepository` queries overdue records → `SmtpMailGateway` sends emails.
3. **Shipment Notice**  
   - Warehouse emits XML matching `shipment.xsd`; validation ensures correct carrier, weight, and address.
4. **Statement Rendering**  
   - `<st:statement>` XML → `statement-to-html.xsl` → HTML page with highlighted bookings and total.

#### Dependencies
- **External Libraries**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox (profile), Spring Task Scheduling.
- **External Resources**: `classpath:reminder.properties`, SMTP server (`${reminder.smtp.host}`), PostgreSQL database (`${reminder.db.url}`).
- **Schema/Namespace**: XML Schema `http://www.w3.org/2001/XMLSchema`; custom namespaces `urn:example:shipment:1` and `urn:example:statement`.

#### Cross-cutting
- Property placeholders (`${...}`) used throughout Spring and Maven configurations.
- Consistent use of `xs:positiveInteger` and enumeration types in XSD for data integrity.
- XSLT 1.0 functions (`format-number`, `sum`, `number`, `xsl:key`) for numeric formatting and conditional styling.
- Scheduler cron expression `0 0 7 * * MON-FRI` for weekday 7 AM execution.
