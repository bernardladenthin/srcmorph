### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:06Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines XML configuration, validation, and presentation assets for billing, overdue reminders, warehouse shipments, and account statements.

#### Purpose
- Configure the invoice-service build and reminder scheduling.
- Specify shipment-notice validation and transform account statements into HTML.

#### Responsibilities
- **Build and billing:** `invoice-service-pom.xml` defines the Maven JAR, database migrations, test setup, and optional PDF support.
- **Reminder delivery:** `scheduler-context.xml` wires database access, SMTP delivery, and a weekday job for overdue items.
- **Shipment validation:** `shipment.xsd` constrains shipment, recipient, carrier, parcel, and weight data.
- **Statement presentation:** `statement-to-html.xsl` renders statement details, booking rows, and totals, highlighting large amounts.

#### Key units
- `invoice-service` Maven project — packages the service with Flyway migrations and an optional `native-pdf` profile.
- `overdueReminderJob` — invokes `OverdueReminderJob.sendDueReminders` on weekday mornings with a 14-day grace period.
- `shipmentNotice` / `ShipmentNoticeType` — defines the shipment payload, including required notice ID and one or more parcels.
- `CarrierCode` / `WeightGrams` — restrict carrier values and parcel weight to supported values and a maximum of 31,500 grams.
- `/st:statement` template — renders the HTML statement and applies booking templates.
- `statementHeader`, `statementTotal`, and `bookingsByDay` — provide header rendering, summed total rendering, and a booking key by value date.

#### Data flow
- Spring loads `reminder.properties`, configures the data source and mail gateway, and runs the reminder job against the repository.
- Shipment notices are checked against `shipment.xsd` constraints.
- Namespaced statement XML is transformed into HTML, with booking amounts formatted and totaled.

#### Dependencies
- Build: parent `org.example.billing:billing-parent:2.7.0`; Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional Apache PDFBox.
- Reminder context: HikariCP and `org.example.reminder` repository, SMTP gateway, and job classes; settings from `reminder.properties`.
- Shipment schema: XML Schema built-in types and internal shipment types in `urn:example:shipment:1`.
- Statement stylesheet: statement vocabulary in `urn:example:statement`; XSLT `format-number()` and `sum()`.

#### Cross-cutting
- Runtime and build settings are property-driven: Maven archive and migration locations, reminder database and SMTP settings, and XSLT currency and highlighting thresholds.
- Reminder scheduling uses a two-thread scheduler and a cron schedule of 07:00, Monday–Friday.
