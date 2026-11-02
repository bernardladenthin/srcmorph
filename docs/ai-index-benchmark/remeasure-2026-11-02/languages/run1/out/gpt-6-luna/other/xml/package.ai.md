### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:24:46Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing and account operations by configuring invoice production and reminders, validating shipment notices, and rendering account statements as HTML.

#### Purpose
- Defines build and runtime configuration for invoice processing and overdue-reminder delivery.
- Provides XML contracts and transformations for shipment notices and monthly account statements.

#### Responsibilities
- **Invoice service build:** Maven packaging, Flyway migrations, test configuration, and optional PDFBox support.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, and weekday overdue-reminder execution.
- **Shipment validation:** XSD rules for shipment identifiers, addresses, carriers, and parcel details.
- **Statement presentation:** XSLT rendering of statement details, booking rows, and totals with configurable currency and highlighting.

#### Key units
- `invoice-service` — Maven JAR for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml` — Spring context wiring the reminder repository, mail gateway, and scheduled job.
- `shipmentNotice` / `ShipmentNoticeType` — shipment-notice schema entry point and structure.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — shipment validation types, including supported carriers and parcel limits.
- `statement-to-html.xsl` — transforms `urn:example:statement` documents into HTML.
- `bookingsByDay` — XSLT key indexing bookings by value date.

#### Data flow
- Invoice service build configuration points Flyway to `src/main/resources/db/migration` and passes the archive directory to tests.
- Reminder configuration loads `reminder.properties`, wires a JDBC repository and SMTP gateway into `OverdueReminderJob`, then invokes `sendDueReminders` on weekdays at 07:00.
- Shipment notice XML is validated against `shipment.xsd`, which constrains required fields, parcel sequencing, carrier codes, and weights.
- Statement XML is transformed into HTML containing account and period details, formatted booking rows, and a summed total; rows above `highlightAbove` receive the `large` class.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0`; uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers, with PDFBox in the optional `native-pdf` profile.
- Reminder scheduling uses Spring beans/context/task schemas, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `classpath:reminder.properties`; connects to JDBC and SMTP.
- `shipment.xsd` uses XML Schema built-in types and the `urn:example:shipment:1` vocabulary.
- `statement-to-html.xsl` uses XSLT 1.0 and the `urn:example:statement` vocabulary.

#### Cross-cutting
- XML-based configuration and data contracts define runtime wiring, validation, and presentation.
- Invoice compilation targets Java 17; reminder scheduler uses a two-thread pool and a database connection pool capped at eight.
- Invoice archiving is configured under `${project.build.directory}/archive`; reminder job grace period is 14 days.
