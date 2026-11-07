### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:04Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides XML-based build, scheduling, data-contract, and presentation configuration for billing, reminders, shipments, and account statements.

#### Purpose
- Defines supporting artifacts for invoice-service builds, overdue-reminder delivery, shipment notice validation, and statement-to-HTML rendering.
- Establishes build dependencies and runtime settings alongside XML data and presentation contracts.

#### Responsibilities
- **Build and migrations:** `invoice-service-pom.xml` configures the `invoice-service` JAR, Flyway migrations, test settings, and optional PDFBox support.
- **Reminder scheduling and wiring:** `scheduler-context.xml` connects the reminder repository, SMTP gateway, and job, scheduled for weekday mornings.
- **Shipment data contract:** `shipment.xsd` specifies shipment notices, recipients, carriers, parcels, and weight constraints.
- **Statement presentation:** `statement-to-html.xsl` transforms statement XML into HTML with formatted totals and threshold-based booking highlighting.

#### Key units
- `invoice-service` Maven project — builds `org.example.billing:invoice-service:2.7.0` with Java 17 configuration.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob` — Spring beans collaborating to send overdue reminders.
- `reminderScheduler` — runs `overdueReminderJob.sendDueReminders` at `0 0 7 * * MON-FRI`.
- `shipmentNotice` / `ShipmentNoticeType` — shipment contract requiring order, date, carrier, recipient, and one or more parcels.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — shipment schema types for carrier enumeration, parcel details, and recipient data.
- `statementHeader`, `statementTotal`, booking template — XSLT rendering units for statement metadata, booking rows, and summed amounts.

#### Data flow
- The Spring context loads `reminder.properties`, configures the datasource and SMTP gateway, then schedules the overdue-reminder job to use the repository and gateway.
- Statement XML in `urn:example:statement` is transformed into HTML containing account and period details, booking rows, and a total; bookings above `highlightAbove` are marked `large`.
- Shipment XML is validated against `shipment.xsd`, including required identifiers, enumerated carriers, and parcel weight limits.
- Maven builds the invoice-service JAR and directs Flyway to migration files under `src/main/resources/db/migration`.

#### Dependencies
- `invoice-service-pom.xml`: parent `org.example.billing:billing-parent:2.7.0`; Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL; optional PDFBox profile.
- `scheduler-context.xml`: HikariCP and `org.example.reminder` repository, SMTP gateway, and job classes; `org.example.reminder.web` component scan; database and SMTP settings supplied through `reminder.properties`.
- `shipment.xsd`: XML Schema built-in types and target namespace `urn:example:shipment:1`.
- `statement-to-html.xsl`: input namespace `urn:example:statement`.

#### Cross-cutting
- XML configuration uses externalized properties for reminder database and SMTP settings; invoice archive output is configured through `invoice.archive.dir`.
- Shipment constraints include carrier codes `DHL`, `UPS`, `GLS`, `POST`, a maximum parcel weight of 31,500 grams, and default country `DE`.
- Statement output defaults to `EUR`, formats amounts as `#,##0.00`, and highlights amounts above `1000`.
