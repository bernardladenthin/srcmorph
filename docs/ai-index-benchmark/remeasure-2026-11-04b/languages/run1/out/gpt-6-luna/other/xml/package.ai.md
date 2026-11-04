### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:01Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides XML-based build, scheduling, validation, and presentation configuration for billing, reminders, shipments, and account statements.

#### Purpose
- Defines service build and runtime scheduling configuration alongside XML formats and transformations for shipment notices and account statements.
- Supports invoice-service dependencies and migrations, overdue-reminder delivery, shipment validation, and statement-to-HTML rendering.

#### Responsibilities
- **Build and migrations:** `invoice-service-pom.xml` configures the `invoice-service` JAR, PostgreSQL/Flyway dependencies, migration location, tests, and optional PDFBox support.
- **Reminder scheduling:** `scheduler-context.xml` wires a JDBC repository, SMTP gateway, and overdue-reminder job, scheduled for weekday mornings.
- **Shipment validation:** `shipment.xsd` defines shipment notice structure, supported carrier codes, recipient address fields, and parcel weight constraints.
- **Statement presentation:** `statement-to-html.xsl` renders account statements as HTML with booking rows, totals, currency formatting, and threshold-based highlighting.

#### Key units
- `invoice-service` Maven project — builds version `2.7.0` with Java release 17 and a `native-pdf` profile.
- `reminderDataSource` — HikariCP data source configured from reminder database properties.
- `reminderRepository` — `JdbcReminderRepository` backed by `reminderDataSource`.
- `mailGateway` — `SmtpMailGateway` configured for the reminder SMTP host on port 587.
- `overdueReminderJob` — `OverdueReminderJob` combining repository and mail gateway with a 14-day grace period.
- `reminderScheduler` — Spring task scheduler with pool size 2; invokes `sendDueReminders` at 07:00 Monday–Friday.
- `shipmentNotice` / `ShipmentNoticeType` — shipment document schema requiring order reference, timestamp, carrier, recipient, and one or more parcels.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — schema types constraining carriers, parcel weights, parcel metadata, and recipient details.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet rendering statement headers, bookings, and totals.

#### Data flow
- Reminder properties configure the data source and SMTP gateway; the repository and gateway feed `OverdueReminderJob`, which the scheduler invokes on weekdays.
- Shipment notice XML is checked against `shipment.xsd`, including carrier enumeration, required identifiers, and parcel weight limits.
- Statement XML in `urn:example:statement` is transformed by `statement-to-html.xsl` into HTML with formatted booking amounts and a summed total.
- Maven build configuration resolves service dependencies and Flyway migrations; Surefire receives the invoice archive directory as a system property.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0`; uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optionally PDFBox.
- Flyway migrations are located at `src/main/resources/db/migration`.
- Reminder context references HikariCP and `org.example.reminder` classes; loads `reminder.properties` and scans `org.example.reminder.web`.
- Shipment schema uses XML Schema built-in types; statement transformation consumes elements in `urn:example:statement`.

#### Cross-cutting
- Configuration is expressed in XML: Maven properties and profiles, Spring beans and property placeholders, XSD constraints, and XSLT parameters.
- Reminder runtime settings include `reminder.db.url`, `reminder.db.user`, and `reminder.smtp.host`; statement rendering defaults to `EUR` and highlights amounts above `1000`.
- Shipment defaults recipient country to `DE` and caps parcel weight at 31,500 grams.
