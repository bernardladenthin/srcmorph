### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:38Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines XML-based build, scheduling, validation, and presentation artifacts for invoice, reminder, shipment, and account-statement workflows.

#### Purpose
- Configures the `invoice-service` Maven build and a weekday overdue-reminder job.
- Defines shipment-notice validation rules and transforms account-statement XML into HTML.

#### Responsibilities
- **Build configuration:** Java 17 Maven JAR build, Flyway migration setup, test configuration, and optional PDFBox support.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, and weekday execution of overdue reminders.
- **Shipment validation:** XSD rules for shipment notices, recipients, carriers, and parcels.
- **Statement rendering:** XSLT templates for account details, booking rows, amount highlighting, and totals.

#### Key units
- `org.example.billing:invoice-service` — Maven module configured with Jackson, Flyway, PostgreSQL, and test dependencies.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — Spring beans composing reminder persistence, mail delivery, and scheduled execution.
- `shp:shipmentNotice` / `ShipmentNoticeType` — shipment-notice document structure in `urn:example:shipment:1`.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — shipment field restrictions and parcel/address structures.
- `statementHeader()` and `statementTotal()` — XSLT templates rendering statement metadata and summed bookings.
- `/st:statement` and `st:booking` templates — render the statement page and individual booking rows.

#### Data flow
- Maven configuration builds the invoice-service JAR and directs Flyway to migration files under `src/main/resources/db/migration`.
- `reminder.properties` supplies database and SMTP settings; Spring wires the data source and repository with the mail gateway into `OverdueReminderJob`, scheduled for weekdays at 07:00.
- Shipment XML in `urn:example:shipment:1` is validated against required element order, fields, attributes, and value restrictions.
- Statement XML in `urn:example:statement` is transformed into HTML with booking amounts, threshold highlighting, and a calculated total.

#### Dependencies
- Maven build: `billing-parent:2.7.0`, Jackson Databind, Flyway Core, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL; PDFBox is enabled by the `native-pdf` profile.
- Reminder context: Spring Beans, Context, and Task; HikariCP; `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`; `reminder.properties`.
- Shipment schema: XML Schema built-in types and namespace `urn:example:shipment:1`.
- Statement stylesheet: XSLT 1.0 and XPath functions including `number()`, `format-number()`, and `sum()`.

#### Cross-cutting
- Configuration is expressed declaratively in Maven, Spring XML, XSD, and XSLT rather than application code.
- Reminder scheduling uses cron `0 0 7 * * MON-FRI`; its scheduler pool size is `2`, and the data-source maximum pool size is `8`.
- Shipment validation enforces carrier codes `DHL`, `UPS`, `GLS`, and `POST`, and parcel weights up to `31,500` grams.
- Statement rendering defaults to currency symbol `EUR` and highlights amounts above `1000`.
