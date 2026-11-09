### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:50Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides build, configuration, and data‑formatting support for an invoicing and reminder system, including Maven build, Spring scheduling, XML schema for shipments, and XSLT for statement rendering.  

#### Purpose
- Build and package the `invoice-service` JAR with database migration and optional PDF support.  
- Configure a Spring reminder scheduler that sends overdue notifications.  
- Define XML contracts for shipment notices and account statements.  
- Transform statement XML into styled HTML.

#### Responsibilities
- **Build & Dependency Management** – Maven POM with Flyway, Jackson, PostgreSQL, PDFBox.  
- **Scheduling & Messaging** – Spring context for reminder jobs, data source, repository, mail gateway.  
- **Data Validation** – XML Schema (`shipment.xsd`) for shipment notices.  
- **Presentation** – XSLT (`statement-to-html.xsl`) to render statements as HTML.

#### Key units
- `invoice-service-pom.xml` – Maven project definition, Flyway migration location, compiler release 17.  
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; scheduler `reminderScheduler`.  
- `shipment.xsd` – Schema for `shipmentNotice` with carrier, parcel, and address types.  
- `statement-to-html.xsl` – XSLT stylesheet producing HTML from `st:statement` XML, highlighting amounts > 1000.

#### Data flow
1. **Build**: Maven compiles source, runs Flyway migrations from `src/main/resources/db/migration`, packages JAR.  
2. **Runtime**: Spring context loads `scheduler-context.xml`; `reminderScheduler` triggers `overdueReminderJob.sendDueReminders()` daily at 07:00 UTC.  
3. **Shipment**: External warehouse emits `shipmentNotice` XML validated against `shipment.xsd`.  
4. **Statement**: Service receives `st:statement` XML, applies `statement-to-html.xsl` to produce an HTML page.

#### Dependencies
- **Internal**: `org.example.reminder.JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`.  
- **External libraries**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox (profile).  
- **Frameworks**: Spring Core, Spring Context, Spring Task Scheduler.  
- **Data**: PostgreSQL database for reminders; XML schemas for shipments and statements.

#### Cross-cutting
- **Configuration**: Property placeholders (`reminder.properties`) for DB and SMTP settings.  
- **Error handling**: Not explicitly defined in summaries; assumed standard Spring and JDBC exception propagation.  
- **Threading**: Scheduler pool size set to 2, allowing concurrent job execution.  
- **Naming conventions**: XML namespaces (`urn:example:shipment:1`, `urn:example:statement`) and XSLT parameters (`currencySymbol`, `highlightAbove`).
