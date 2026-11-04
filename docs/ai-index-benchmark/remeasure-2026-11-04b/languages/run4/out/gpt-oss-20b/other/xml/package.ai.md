### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:04Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides infrastructure for invoicing, reminders, shipment validation, and statement rendering in a billing system.

#### Purpose
- Build and package the Invoice Service.
- Configure a scheduled reminder job for overdue invoices.
- Validate shipment notices from warehouses.
- Transform monthly account statements into HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM with dependencies, compiler settings, Flyway migrations, and optional PDF support.
- **Scheduling & Email** – Spring context wiring for a reminder scheduler that queries a JDBC repository and sends emails via SMTP.
- **XML Validation** – XSD schema defining the structure of shipment notices.
- **Presentation** – XSLT stylesheet converting statement XML into styled HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, Surefire configuration.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema for `<shipmentNotice>` with `CarrierCode`, `ParcelType`, `AddressType`.
- `statement-to-html.xsl` – XSLT stylesheet with templates `statementHeader`, `statementTotal`, and booking row rendering.

#### Data flow
1. **Build**: Maven compiles Java, runs Flyway migrations, packages JAR, optionally includes PDFBox.
2. **Scheduling**: Spring context loads; `reminderScheduler` triggers `overdueReminderJob.sendDueReminders` every weekday at 7 AM.
3. **Reminder Job**: `JdbcReminderRepository` queries overdue invoices → `SmtpMailGateway` sends reminder emails.
4. **Shipment Validation**: XML documents are validated against `shipment.xsd` before processing.
5. **Statement Rendering**: Statement XML (namespace `urn:example:statement`) is transformed by `statement-to-html.xsl` into an HTML page with totals and highlighted rows.

#### Dependencies
- **Internal**: `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`.
- **External Libraries**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers, PDFBox (profile), HikariCP, Spring Task & Context.
- **External Resources**: `reminder.properties` for DB and SMTP settings; `src/main/resources/db/migration` for Flyway scripts.

#### Cross-cutting
- **Configuration**: Property placeholders (`reminder.properties`), Maven properties (`maven.compiler.release`, `invoice.archive.dir`).
- **Error Handling**: Not explicitly detailed; relies on Spring and JDBC exception propagation.
- **Threading**: Scheduler pool size set to 2; HikariCP pool size 8.
- **Naming Conventions**: XML namespaces (`urn:example:shipment:1`, `urn:example:statement`), bean names follow Spring conventions.
