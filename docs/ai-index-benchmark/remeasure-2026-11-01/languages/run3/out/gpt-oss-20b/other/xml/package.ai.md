### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:58:14Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> A collection of configuration, schema, and transformation files that support an invoicing and reminder system, including a Spring scheduler, shipment XML validation, and HTML statement rendering.  

#### Purpose  
- Provide build, runtime, and data‑validation artifacts for the Invoice Service.  
- Enable scheduled reminder jobs and email notifications.  
- Validate shipment notices and render account statements to HTML.  

#### Responsibilities  
- **Build & Dependency Management** – Maven POM with Java 17, Jackson, Flyway, PostgreSQL, PDF export.  
- **Scheduling & Messaging** – Spring XML context wiring a reminder scheduler, data source, repository, mail gateway, and job trigger.  
- **Data Validation** – XML Schema (`shipment.xsd`) enforcing shipment notice structure and constraints.  
- **Presentation** – XSLT (`statement-to-html.xsl`) transforming statement XML into styled HTML.  

#### Key units  
- `invoice-service-pom.xml` – Maven project definition, dependencies, Flyway plugin, PDF profile.  
- `scheduler-context.xml` – Spring bean definitions for `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and scheduled trigger.  
- `shipment.xsd` – Schema for `shipmentNotice` with types `ShipmentNoticeType`, `ParcelType`, `AddressType`, and simple types `CarrierCode`, `WeightGrams`.  
- `statement-to-html.xsl` – XSLT stylesheet producing `<html>` from `<st:statement>`, highlighting large amounts and computing totals.  

#### Data flow  
1. **Build**: Maven compiles Java 17 code, runs Flyway migrations, packages JAR, optionally builds PDF export.  
2. **Runtime**: Spring context loads `scheduler-context.xml`; `reminderScheduler` triggers `overdueReminderJob.sendDueReminders` daily at 7 AM, which queries `reminderRepository` (via `reminderDataSource`) and sends emails via `mailGateway`.  
3. **Validation**: Incoming shipment XML is validated against `shipment.xsd` before processing.  
4. **Presentation**: Statement XML (`<st:statement>`) is transformed by `statement-to-html.xsl` into an HTML page for display or email.  

#### Dependencies  
- **Internal**: `org.example.reminder` classes (`JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`).  
- **External**: HikariCP, Jackson, Flyway, PostgreSQL driver, JUnit 5, Testcontainers, PDFBox (PDF export profile).  
- **Configuration**: `reminder.properties` for database and SMTP settings.  

#### Cross-cutting  
- Spring bean wiring via XML; property placeholders load from `reminder.properties`.  
- Consistent use of namespace prefixes (`st`, `shp`) across XML/XSLT.  
- Default values and constraints defined in XSD (e.g., `WeightGrams` ≤ 31500, `country` default “DE”).  
- XSLT parameters (`currencySymbol`, `highlightAbove`) allow runtime customization of output.
