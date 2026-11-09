### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:06:12Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, scheduling, and XML schema/transformations for an invoicing and reminder system.

#### Purpose
- Build the `invoice-service` JAR and manage migrations.
- Wire a reminder scheduler that sends overdue notices.
- Validate shipment notices from warehouses.
- Render monthly account statements as HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM, compiler settings, Flyway migrations, test dependencies, optional PDF support.
- **Scheduling** – Spring context for a reminder job, data source, repository, mail gateway, cron trigger.
- **Shipment Validation** – XML Schema for shipment notices, carrier codes, parcel weight limits, address defaults.
- **Statement Rendering** – XSLT stylesheet that transforms statement XML into an HTML page with highlighting.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, dependencies, Flyway plugin, PDF profile.
- `scheduler-context.xml` – Spring beans: data source, repository, mail gateway, reminder job, scheduler, cron task.
- `shipment.xsd` – XML Schema for `shipmentNotice` with `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType`.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, bookings, and total.

#### Data flow
- Maven build → `invoice-service` JAR → Flyway migrations → database.
- Scheduler triggers `OverdueReminderJob.sendDueReminders` → repository fetches overdue records → mail gateway sends emails.
- Warehouse emits `shipmentNotice` XML → validated against `shipment.xsd`.
- Statement XML → XSLT transforms to HTML → rendered in browser.

#### Dependencies
- **External libraries**: Jackson‑databind, Flyway‑core, PostgreSQL driver, JUnit‑Jupiter, Testcontainers‑PostgreSQL, PDFBox (profile).
- **Maven plugins**: Flyway‑maven‑plugin, Surefire‑plugin.
- **Spring**: task scheduling, context, component scanning.
- **HikariCP**: JDBC data source.
- **XML**: standard XSD namespace, XSLT namespace, statement namespace.

#### Cross-cutting
- Property placeholders (`reminder.properties`) for database and SMTP settings.
- XML namespaces: `urn:example:shipment:1`, `urn:example:statement`.
- Default values in schema (`country` defaults to `"DE"`).
- XSLT parameters with defaults (`currencySymbol='EUR'`, `highlightAbove=1000`).
