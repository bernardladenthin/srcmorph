### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:56:37Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation artifacts that support the Invoice Service, a reminder scheduler, shipment notifications, and statement rendering.

#### Purpose
- Build and package the Invoice Service JAR with database migration and optional PDF support.
- Wire and schedule a reminder job for overdue invoices.
- Validate outbound shipment notices from the warehouse.
- Transform monthly account statements into styled HTML pages.

#### Responsibilities
- **Build & Deployment** – Maven POM with dependencies, compiler settings, Flyway migrations, and a PDF profile.
- **Scheduling** – Spring XML context that configures data source, repository, mail gateway, job, and a cron‑based scheduler.
- **Data Validation** – XML Schema for shipment notices, defining carrier, parcel, and recipient structures.
- **Presentation** – XSLT stylesheet that renders `<st:statement>` XML into HTML, highlighting large bookings.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, PDF profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; `reminderScheduler` with cron `0 0 7 * * MON-FRI`.
- `shipment.xsd` – Schema for `<shipmentNotice>` with `CarrierCode`, `ParcelType`, `AddressType`, and `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT that outputs HTML, uses parameters `currencySymbol` and `highlightAbove`.

#### Data flow
1. **Invoice Service**: Maven builds JAR → Flyway applies migrations → optional PDF generation via PDFBox.
2. **Reminder Scheduler**: Spring context loads beans → `overdueReminderJob.sendDueReminders()` runs at 7 AM weekdays → queries `reminderRepository` → sends emails via `mailGateway`.
3. **Shipment Notice**: Warehouse emits XML → validated against `shipment.xsd` before transmission.
4. **Statement Rendering**: `<st:statement>` XML → XSLT transforms to HTML, applying `class="large"` to rows where `amount > 1000`.

#### Dependencies
- **Internal**: `org.example.reminder` package (repository, gateway, job), `org.example.billing` parent POM.
- **External**: HikariCP, Flyway, PostgreSQL driver, JUnit, Testcontainers, PDFBox (profile), Spring beans/task schemas.
- **Data**: `reminder.properties` for DB and SMTP settings; XML namespaces `urn:example:shipment:1` and `urn:example:statement`.

#### Cross-cutting
- **Configuration**: Property placeholders (`${...}`) for database URLs, SMTP host/port, and archive directory.
- **Error handling**: Not explicitly defined; relies on Spring and Flyway defaults.
- **Threading**: Scheduler pool size set to 2; job execution concurrent with Spring task executor.
- **Versioning**: Java 17, Jackson 2.19.2, Flyway 11.4.0, PDFBox 3.0.5.
