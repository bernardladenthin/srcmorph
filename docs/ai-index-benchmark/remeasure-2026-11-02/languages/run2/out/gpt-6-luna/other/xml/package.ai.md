### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:41:09Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines billing and reminder-service configuration alongside XML contracts for shipment notices and account-statement rendering.

#### Purpose
- Configures the invoice Maven module and the reminder service’s database, mail delivery, and schedule.
- Defines shipment-notice validation rules and transforms account-statement XML into HTML.

#### Responsibilities
- **Invoice build and migrations:** Sets Java 17 build properties, runtime and test dependencies, Flyway migration location, and an optional PDFBox profile.
- **Overdue reminders:** Wires database access, SMTP delivery, and a weekday morning job using settings from `reminder.properties`.
- **Shipment message validation:** Specifies required notice, recipient, carrier, and parcel data, including carrier choices and parcel-weight limits.
- **Statement presentation:** Produces HTML with account and period details, booking rows, conditional amount highlighting, and a total.

#### Key units
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml` — Spring configuration for reminder components and their schedule.
- `reminderRepository` (`JdbcReminderRepository`) — accesses reminder data through `reminderDataSource`.
- `mailGateway` (`SmtpMailGateway`) — provides SMTP delivery for the reminder job.
- `overdueReminderJob` (`OverdueReminderJob`) — sends due reminders with a 14-day grace period.
- `shipmentNotice` / `ShipmentNoticeType` — XML shipment-notice element and its structured content.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — schema types constraining shipment carriers, parcel weights, and recipient details.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet for rendering statement XML as HTML.

#### Data flow
- The reminder scheduler invokes `OverdueReminderJob.sendDueReminders` at 07:00 Monday–Friday; the job is wired to the JDBC repository and SMTP gateway.
- Shipment notice XML is checked against `shipment.xsd`, including required notice and parcel fields, enumerated carrier codes, and weight constraints.
- Statement XML in `urn:example:statement` is transformed into HTML, with booking amounts formatted and values above `highlightAbove` highlighted.

#### Dependencies
- `invoice-service` depends on Jackson, Flyway, PostgreSQL at runtime, JUnit Jupiter and Testcontainers for tests; PDFBox is added by the `native-pdf` profile.
- Reminder configuration uses HikariCP, `org.example.reminder` repository, gateway, and job classes, `org.example.reminder.web` component scanning, and `reminder.properties`.
- `shipment.xsd` uses XML Schema built-in types and the `urn:example:shipment:1` namespace.
- The XSLT consumes the `urn:example:statement` vocabulary.

#### Cross-cutting
- Runtime settings are externalized through Maven properties or Spring property placeholders; the reminder scheduler uses a two-thread pool.
- Shipment XML uses qualified elements; recipient country defaults to `DE`, and parcel weights must be positive integers up to 31,500 grams.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, and formats amounts to two decimal places.
