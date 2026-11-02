### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:56:51Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines billing, reminder-scheduling, shipment-validation, and account-statement presentation assets for business operations.

#### Purpose
- Provides build and migration configuration for the invoice service and Spring wiring for scheduled overdue reminders.
- Defines XML contracts and transformations for shipment notices and monthly account statements.

#### Responsibilities
- **Invoice service build:** Packages `invoice-service` as a JAR, configures Flyway migrations and tests, and offers optional PDFBox support through the `native-pdf` profile.
- **Reminder delivery:** Wires a database-backed repository and SMTP gateway into `OverdueReminderJob`, scheduled on weekday mornings.
- **Shipment notice validation:** Constrains shipment fields, carrier codes, parcel weights, and required identifiers and sequencing.
- **Statement rendering:** Transforms namespaced statements into HTML with booking rows, formatted totals, and threshold-based highlighting.

#### Key units
- `invoice-service` Maven POM — configures Java 17 build dependencies, Flyway, tests, and optional PDF support.
- `scheduler-context.xml` — defines reminder beans and the weekday scheduler.
- `shipmentNotice` / `ShipmentNoticeType` — schema entry point and structure for shipment notices, recipient addresses, and parcels.
- `CarrierCode` / `WeightGrams` — restrict carrier values and parcel weights to supported codes and a maximum of 31,500 grams.
- `statement-to-html.xsl` — renders statement account and period details, bookings, and totals as HTML.
- `bookingsByDay` — indexes statement bookings by value date.

#### Data flow
- `reminder.properties` supplies database and SMTP settings to `reminderDataSource` and `mailGateway`; `reminderRepository` and `mailGateway` feed `overdueReminderJob`, invoked by `reminderScheduler`.
- Shipment notices are validated against `shipment.xsd`, requiring an order reference, shipment timestamp, supported carrier, recipient, and one or more parcels.
- Namespaced statement XML is transformed by `statement-to-html.xsl` into HTML; booking amounts are formatted, summed, and highlighted when above `highlightAbove`.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL; PDFBox is added by `native-pdf`.
- Reminder wiring depends on Spring beans/context/task schemas, HikariCP, and `org.example.reminder` classes including `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`.
- Flyway migrations are located at `src/main/resources/db/migration`; tests receive `invoice.archive.dir`.
- Shipment schema uses XML Schema built-in types; the XSLT consumes statement elements in `urn:example:statement`.

#### Cross-cutting
- Reminder settings are property-driven; the Hikari connection pool is limited to 8 connections, and the scheduler uses a pool of 2.
- Overdue reminders use a 14-day grace period and run at 07:00 Monday through Friday.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, formats amounts as `#,##0.00`, and emits indented UTF-8 HTML.
