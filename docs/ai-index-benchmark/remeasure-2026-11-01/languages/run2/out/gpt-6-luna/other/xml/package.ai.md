### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:39:43Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing and customer operations through invoice-service build configuration, scheduled overdue reminders, validated shipment notices, and HTML statement rendering.

#### Purpose
- Defines build and runtime configuration for invoice archiving and weekday reminder delivery.
- Provides XML contracts and presentation rules for warehouse shipment notices and monthly account statements.

#### Responsibilities
- **Invoice service build:** Maven coordinates, Java 17 compilation, Flyway migrations, archive-directory configuration, and optional PDFBox support.
- **Reminder scheduling:** Spring wiring for the reminder database repository, SMTP gateway, and weekday overdue-reminder job.
- **Shipment validation:** XML Schema constraints for carrier, recipient, parcel, weight, and tracking data.
- **Statement presentation:** XSLT conversion of account statement data into HTML, including booking rows, totals, currency, and large-amount highlighting.

#### Key units
- `invoice-service` Maven module — builds the invoice service JAR and configures dependencies, migrations, tests, and optional native PDF support.
- `scheduler-context.xml` — configures reminder beans and schedules `OverdueReminderJob.sendDueReminders`.
- `shipmentNotice` / `ShipmentNoticeType` — defines the shipment notice document structure and required `noticeId`.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — constrain shipment carriers, parcel weights and identifiers, and recipient addresses.
- `statement-to-html.xsl` — renders statement account details, booking rows, and formatted total as HTML.

#### Data flow
- Reminder properties configure the Hikari data source and SMTP gateway; `JdbcReminderRepository` and `SmtpMailGateway` are injected into `OverdueReminderJob`, whose due-reminder operation is scheduled for weekday mornings.
- Shipment notice XML is validated against `shipment.xsd`, enforcing required shipment and parcel fields plus carrier and weight constraints.
- Statement XML in `urn:example:statement` is transformed by `statement-to-html.xsl` into an HTML document with a configurable currency symbol and large-booking threshold.
- Maven build configuration supplies invoice-service dependencies and Flyway migration resources, with the archive directory passed to tests as a system property.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0`; uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers, with PDFBox in the `native-pdf` profile.
- Flyway migrations are located at `src/main/resources/db/migration`; tests receive `invoice.archive.dir`.
- Reminder configuration depends on `classpath:reminder.properties`, Spring beans/context/task schemas, HikariCP, and the `org.example.reminder` repository, gateway, and job classes.
- Reminder delivery uses the configured database URL and SMTP host on port `587`; web components are scanned from `org.example.reminder.web`.
- `shipment.xsd` uses XML Schema built-in types; the XSLT consumes statement elements in `urn:example:statement`.

#### Cross-cutting
- Externalized reminder database and SMTP settings use property placeholders; the reminder data source is closed via its configured destroy method.
- Reminder scheduling uses a two-thread pool and runs at 07:00 Monday through Friday, with a configured 14-day grace period.
- Shipment schema defaults recipient country to `DE`, requires positive parcel weights up to `31500` grams, and allows one or more parcels.
- Statement output defaults to `EUR`, highlights amounts above `1000`, and formats amounts and totals as `#,##0.00`.
