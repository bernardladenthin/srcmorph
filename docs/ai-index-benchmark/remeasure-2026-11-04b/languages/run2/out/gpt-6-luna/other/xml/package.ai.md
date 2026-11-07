### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:29Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines XML-based build, scheduling, validation, and presentation configuration for billing, reminders, shipments, and account statements.

#### Purpose
- Packages the invoice service’s Maven build and configures weekday overdue-reminder delivery.
- Defines the XML contract for shipment notices and transforms account statements into HTML.

#### Responsibilities
- **Invoice service build:** Java 17 Maven JAR configuration, dependencies, Flyway migration setup, archive directory, test configuration, and optional PDFBox profile.
- **Reminder scheduling:** Spring wiring for the reminder datasource, repository, SMTP gateway, overdue-reminder job, and weekday schedule.
- **Shipment validation:** XSD constraints for shipment identifiers, recipient addresses, carriers, and parcels.
- **Statement rendering:** XSLT templates for statement headers, booking rows, totals, currency display, and large-amount highlighting.

#### Key units
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, with Flyway migrations and optional native PDF support.
- `scheduler-context.xml` — Spring context configuring `OverdueReminderJob.sendDueReminders` at 07:00 Monday–Friday.
- `shipmentNotice` / `ShipmentNoticeType` — shipment notice schema with order, dispatch, carrier, recipient, and one-or-more parcel details.
- `CarrierCode` / `WeightGrams` — schema restrictions for supported carriers and parcel weights up to 31,500 grams.
- `/st:statement` — XSLT template rendering statement details, booking rows, and total.
- `statementHeader`, `statementTotal` — named templates rendering account information and formatted booking sum.

#### Data flow
- Spring loads reminder settings from `reminder.properties`, wires database and SMTP components into `OverdueReminderJob`, and schedules its due-reminder operation on weekdays.
- Shipment notice XML is validated against `urn:example:shipment:1`, including required identifiers and parcel constraints.
- Statement XML in `urn:example:statement` is transformed into HTML; booking amounts are formatted and values above `highlightAbove` receive the `large` class.

#### Dependencies
- Invoice build: parent POM `org.example.billing:billing-parent:2.7.0`; Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, and profile-specific PDFBox.
- Reminder context: `reminder.properties`, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and scanned package `org.example.reminder.web`.
- Shipment schema uses XML Schema built-in types; statement stylesheet consumes statement elements in `urn:example:statement`.

#### Cross-cutting
- Configuration is expressed through Maven properties, Spring property placeholders, XSD defaults and restrictions, and XSLT parameters.
- Reminder database connections use a Hikari pool capped at 8; the scheduler pool size is 2.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, and formats amounts as `#,##0.00`.
