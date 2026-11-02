### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:35:35Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation files that support an invoice‑and‑shipment service, including build setup, reminder scheduling, shipment validation, and statement rendering.

#### Purpose
- Build and package the Invoice Service with Maven.
- Configure a Spring reminder scheduler for overdue invoices.
- Validate shipment notice XML from warehouses.
- Transform monthly account statements into HTML.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with compiler, Flyway, PDF export profile.
- **Scheduling & Notification** – Spring XML wiring for data source, repository, mail gateway, job, and cron trigger.
- **Data Validation** – XML Schema (`shipment.xsd`) enforcing shipment notice structure.
- **Presentation** – XSLT (`statement-to-html.xsl`) converting statement XML to styled HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, dependencies, Flyway plugin, PDF profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema for `shipmentNotice` with `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`, `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT stylesheet producing `<html>` from `<st:statement>`, highlighting large bookings.

#### Data flow
1. **Build**: Maven compiles, runs Flyway migrations, packages JAR, optionally generates PDF via PDFBox.
2. **Scheduling**: Spring context loads data source → repository → job → scheduler; cron triggers `sendDueReminders` on `OverdueReminderJob`.
3. **Shipment**: External system sends XML; `shipment.xsd` validates structure before processing.
4. **Statement**: XML statement is fed to `statement-to-html.xsl`, producing an HTML report with totals and highlights.

#### Dependencies
- Maven: `jackson-databind`, `flyway-core`, `postgresql`, `junit-jupiter`, `testcontainers:postgresql`, `pdfbox`.
- Spring: beans, context, task schemas; HikariCP, custom `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`.
- XML: XSD namespace `urn:example:shipment:1`; XSLT namespace `urn:example:statement`.

#### Cross-cutting
- **Configuration placeholders**: `context:property-placeholder` for database and SMTP settings.
- **Naming conventions**: XML elements use qualified names (`shp`, `st` prefixes).
- **Error handling**: Schema validation ensures data integrity; XSLT applies conditional classes for highlighting.
- **Threading**: Spring scheduler pool size set to 2 for concurrent reminder jobs.
