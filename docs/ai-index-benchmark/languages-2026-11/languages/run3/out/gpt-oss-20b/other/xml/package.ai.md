### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:23Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation files that support the Invoice Service’s build, scheduling, shipment validation, and statement rendering.

#### Purpose
- Build and package the Invoice Service with Maven.
- Configure a Spring-based reminder scheduler.
- Validate shipment notice XML documents.
- Transform monthly account statements into HTML.

#### Responsibilities
- **Build Configuration** – Maven POM with dependencies, plugins, and PDF export profile.
- **Scheduler Setup** – Spring XML defining data source, repository, mail gateway, job, and cron trigger.
- **Shipment Validation** – XML Schema for shipment notices (carrier, parcel, address).
- **Statement Rendering** – XSLT stylesheet converting statement XML to HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDFBox profile.
- `scheduler-context.xml` – Spring context for `OverdueReminderJob`, `JdbcReminderRepository`, `SmtpMailGateway`.
- `shipment.xsd` – `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode` enum.
- `statement-to-html.xsl` – XSLT templates: `statementHeader`, `booking`, `statementTotal`; parameters `$currencySymbol`, `$highlightAbove`.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations, packages JAR, optionally generates PDF via PDFBox.
2. **Scheduling**: Spring context loads data source → repository → job; cron triggers `sendDueReminders` → mail gateway sends emails.
3. **Shipment**: XML documents validated against `shipment.xsd` before processing.
4. **Statement**: XML statement → XSLT → HTML output for display or export.

#### Dependencies
- Maven: `jackson-databind`, `flyway-core`, `postgresql`, `pdfbox` (profile), testing libs.
- Spring: `HikariCP`, custom `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`.
- XML: XSD core (`xs`), custom `shp` namespace; XSLT 1.0 processor.

#### Cross-cutting
- Property placeholders (`${...}`) used in Spring and Maven for external configuration.
- Consistent use of `urn:example:...` namespaces for domain types.
- Default values and constraints defined in XSD and XSLT parameters.
