### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:05Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, data‑migration, scheduling, and XML/XSLT utilities for an invoicing and shipment system.

#### Purpose
- Build the Invoice Service JAR with Maven, including Flyway migrations and optional PDF support.
- Configure a Spring reminder scheduler that sends overdue reminders via email.
- Define XML schemas for shipment notices and XSLT for rendering account statements.

#### Responsibilities
- **Build & Deployment** – Maven POM, compiler settings, test dependencies, Flyway plugin.
- **Data Migration** – Flyway migrations located in `src/main/resources/db/migration`.
- **Scheduling** – Spring XML context wiring for a daily reminder job.
- **XML Validation** – `shipment.xsd` schema for shipment notices.
- **Presentation** – `statement-to-html.xsl` stylesheet for converting statement XML to HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway configuration, PDF profile.
- `scheduler-context.xml` – Spring bean definitions: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- `shipment.xsd` – XML Schema defining `shipmentNotice`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.
- `statement-to-html.xsl` – XSLT stylesheet rendering `st:statement` to HTML, highlighting amounts > `highlightAbove`.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations, packages JAR.
2. **Scheduler**: At 7 AM Mon‑Fri, `OverdueReminderJob.sendDueReminders` queries `reminderRepository`, uses `mailGateway` to email reminders.
3. **Shipment Notice**: XML documents validated against `shipment.xsd` before processing.
4. **Statement Rendering**: Input XML in `urn:example:statement` namespace is transformed by `statement-to-html.xsl` into an HTML page.

#### Dependencies
- **Internal**: `org.example.reminder` classes, `org.example.billing` parent POM.
- **External**: PostgreSQL (runtime), Flyway, Jackson, JUnit, Testcontainers, PDFBox (optional), HikariCP, Spring task/context modules.
- **Schema**: Standard XML Schema namespace; no external XSD imports.

#### Cross-cutting
- **Configuration**: Property placeholders (`reminder.properties`), Maven properties (`jackson.version`, `flyway.version`).
- **Error handling**: Not explicitly defined in summaries; assumed standard Spring and Flyway mechanisms.
- **Concurrency**: Scheduler pool size set to 2; HikariCP pool size 8.
- **Defaults**: `AddressType.country` defaults to `DE`; XSLT parameters `currencySymbol='EUR'`, `highlightAbove=1000`.
