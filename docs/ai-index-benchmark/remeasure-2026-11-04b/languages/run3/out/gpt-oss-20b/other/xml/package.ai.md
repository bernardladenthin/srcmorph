### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:49Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, configuration, and data‑formatting support for an invoicing and reminder system, including XML schemas, XSLT rendering, and Spring scheduling.

#### Purpose
- Build the `invoice-service` JAR with Maven, manage DB migrations, and optionally support PDF generation.
- Configure a Spring context for scheduling overdue reminder emails.
- Define XML schemas for shipment notices and XSLT for statement‑to‑HTML conversion.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with compiler, Flyway, test, and optional PDF dependencies.
- **Scheduling Infrastructure** – Spring XML defining data source, repository, mail gateway, job, and cron trigger.
- **Data Validation** – XML Schema (`shipment.xsd`) for shipment notices.
- **Presentation** – XSLT (`statement-to-html.xsl`) to transform statement XML into HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway plugin, optional `native-pdf` profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – Schema for `shipmentNotice` with `ParcelType`, `AddressType`, and `ShipmentNoticeType`.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, booking rows, and total.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations, packages JAR, optionally bundles PDF support.
2. **Scheduling**: Spring context loads data source → repository → job → scheduler; cron triggers `overdueReminderJob.sendDueReminders` → mail gateway sends emails.
3. **Shipment Notice**: External system generates XML validated against `shipment.xsd`.
4. **Statement Rendering**: Input `<st:statement>` XML is transformed by `statement-to-html.xsl` into an HTML page, highlighting large amounts.

#### Dependencies
- Maven: `org.flywaydb:flyway-core`, `com.fasterxml.jackson.core:jackson-databind`, `org.postgresql:postgresql`, testcontainers, JUnit, optional `org.apache.pdfbox:pdfbox`.
- Spring: `com.zaxxer.hikari.HikariDataSource`, task scheduler, context.
- XML: standard XSD namespace; XSLT 1.0 processor.

#### Cross-cutting
- Configuration via property placeholders (`reminder.properties`).
- Consistent use of namespace prefixes (`shp`, `st`) for XML documents.
- Parameterized XSLT for currency and threshold customization.
- Flyway migration location set to `src/main/resources/db/migration`.
