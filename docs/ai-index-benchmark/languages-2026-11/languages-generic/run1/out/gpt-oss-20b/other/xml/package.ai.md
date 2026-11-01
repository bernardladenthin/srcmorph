### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:32:05Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides configuration, schema, and transformation resources for invoice, reminder, shipment, and statement processing within the billing domain.

#### Purpose
- Central repository of XML‑based configuration, schema, and XSLT for billing‑related services.

#### Responsibilities
- **Build & dependency management** – Maven POM for the Invoice Service.
- **Scheduling & mail** – Spring bean definitions for reminder jobs.
- **Data validation** – XSD for shipment notices.
- **Presentation** – XSLT to render monthly statements as HTML.

#### Key units
- **`invoice-service-pom.xml`** – Maven POM configuring Java 17, Flyway migrations, PostgreSQL runtime, test containers, and optional PDFBox.
- **`scheduler-context.xml`** – Spring XML defining `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and a cron‑scheduled `TaskScheduler`.
- **`shipment.xsd`** – XML Schema for `<shipmentNotice>` documents, enforcing carrier codes, weight limits, and address defaults.
- **`statement-to-html.xsl`** – XSLT stylesheet converting `<st:statement>` XML into an HTML report, with configurable currency symbol and highlight threshold.

#### Data flow
1. **Invoice Service**: Maven builds JAR, Flyway applies DB migrations from `src/main/resources/db/migration`. Tests use Testcontainers PostgreSQL; PDF generation optional via `native-pdf` profile.
2. **Reminder Scheduler**: Spring loads properties, creates data source and repository, schedules `OverdueReminderJob.sendDueReminders` at 7 AM Mon‑Fri, which queries reminders and sends emails via `SmtpMailGateway`.
3. **Shipment Notice**: External warehouse emits XML; `shipment.xsd` validates structure before downstream processing.
4. **Statement Rendering**: Service produces `<st:statement>` XML; `statement-to-html.xsl` transforms it into an HTML page, highlighting large amounts.

#### Dependencies
- **Internal**: Spring framework, HikariCP, Flyway, PDFBox (optional), XSLT 1.0.
- **External**: PostgreSQL driver, Jackson databind, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox.
- **Resources**: `reminder.properties`, `shipment.xsd`, `statement-to-html.xsl`.

#### Cross-cutting
- **Configuration**: Property placeholders (`reminder.properties`) and Maven profiles (`native-pdf`).
- **Error handling**: Spring bean creation failures, Flyway migration errors, XML validation errors, XSLT runtime type conversion.
- **Consistency**: Namespace usage (`urn:example:shipment:1`, `urn:example:statement`), default values (country DE, currency EUR), and shared constants (carrier codes, weight limits).
