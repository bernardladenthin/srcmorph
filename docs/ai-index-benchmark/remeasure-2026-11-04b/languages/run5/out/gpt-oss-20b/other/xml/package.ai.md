### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:00Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build configuration, database migration, PDF generation, reminder scheduling, shipment XML schema, and statement-to‑HTML transformation for a billing and logistics system.

#### Purpose
- Build and package the Invoice Service module.
- Manage database migrations and PDF generation.
- Configure a scheduled reminder system.
- Define shipment notice XML structure.
- Transform monthly statements to HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM with compiler, dependencies, Flyway, and PDF profile.
- **Database Migration** – Flyway integration for schema changes.
- **Reminder Scheduling** – Spring context wiring for data source, repository, mail gateway, job, and cron trigger.
- **XML Schema Definition** – Shipment notice schema for warehouse notifications.
- **XSLT Transformation** – Statement XML to styled HTML output.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, PDFbox profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – `shipmentNotice` root, `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, booking rows, and total.

#### Data flow
1. **Build**: Maven compiles Java, runs Flyway migrations, packages JAR, optionally adds PDFbox for PDF generation.
2. **Scheduling**: Spring context loads data source → repository → job → scheduled trigger → job executes `sendDueReminders` using `mailGateway`.
3. **Shipment**: Warehouse emits XML matching `shipment.xsd`; consumers validate against schema.
4. **Statement**: XML statement (namespace `urn:example:statement`) is fed to `statement-to-html.xsl`; XSLT produces an HTML page with styled rows and totals.

#### Dependencies
- Maven: `org.example.billing:billing-parent`, Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFbox (profile).
- Spring: `task`, `context`, HikariCP, custom reminder classes (`JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`).
- XML: `http://www.w3.org/2001/XMLSchema`, `urn:example:shipment:1`, `urn:example:statement`.

#### Cross-cutting
- **Configuration placeholders**: `reminder.properties` for DB and SMTP settings.
- **Naming conventions**: XML namespaces (`shp`, `st`), bean names (`reminder*`, `overdueReminderJob`).
- **Error handling**: Not explicitly detailed; relies on Spring and Flyway defaults.
- **Testing**: Maven test scope includes JUnit and Testcontainers for PostgreSQL.
