### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:28:59Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines XML-based build, scheduling, validation, and presentation configuration for invoice, reminder, shipment, and account-statement workflows.

#### Purpose
- Groups configuration and transformation artifacts supporting invoice-service builds, overdue reminders, shipment-notice validation, and statement rendering.
- Defines no shared runtime business logic across these distinct workflows.

#### Responsibilities
- **Build and test configuration:** Maven setup for `invoice-service`, including Java 17 compilation, Flyway migrations, test dependencies, and optional PDFBox support.
- **Reminder scheduling and wiring:** Spring beans for database access, SMTP delivery, overdue-reminder execution, and weekday scheduling.
- **Shipment data validation:** XML schema rules for shipment notices, recipient addresses, carriers, and parcels.
- **Statement presentation:** XSLT transformation from statement XML to formatted HTML with configurable currency and amount highlighting.

#### Key units
- `invoice-service-pom.xml` — builds the invoice-service JAR and configures migrations, tests, and optional PDF support.
- `scheduler-context.xml` — wires `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`; schedules `sendDueReminders`.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment notice structure, required content, and notice ID.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — constrain carrier values, parcel weights and sequence, and recipient address fields.
- `statement-to-html.xsl` — transforms `st:statement` XML into an HTML page with booking rows and a total.

#### Data flow
- Maven consumes project properties and migration scripts to build and test `invoice-service`; the `native-pdf` profile adds PDFBox.
- `scheduler-context.xml` loads reminder properties, configures a Hikari data source and JDBC repository, then injects the repository and SMTP gateway into `OverdueReminderJob`; the scheduler invokes `sendDueReminders` on weekday mornings.
- Shipment-notice XML is checked against `shipment.xsd` for required structure and value constraints.
- Statement XML is transformed by `statement-to-html.xsl` into an HTML statement, with parameters controlling currency display and booking highlighting.

#### Dependencies
- **Build:** `billing-parent:2.7.0`, Jackson Databind, Flyway Core and Maven plugin, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL, Maven Surefire; optional Apache PDFBox.
- **Reminder configuration:** Spring Beans, Context, and Task; HikariCP; `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`; `reminder.properties` supplies database and SMTP settings.
- **XML artifacts:** XML Schema built-in types and XSLT 1.0/XPath functions.

#### Cross-cutting
- Configuration is expressed through Maven properties, Spring XML properties and bean values, XSD constraints, and XSLT parameters.
- Reminder scheduling uses a pool size of `2`, SMTP port `587`, a 14-day grace period, and cron schedule `0 0 7 * * MON-FRI`; the data source closes via its `close` method.
- Schema validation rejects missing or misordered required content and invalid carrier, weight, or sequence values; no custom error handling is specified for the other artifacts.
