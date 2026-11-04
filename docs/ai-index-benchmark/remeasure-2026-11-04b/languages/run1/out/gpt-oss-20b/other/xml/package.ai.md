### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:38Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides configuration and schema artifacts for an invoicing and reminder system, including build setup, scheduling, shipment validation, and statement rendering.

#### Purpose
- Define Maven build and dependencies for the Invoice Service.
- Configure Spring components for reminder scheduling.
- Validate shipment notices via XML Schema.
- Transform account statements from XML to HTML.

#### Responsibilities
- **Build & Deployment** – Maven POM with compiler, Flyway, PDF profile.
- **Scheduling** – Spring context wiring for reminder jobs and cron triggers.
- **Data Validation** – XSD for shipment notices.
- **Presentation** – XSLT for converting statements to HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration path, PDF profile.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, scheduler and cron trigger.
- `shipment.xsd` – `ShipmentNoticeType` schema with carrier codes, parcel weights, addresses, and notice metadata.
- `statement-to-html.xsl` – XSLT stylesheet with parameters `$currencySymbol`, `$highlightAbove`, templates for header, bookings, and total.

#### Data flow
1. **Build**: Maven compiles code, runs Flyway migrations, optionally packages PDF generation.
2. **Scheduling**: Spring context loads data source → repository → job → scheduler → cron trigger → job executes, sending reminders via mail gateway.
3. **Shipment**: External system emits XML; `shipment.xsd` validates structure before processing.
4. **Statement**: XML statement is fed to `statement-to-html.xsl`, producing an HTML page with highlighted bookings.

#### Dependencies
- Maven: `org.example.billing:billing-parent`, Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFBox (profile).
- Spring: `task`, `context`, `beans`; HikariCP; custom reminder classes (`JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`).
- XML: standard XSD namespace; XSLT 1.0.

#### Cross-cutting
- Configuration via property placeholders (`reminder.properties`).
- Use of namespaces for XML (`urn:example:shipment:1`, `urn:example:statement`).
- Consistent use of default values (e.g., `country="DE"`, `$currencySymbol='EUR'`).
- Parameterized XSLT for threshold highlighting.
