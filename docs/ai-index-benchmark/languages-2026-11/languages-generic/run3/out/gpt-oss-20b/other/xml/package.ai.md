### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:41:52Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build configuration, scheduling, and XML/XSLT resources for an invoice‑handling service.

#### Purpose
- Configure Maven build and dependencies for the Invoice Service.
- Define a Spring scheduler that sends overdue reminder emails.
- Supply XML schemas and XSLT for shipment notices and account statements.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with Flyway, Jackson, PostgreSQL, PDFBox (optional).
- **Scheduling & Email** – Spring beans for data source, repository, mail gateway, and a job that runs weekdays at 7 AM.
- **XML Validation** – `shipment.xsd` validates shipment notices (carrier codes, weight limits, address defaults).
- **Report Generation** – `statement-to-html.xsl` transforms statement XML into an HTML report with highlighting.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration configuration.
- `scheduler-context.xml` – Spring bean definitions: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – XML Schema for `shipmentNotice` with carrier enumeration, weight limits, and address defaults.
- `statement-to-html.xsl` – XSLT stylesheet producing an HTML account statement, with parameters `currencySymbol` and `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles Java sources, runs Flyway migrations, packages `invoice-service.jar`.
2. **Scheduling**: `reminderScheduler` triggers `overdueReminderJob.sendDueReminders()` → repository queries overdue records → `mailGateway` sends emails.
3. **Validation**: Incoming shipment XML is validated against `shipment.xsd` before processing.
4. **Reporting**: Statement XML is transformed by `statement-to-html.xsl` into an HTML page for display or email.

#### Dependencies
- **Internal**: `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob` (Spring beans).
- **External**: Jackson Databind, Flyway Core, PostgreSQL driver, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (optional), HikariCP, Spring Framework (beans, context, task).
- **Data**: `reminder.properties` for DB and SMTP settings; `src/main/resources/db/migration` for Flyway scripts.

#### Cross-cutting
- Configuration via XML (POM, Spring, XSD, XSLT) rather than code.
- Reusable constants: `graceDays=14`, `maximumPoolSize=8`, `port=587`.
- Default values in XSD (`AddressType.country` defaults to `DE`) and XSLT (`currencySymbol='EUR'`, `highlightAbove=1000`).
- No explicit exception handling in configuration files; relies on framework defaults.
