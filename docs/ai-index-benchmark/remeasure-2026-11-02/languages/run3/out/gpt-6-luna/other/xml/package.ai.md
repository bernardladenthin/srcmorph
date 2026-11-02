### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:55:40Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines XML build and service configuration, shipment and statement data contracts, and statement-to-HTML presentation for billing and related business workflows.

#### Purpose
- Groups Maven build settings, Spring reminder scheduling, shipment-notice validation, and account-statement rendering.
- Establishes configuration and XML contracts used by invoice, reminder, shipment, and statement workflows.

#### Responsibilities
- **Build and invoice service:** `invoice-service-pom.xml` configures Java 17 packaging, dependencies, Flyway migrations, test archive settings, and optional PDFBox support.
- **Reminder delivery:** `scheduler-context.xml` wires database access, SMTP delivery, and a weekday overdue-reminder job.
- **Shipment validation:** `shipment.xsd` defines shipment notices, recipient addresses, carriers, and parcel constraints.
- **Statement presentation:** `statement-to-html.xsl` transforms namespaced statements into HTML with booking rows, amount highlighting, and a total.

#### Key units
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — Spring beans composing the reminder service and its schedule.
- `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams` — schema types defining and constraining shipment-notice content.
- `statementHeader`, `statementTotal`, `bookingsByDay` — XSLT named templates and key used to render statement details.

#### Data flow
- `scheduler-context.xml` supplies configured database and mail beans to `OverdueReminderJob`, which runs `sendDueReminders` on the configured weekday schedule.
- Shipment notice XML is validated against `shipment.xsd`, including carrier codes, required identifiers, and parcel weights.
- Statement XML in `urn:example:statement` is transformed by `statement-to-html.xsl` into HTML booking rows and a total.
- Maven configuration builds `invoice-service` and locates Flyway migrations under `src/main/resources/db/migration`.

#### Dependencies
- `invoice-service-pom.xml` inherits `org.example.billing:billing-parent:2.7.0` and configures Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional Apache PDFBox.
- `scheduler-context.xml` depends on `reminder.properties`, HikariCP, Spring beans/context/task schemas, and `org.example.reminder` repository, gateway, and job classes; it scans `org.example.reminder.web`.
- `shipment.xsd` uses XML Schema built-in types; its target namespace is `urn:example:shipment:1`.
- `statement-to-html.xsl` consumes statement elements in `urn:example:statement`.

#### Cross-cutting
- XML namespaces identify the shipment and statement vocabularies; the stylesheet also uses XSLT 1.0.
- Configuration is property-driven for reminder database and SMTP settings; reminder scheduling uses a two-thread pool and a 14-day grace period.
- Invoice build settings specify Java release 17, Flyway 11.4.0, and an archive directory under the Maven build directory.
