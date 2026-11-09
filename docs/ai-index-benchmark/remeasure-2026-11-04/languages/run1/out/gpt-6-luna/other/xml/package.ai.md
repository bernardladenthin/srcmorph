### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:59Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines billing and reminder runtime configuration alongside shipment-message validation and account-statement rendering.

#### Purpose
- Provides XML build and Spring scheduling configuration for invoice and overdue-reminder services.
- Defines the warehouse shipment notice contract and transforms account statements into HTML.

#### Responsibilities
- **Build and migrations:** `invoice-service-pom.xml` configures the `invoice-service` JAR, Flyway migrations, test runtime, and optional PDFBox support.
- **Reminder scheduling:** `scheduler-context.xml` wires database access, SMTP delivery, and weekday execution of overdue reminders.
- **Shipment validation:** `shipment.xsd` constrains shipment notice structure, carriers, parcel weights, and recipient addresses.
- **Statement presentation:** `statement-to-html.xsl` renders statement details and booking totals, highlighting large amounts.

#### Key units
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, with Java 17 compilation and a `native-pdf` profile.
- `reminderDataSource` — pooled `HikariDataSource` configured from reminder database properties.
- `reminderRepository` — `JdbcReminderRepository` backed by `reminderDataSource`.
- `mailGateway` — `SmtpMailGateway` configured for the SMTP host and port 587.
- `overdueReminderJob` — `OverdueReminderJob` using the repository and mail gateway, with a 14-day grace period.
- `reminderScheduler` — Spring task scheduler that runs `sendDueReminders` at 07:00 Monday through Friday.
- `shipmentNotice` / `ShipmentNoticeType` — shipment document containing order, dispatch, carrier, recipient, and one or more parcels.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — schema types constraining shipment carrier, weight, parcel, and address data.
- `/st:statement` and `st:booking` templates — XSLT rules that render the statement page and booking rows.
- `statementHeader` and `statementTotal` — named XSLT templates for account details and booking totals.

#### Data flow
- Spring loads `reminder.properties`, configures the reminder data source and mail gateway, and invokes `OverdueReminderJob.sendDueReminders` on the weekday schedule.
- Shipment notices are validated against `urn:example:shipment:1`, including required notice and parcel attributes and a maximum parcel weight of 31,500 grams.
- Statement XML in `urn:example:statement` is transformed into HTML with account details, formatted booking amounts, a total, and `large` styling for amounts above the configured threshold.
- Maven builds the invoice service and directs Flyway to migration files under `src/main/resources/db/migration`.

#### Dependencies
- `invoice-service-pom.xml` inherits from `org.example.billing:billing-parent:2.7.0`; uses Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL; optionally uses PDFBox.
- `scheduler-context.xml` references `reminder.properties`, HikariCP, and `org.example.reminder` repository, gateway, and job classes; scans `org.example.reminder.web`.
- `shipment.xsd` uses XML Schema built-in types and its `urn:example:shipment:1` types.
- `statement-to-html.xsl` consumes the `urn:example:statement` vocabulary and uses XSLT `format-number()`, `sum()`, and `number()`.

#### Cross-cutting
- External configuration uses Maven properties, Spring property placeholders, and XSLT parameters.
- The reminder data source has an explicit `close` destroy method; reminder scheduling uses a two-thread scheduler pool.
- Statement formatting defaults to currency symbol `EUR`, highlight threshold `1000`, and amount format `#,##0.00`.
