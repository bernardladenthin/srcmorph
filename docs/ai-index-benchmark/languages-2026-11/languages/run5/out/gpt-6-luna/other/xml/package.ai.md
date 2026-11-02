### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:35:05Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing operations with invoice build configuration, scheduled overdue reminders, shipment-notice validation, and HTML account-statement rendering.

#### Purpose
- Provides XML build and runtime configuration plus schema and transformation assets for billing-related workflows.
- Covers invoice packaging, reminder delivery, shipment data validation, and statement presentation.

#### Responsibilities
- **Invoice service build:** Configures the Java 17 `invoice-service` JAR, Flyway migrations, test dependencies, archive-directory property, and optional PDFBox support.
- **Reminder scheduling:** Wires database persistence and SMTP delivery into a weekday overdue-reminder job.
- **Shipment validation:** Defines required shipment-notice fields and constraints for carriers, addresses, parcels, and weights.
- **Statement rendering:** Transforms statement XML into an HTML page with booking rows, totals, and threshold-based highlighting.

#### Key units
- `invoice-service-pom.xml` — Maven configuration for `org.example.billing:invoice-service`, including migration and test setup.
- `scheduler-context.xml` — Spring context configuring `reminderDataSource`, `reminderRepository`, `mailGateway`, and `overdueReminderJob`.
- `reminderScheduler` — Runs `sendDueReminders` at 07:00 Monday through Friday with scheduler pool size 2.
- `shipment.xsd` — Schema for `urn:example:shipment:1`, defining `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, and `WeightGrams`.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet rendering `urn:example:statement` data as HTML.

#### Data flow
- The reminder context loads `reminder.properties`, configures database and SMTP beans, and schedules `OverdueReminderJob.sendDueReminders` with a 14-day grace period.
- Shipment notices are checked against `shipment.xsd`, including a required notice ID, one or more parcels, and parcel weights capped at 31,500 grams.
- Statement XML is transformed into HTML containing account and period details, booking rows, and a formatted total; amounts above `highlightAbove` are marked `large`.

#### Dependencies
- Invoice build: parent POM `org.example.billing:billing-parent:2.7.0`; Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; optional PDFBox profile.
- Reminder context: HikariCP, `org.example.reminder.JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and components in `org.example.reminder.web`.
- Reminder settings: `reminder.properties` supplies database URL/user and SMTP host; SMTP port is 587.
- Shipment schema: XML Schema built-in types and internal types in `urn:example:shipment:1`.
- Statement stylesheet: statement elements in `urn:example:statement` and XSLT functions including `sum()` and `format-number()`.

#### Cross-cutting
- XML assets use explicit namespaces for Maven, Spring, shipment, and statement data.
- Runtime and build behavior is property-driven, including database/SMTP settings, invoice archive location, statement currency symbol, and highlighting threshold.
