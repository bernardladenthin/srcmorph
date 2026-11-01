### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:44Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Defines build, scheduling, validation, and rendering support for invoice, overdue-reminder, shipment, and account-statement workflows.

#### Purpose
- Configures the invoice-service build and scheduled overdue-reminder dispatch.
- Defines XML contracts and presentation for shipment notices and monthly account statements.

#### Responsibilities
- **Build and testing:** Maven configuration for Java 17 invoice-service builds, Flyway migrations, PostgreSQL runtime access, and tests; PDFBox is optional.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, and weekday overdue-reminder execution.
- **Shipment validation:** XSD contract for shipment notices, recipients, carrier codes, and parcel constraints.
- **Statement presentation:** XSLT transformation from statement XML to formatted HTML with totals and large-amount highlighting.

#### Key units
- `invoice-service-pom.xml` — configures Maven dependencies, migrations, archive-directory test property, and the `native-pdf` profile.
- `scheduler-context.xml` — defines `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment XML document structure and required fields.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — constrain shipment carriers, parcel weights and details, and recipient addresses.
- `statement-to-html.xsl` — transforms `st:statement` documents into HTML using `statementHeader` and `statementTotal` templates.

#### Data flow
- Maven properties and module resources configure the invoice-service build, Flyway migrations, and test execution.
- `reminder.properties` supplies database and SMTP settings; Spring wires these into the repository and mail gateway, then schedules `OverdueReminderJob.sendDueReminders()` for weekday mornings.
- Shipment XML is checked against `shipment.xsd` for structure, ordering, required attributes, and value constraints.
- Statement XML is transformed into an HTML page with booking rows, formatted amounts, highlights, and a summed total.

#### Dependencies
- Build: `billing-parent:2.7.0`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; Apache PDFBox is profile-only.
- Reminder context: Spring beans, context, and task namespaces; HikariCP; `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`.
- XML artifacts: XSD built-in types and the `urn:example:shipment:1` namespace; XSLT 1.0 and the `urn:example:statement` namespace.

#### Cross-cutting
- Reminder scheduling uses a HikariCP pool capped at 8 connections, SMTP port 587, a 14-day grace period, and a two-thread scheduler.
- Shipment schema requires `noticeId` and parcel `sequence`; carrier values are `DHL`, `UPS`, `GLS`, or `POST`; parcel weight is positive and at most 31,500 grams; country defaults to `DE`.
- Statement rendering defaults to currency symbol `EUR` and highlights booking amounts above `1000`; amounts use `#,##0.00` formatting.
