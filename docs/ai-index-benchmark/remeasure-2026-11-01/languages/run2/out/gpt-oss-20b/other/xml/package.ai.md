### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:41:06Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation files that support an invoice‑generation service, a reminder scheduler, and shipment/statement processing.

#### Purpose
- Build and configure the Invoice Service Maven module.
- Wire a Spring‑based reminder scheduler.
- Define XML contracts for shipment notices and account statements.
- Provide XSLT for rendering statements to HTML.

#### Responsibilities
- **Build & Dependency Management** – `invoice-service-pom.xml` sets Maven coordinates, compiler level, runtime/test dependencies, Flyway migrations, and a PDF export profile.
- **Scheduler Configuration** – `scheduler-context.xml` defines data source, repository, mail gateway, job, and cron trigger for overdue reminders.
- **XML Schema Definition** – `shipment.xsd` validates shipment notice documents with carrier, parcel, and address constraints.
- **XSLT Transformation** – `statement-to-html.xsl` converts a statement XML into an HTML page, highlighting large amounts and computing totals.

#### Key units
- `invoice-service-pom.xml` – Maven POM for the invoice service.
- `scheduler-context.xml` – Spring XML context for the reminder scheduler.
- `shipment.xsd` – XML Schema for `shipmentNotice` documents.
- `statement-to-html.xsl` – XSLT stylesheet for `st:statement` → HTML.

#### Data flow
1. **Build**: Maven compiles the invoice service, runs Flyway migrations, and optionally packages a PDF export via the `native-pdf` profile.
2. **Scheduler**: Spring loads `scheduler-context.xml`, creates a `JdbcReminderRepository`, `SmtpMailGateway`, and schedules `OverdueReminderJob` to send reminders at 7 AM weekdays.
3. **Shipment**: External systems generate `shipmentNotice` XML that is validated against `shipment.xsd`.
4. **Statement Rendering**: An XML document matching `st:statement` is transformed by `statement-to-html.xsl` into an HTML report.

#### Dependencies
- **Maven**: `flyway-core`, `jackson-databind`, `postgresql`, `pdfbox`, test libs.
- **Spring**: Beans, Context, Task schemas; HikariCP data source.
- **External services**: PostgreSQL database, SMTP server.
- **XML**: XSLT 1.0 processor for statement rendering.

#### Cross-cutting
- Property placeholders (`${...}`) used throughout Spring and Maven configurations.
- Consistent use of `urn:example:shipment:1` and `urn:example:statement` namespaces.
- Default values and constraints (e.g., `WeightGrams` ≤ 31500, `AddressType.country` default `DE`).
- Error handling is implicit in schema validation and Spring bean wiring.
