### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:42Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides XML-based build, scheduling, shipment-validation, and account-statement presentation artifacts for billing and fulfillment workflows.

#### Purpose
- Defines the `invoice-service` Maven build, weekday overdue-reminder scheduling, shipment notice validation, and statement-to-HTML transformation.
- Supplies configuration and data-format assets spanning invoice archiving, reminder delivery, warehouse shipments, and account statements.

#### Responsibilities
- **Invoice service build:** Declares Java 17 build settings, Jackson and Flyway dependencies, PostgreSQL runtime support, tests, database migration location, and optional PDFBox support.
- **Overdue reminders:** Wires database persistence and SMTP delivery, loads settings from `reminder.properties`, and schedules weekday processing.
- **Shipment notice contract:** Constrains shipment, recipient, carrier, and parcel data, including allowed carriers and parcel weight limits.
- **Statement rendering:** Transforms statement XML into HTML with booking rows, formatted amounts, threshold-based highlighting, and a total.

#### Key units
- `invoice-service` — Maven JAR coordinates `org.example.billing:invoice-service:2.7.0`, with Flyway migrations and optional `native-pdf` profile.
- `JdbcReminderRepository` — reminder persistence backed by `reminderDataSource`.
- `SmtpMailGateway` — SMTP service configured for the reminder workflow.
- `OverdueReminderJob` — sends due reminders with a 14-day grace period.
- `reminderScheduler` — runs `OverdueReminderJob.sendDueReminders` at 07:00 Monday through Friday.
- `shipmentNotice` / `ShipmentNoticeType` — XML shipment notice structure with required `noticeId` and one or more parcels.
- `CarrierCode`, `WeightGrams`, `AddressType`, `ParcelType` — shipment schema types for carrier, weight, recipient, and parcel details.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet rendering account statements and highlighting bookings above `highlightAbove`.

#### Data flow
- Spring loads `reminder.properties`, configures the Hikari data source and SMTP gateway, and invokes the overdue-reminder job on its weekday cron schedule.
- Shipment notices are validated against `shipment.xsd`, which checks notice metadata, recipient details, carrier codes, parcel sequence numbers, and weights.
- Statement XML in `urn:example:statement` is transformed into HTML with account details, booking rows, and a summed total.
- Maven builds the invoice-service JAR and configures Flyway to read migrations from `src/main/resources/db/migration`.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0`; uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers, with PDFBox in the optional `native-pdf` profile.
- Reminder configuration uses Spring Beans, Context, and Task schemas; HikariCP; `org.example.reminder` repository, gateway, and job classes; and component scanning of `org.example.reminder.web`.
- `shipment.xsd` uses XML Schema types and the target namespace `urn:example:shipment:1`.
- `statement-to-html.xsl` consumes statement elements in `urn:example:statement` and uses XSLT `number()`, `format-number()`, and `sum()`.

#### Cross-cutting
- Configuration uses property placeholders for reminder database and SMTP settings; the reminder scheduler has a pool size of 2, while its Hikari data source has a maximum pool size of 8.
- Invoice archive output defaults to `${project.build.directory}/archive` and is passed to tests as the `invoice.archive.dir` system property.
- Shipment constraints include carrier enumeration `DHL`, `UPS`, `GLS`, `POST`, a maximum weight of 31,500 grams, and default address country `DE`.
- Statement rendering defaults to currency symbol `EUR` and highlight threshold `1000`; amounts use `#,##0.00` formatting.
