### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:38:34Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing operations by configuring invoice-service builds and reminder delivery, validating shipment notices, and rendering account statements as HTML.

#### Purpose
- Groups XML assets for invoice-service packaging and migrations, scheduled overdue reminders, shipment-notice validation, and statement presentation.
- Defines build dependencies, Spring scheduler wiring, shipment data constraints, and statement formatting rules.

#### Responsibilities
- **Invoice service build:** Maven configuration for the `invoice-service` JAR, Flyway migrations, test execution, archive directory, and optional PDFBox profile.
- **Reminder delivery:** Spring bean wiring for database access, reminder persistence, SMTP, and weekday scheduling of overdue reminders.
- **Shipment validation:** XML Schema rules for shipment identifiers, addresses, carriers, and parcel weights.
- **Statement presentation:** XSLT transformation from monthly statement XML to an HTML report with formatted totals and threshold-based booking highlights.

#### Key units
- `invoice-service-pom.xml` — configures Java 17 Maven build, dependencies, Flyway migration location, tests, and the `native-pdf` profile.
- `scheduler-context.xml` — wires `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`; schedules `sendDueReminders` at 07:00 Monday through Friday.
- `shipment.xsd` — defines `shipmentNotice` and its `ShipmentNoticeType`, `AddressType`, `ParcelType`, `CarrierCode`, and `WeightGrams` constraints.
- `statement-to-html.xsl` — renders statement account, period, booking rows, and total; highlights booking amounts above `highlightAbove`.

#### Data flow
- Reminder configuration supplies database and SMTP properties to Spring-managed beans; `OverdueReminderJob` uses the repository and mail gateway when the scheduler invokes it.
- Shipment-notice XML is validated against `urn:example:shipment:1`, including allowed carrier codes, required identifiers, and parcel weight limits.
- Statement XML in `urn:example:statement` is transformed into HTML with formatted booking amounts and a calculated total.
- Maven builds `invoice-service`, applies migrations from `src/main/resources/db/migration`, and passes the configured archive directory to tests.

#### Dependencies
- Build: parent `org.example.billing:billing-parent:2.7.0`; Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional Apache PDFBox.
- Reminder context: `classpath:reminder.properties`; HikariCP; `org.example.reminder` repository, gateway, and job classes; Spring beans, context, and task schemas; component scan of `org.example.reminder.web`.
- Shipment schema: XML Schema built-in types.
- Statement transform: XSLT 1.0 and input elements in `urn:example:statement`.

#### Cross-cutting
- Configuration is externalized through Maven properties and the reminder properties file; scheduler settings include a pool size of 2, database pool maximum of 8, and a 14-day reminder grace period.
- Document constraints are explicit: shipment carriers are limited to `DHL`, `UPS`, `GLS`, and `POST`; parcel weights are capped at 31,500 grams; statement amounts use `#,##0.00` formatting.
