### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:28:16Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines supporting contracts, build configuration, scheduled processing, and presentation for billing, overdue reminders, shipment notices, and account statements.

#### Purpose
- Packages XML assets for the invoice-service build, weekday overdue-reminder scheduling, shipment-notice validation, and statement-to-HTML rendering.
- Provides configuration and data contracts used by those separate workflows.

#### Responsibilities
- **Invoice service build:** Maven coordinates, Java 17 build settings, Flyway migrations, test configuration, and optional PDFBox support.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, and a weekday 07:00 overdue-reminder job.
- **Shipment contract:** XSD validation for shipment notices, recipients, carriers, and parcels.
- **Statement presentation:** XSLT transformation of account statements into HTML, including totals and large-amount highlighting.

#### Key units
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml` — Spring context defining `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`.
- `ShipmentNoticeType` — shipment-notice structure with order reference, dispatch time, carrier, recipient, and parcels.
- `CarrierCode`, `WeightGrams`, `AddressType`, `ParcelType` — shipment value constraints and nested data types.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet rendering statement headers, booking rows, and totals.

#### Data flow
- Spring loads `reminder.properties`, configures database and mail services, then schedules `OverdueReminderJob.sendDueReminders` for weekdays at 07:00.
- Shipment notice XML is validated against `shipment.xsd`, which constrains carrier codes, parcel weights, required identifiers, and element order.
- Statement XML in `urn:example:statement` is transformed to HTML with formatted amounts, a computed total, and conditional highlighting.
- Maven builds the invoice-service JAR, runs configured tests, and locates Flyway migrations; the `native-pdf` profile adds PDFBox.

#### Dependencies
- Invoice build: `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL, and optional PDFBox.
- Reminder context: Spring beans/context/task schemas, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- Shipment schema: XML Schema built-in types and namespace `urn:example:shipment:1`.
- Statement stylesheet: input elements in `urn:example:statement` and XSLT functions `number()`, `format-number()`, and `sum()`.

#### Cross-cutting
- Configuration is externalized through Maven properties and `reminder.properties`; reminder database and SMTP values use placeholders.
- Reminder execution uses a scheduler pool of 2 and a database connection pool capped at 8.
- Shipment validation enforces positive parcel weights up to 31,500 grams and defaults an omitted recipient country to `DE`; statement rendering defaults to `EUR` and highlights amounts above `1000`.
