### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:47Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing operations through invoice build configuration, scheduled overdue-reminder delivery, shipment-notice validation, and account-statement rendering.

#### Purpose
- Defines Maven build and test settings for the `invoice-service`, including database migrations and invoice archive configuration.
- Provides XML resources for reminder scheduling, shipment-message validation, and statement-to-HTML presentation.

#### Responsibilities
- **Invoice service build:** Packages the `invoice-service` JAR; configures Flyway migrations, test archive directory, and optional PDFBox support.
- **Reminder scheduling:** Wires JDBC persistence and SMTP delivery into a weekday job, with database and mail settings supplied by properties.
- **Shipment validation:** Specifies required shipment notice, recipient, carrier, and parcel fields and their constraints.
- **Statement presentation:** Transforms statement XML into HTML with booking details, totals, and threshold-based highlighting.

#### Key units
- `invoice-service-pom.xml` — Maven configuration for the Java 17 invoice module, dependencies, migrations, and tests.
- `scheduler-context.xml` — Spring context defining `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`.
- `shipment.xsd` — Schema for `shipmentNotice`, including `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, and `WeightGrams`.
- `statement-to-html.xsl` — XSLT templates for statement headers, booking rows, and totals; exposes `currencySymbol` and `highlightAbove`.

#### Data flow
- Reminder configuration resolves database and SMTP placeholders from `reminder.properties`, wires the repository and gateway into `OverdueReminderJob`, and schedules `sendDueReminders` for weekday mornings.
- Shipment XML is checked against `shipment.xsd`, including carrier enumeration, parcel weight limits, required identifiers, and recipient structure.
- Statement XML in `urn:example:statement` is transformed into HTML; bookings are formatted and summed, with rows marked `large` when amounts exceed `highlightAbove`.
- Maven builds the invoice JAR, runs Flyway from `src/main/resources/db/migration`, and passes the configured archive directory to tests.

#### Dependencies
- Build: parent `org.example.billing:billing-parent:2.7.0`; Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL; optional PDFBox.
- Reminder context: HikariCP, `org.example.reminder` repository, gateway, and job classes; scans `org.example.reminder.web`.
- XML resources: XML Schema built-in types and XSLT functions; statement input uses namespace `urn:example:statement`.

#### Cross-cutting
- XML resources use explicit namespaces; shipment elements are qualified in `urn:example:shipment:1`.
- Reminder settings use property placeholders; the Hikari data source is closed through its configured destroy method.
- Reminder scheduling uses a pool size of 2 and a cron schedule of `0 0 7 * * MON-FRI`; the job has a 14-day grace period.
- Invoice archive location defaults to `${project.build.directory}/archive`; PDF generation is enabled through the `native-pdf` profile.
