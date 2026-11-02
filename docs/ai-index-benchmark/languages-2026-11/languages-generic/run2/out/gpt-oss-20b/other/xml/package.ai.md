### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:35:42Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, scheduling, and XML transformation utilities for an invoicing and shipment system.

#### Purpose
- Build the Invoice Service JAR with Maven, including optional PDF support.
- Configure a Spring scheduler to send overdue reminder emails.
- Define XML schemas for shipment notices and XSLT for statement rendering.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with Flyway, Jackson, PostgreSQL, and test containers.
- **Scheduling & Email** – Spring beans for data source, repository, SMTP gateway, and a daily reminder job.
- **XML Validation** – XSD for shipment notices enforcing carrier codes, weight limits, and address defaults.
- **XML Transformation** – XSLT stylesheet converting statement XML to HTML, highlighting large bookings.

#### Key units
- `invoice-service-pom.xml` – Maven configuration for the Invoice Service.
- `scheduler-context.xml` – Spring context defining `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`.
- `shipment.xsd` – Schema for `<shipmentNotice>` with `CarrierCode`, `WeightGrams`, and `AddressType`.
- `statement-to-html.xsl` – XSLT stylesheet rendering `<st:statement>` to HTML, exposing `statementHeader()` and `statementTotal()` templates.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations against PostgreSQL, packages JAR.
2. **Scheduling**: Spring initializes beans → `overdueReminderJob.sendDueReminders()` runs daily at 7 AM → queries `JdbcReminderRepository`, sends emails via `SmtpMailGateway`.
3. **Shipment**: External system produces XML; `shipment.xsd` validates structure before processing.
4. **Statement**: XML statement → `statement-to-html.xsl` → HTML output with highlighted rows.

#### Dependencies
- **Maven**: Jackson Databind, Flyway Core, PostgreSQL driver, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (optional).
- **Spring**: HikariCP, task scheduling, context.
- **XML**: XSD namespace `urn:example:shipment:1`, XSLT 1.0, namespace `urn:example:statement`.

#### Cross-cutting
- Central configuration via property placeholders (`reminder.db.url`, `reminder.smtp.host`).
- Consistent use of default values (e.g., `currencySymbol='EUR'`, `highlightAbove=1000`).
- Error handling delegated to framework (Spring bean creation, XML validation, XSLT runtime).
