### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:17Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, configuration, and data‑formatting support for an invoicing and reminder system, including Maven setup, Spring scheduling, XML schema for shipments, and XSLT for statement rendering.  

#### Purpose  
- Build and dependency management for the Invoice Service.  
- Configure a scheduled reminder job for overdue invoices.  
- Define XML contracts for shipment notices.  
- Transform monthly statement XML into HTML.  

#### Responsibilities  
- **Build & Deployment** – Maven POM with compiler, Flyway, and PDF generation profile.  
- **Scheduling & Email** – Spring context wiring for a daily overdue‑reminder job.  
- **Data Validation** – XML Schema for shipment notices.  
- **Presentation** – XSLT stylesheet converting statement XML to HTML.  

#### Key units  
- `invoice-service-pom.xml` – Maven project definition, Flyway migration, PDF profile.  
- `scheduler-context.xml` – Spring bean definitions: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and scheduled trigger.  
- `shipment.xsd` – XML Schema (`shipmentNotice` element) with `ParcelType`, `AddressType`, and carrier enumeration.  
- `statement-to-html.xsl` – XSLT stylesheet with parameters `currencySymbol`, `highlightAbove`; templates for header, bookings, and total.  

#### Data flow  
1. **Build**: Maven compiles Java, runs Flyway migrations, packages JAR, optionally generates PDFs.  
2. **Runtime**: Spring context loads `scheduler-context.xml`; `overdueReminderJob` queries `reminderRepository` (via HikariDataSource), sends emails via `mailGateway`.  
3. **External Data**: Shipment notices validated against `shipment.xsd`; monthly statements transformed by `statement-to-html.xsl` into HTML pages.  

#### Dependencies  
- Maven: `jackson-databind`, `flyway-core`, `postgresql`, `pdfbox`, testing libs.  
- Spring: `context`, `task`; HikariCP for data source.  
- XSLT: relies on input XML in `urn:example:statement` namespace.  

#### Cross-cutting  
- Central configuration via property placeholders (`reminder.properties`).  
- Consistent use of `jdbcUrl`, `username`, and `maximumPoolSize` for data sources.  
- Scheduler uses cron expression `0 0 7 * * MON-FRI` and a pool size of 2.  
- XML schema defines default country `DE` and enumerated carrier codes.  
- XSLT parameters allow currency symbol and highlight threshold to be overridden.
