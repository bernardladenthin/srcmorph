### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:54:57Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines billing and reminder-service configuration plus XML contracts and presentation rules for shipment and account-statement data.

#### Purpose
- Packages build and runtime configuration for invoice archiving and scheduled overdue reminders.
- Supplies a shipment-notice validation schema and an XSLT that renders account statements as HTML.

#### Responsibilities
- **Invoice service build:** Maven coordinates, Java 17 build settings, database-migration and test plugins, service dependencies, archive-directory configuration, and optional PDFBox support.
- **Reminder scheduling:** Spring wiring for database-backed reminder persistence, SMTP delivery, the overdue-reminder job, and weekday scheduling.
- **Shipment data contract:** Schema constraints for shipment identifiers, recipient addresses, carriers, parcel counts, and parcel weights.
- **Statement presentation:** HTML rendering of statement headers, booking rows, formatted amounts, totals, and threshold-based row highlighting.

#### Key units
- `invoice-service` — Maven JAR project using Flyway migrations and configuring `invoice.archive.dir`; the `native-pdf` profile adds PDFBox.
- `scheduler-context.xml` — Spring context defining `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and a weekday scheduler.
- `shipmentNotice` / `ShipmentNoticeType` — shipment document structure requiring an ID, order reference, shipment time, carrier, recipient, and one or more parcels.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — schema types constraining carrier values, parcel weight, parcel sequence, and recipient details.
- `statement-to-html.xsl` — XSLT 1.0 transformation for statement HTML, including configurable currency and large-booking threshold.
- `bookingsByDay` — XSLT key indexing bookings by `st:valueDate`.

#### Data flow
- Reminder configuration loads `reminder.properties`; Spring wires JDBC persistence and SMTP delivery into `OverdueReminderJob`, then invokes `sendDueReminders` on weekdays at 07:00 with a 14-day grace period.
- Shipment notices are validated against `shipment.xsd`, which constrains their structure and field values.
- Statement XML in `urn:example:statement` is transformed into HTML with booking rows and a summed total; amounts above `highlightAbove` receive the `large` class.
- The invoice Maven build configures Flyway migration files and passes the archive directory to tests.

#### Dependencies
- Invoice build: parent `org.example.billing:billing-parent:2.7.0`; Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, and optional PDFBox.
- Reminder context: Spring beans/context/task schemas; HikariCP; `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`; database and SMTP settings from `reminder.properties`.
- Shipment schema: XML Schema built-in types and target namespace `urn:example:shipment:1`.
- Statement transform: XSLT 1.0 functions and input elements in namespace `urn:example:statement`.

#### Cross-cutting
- External endpoints and presentation settings are parameterized: reminder database and SMTP properties, invoice archive directory, and statement currency and highlighting threshold.
- Shipment validation requires qualified elements; parcel weight is capped at 31,500 grams, and the optional recipient country defaults to `DE`.
- The reminder scheduler uses a pool of two threads; the JDBC connection pool allows up to eight connections.
