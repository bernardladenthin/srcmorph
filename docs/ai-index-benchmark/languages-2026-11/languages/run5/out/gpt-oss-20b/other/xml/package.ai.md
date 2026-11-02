### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:36:35Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build configuration, scheduling, XML schema, and XSLT transformation for an invoicing and shipment system.

#### Purpose
- Build and package the Invoice Service with PDF export capability.
- Configure a reminder scheduler for overdue invoices.
- Define a shipment notice XML schema.
- Transform monthly account statements to HTML.

#### Responsibilities
- **Build & Dependencies** – Maven POM with compiler, Jackson, Flyway, PostgreSQL, PDFBox.
- **Scheduling** – Spring XML context wiring a data source, repository, mail gateway, job, and cron trigger.
- **Schema Validation** – XSD for shipment notices (carrier, parcel, address).
- **Presentation** – XSLT to render statements, highlight large bookings.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, `native-pdf` profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.
- `statement-to-html.xsl` – Templates: `statementHeader`, `booking`, `statementTotal`; parameters `currencySymbol`, `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles, tests, runs Flyway migrations, packages JAR, optionally adds PDFBox for PDF export.
2. **Scheduling**: At 07:00 Mon‑Fri, `overdueReminderJob.sendDueReminders()` queries `reminderRepository`, sends emails via `mailGateway`.
3. **Shipment**: Warehouse emits XML validated against `shipment.xsd`; consumer parses `ShipmentNoticeType`.
4. **Statement Rendering**: XSLT consumes `<st:statement>` XML, outputs HTML with highlighted rows.

#### Dependencies
- **Internal**: `org.example.reminder` components, `org.example.billing` parent POM.
- **External**: Jackson, Flyway, PostgreSQL, PDFBox, HikariCP, JUnit, Testcontainers.
- **Resources**: `reminder.properties`, `db/migration` scripts, `statement` XML namespace.

#### Cross-cutting
- Property placeholders (`${...}`) for database, SMTP, and archive paths.
- Spring component scanning (`org.example.reminder.web`).
- XML namespaces (`urn:example:shipment:1`, `urn:example:statement`).
- Default values in XSD (`country="DE"`, `WeightGrams` max 31500).
- XSLT parameters with defaults (`currencySymbol='EUR'`, `highlightAbove=1000`).
