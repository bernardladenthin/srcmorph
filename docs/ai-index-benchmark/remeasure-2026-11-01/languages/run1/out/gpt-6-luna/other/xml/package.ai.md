### other/xml
- H: 1.0
- C: 1F3F74FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:16:45Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Supports billing and account operations through service build configuration, overdue-reminder scheduling, shipment notice validation, and statement-to-HTML rendering.

#### Purpose
- Defines XML resources for building the invoice service and configuring a weekday reminder workflow.
- Provides schemas and transformations for shipment notices and monthly account statements.

#### Responsibilities
- **Invoice service build:** Maven dependencies, Flyway migration location, test configuration, archive directory, and optional native PDFBox support.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, and weekday overdue-reminder execution.
- **Shipment data validation:** XML Schema constraints for shipment notices, recipient addresses, carriers, and parcels.
- **Statement presentation:** XSLT rendering of account statements as HTML, with formatted amounts and threshold-based booking highlights.

#### Key units
- `invoice-service` — Maven JAR module for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — Spring context defining reminder beans and scheduling `OverdueReminderJob.sendDueReminders`.
- `shipmentNotice` / `ShipmentNoticeType` — shipment document structure with required notice ID and one or more parcels.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — schema types constraining carriers, parcel weights and sequence, and recipient address fields.
- `statement-to-html.xsl` — XSLT 1.0 stylesheet generating statement HTML with configurable currency and highlight threshold.
- `statementHeader`, `statementTotal`, `bookingsByDay` — named templates and key for statement header/total generation and booking indexing.

#### Data flow
- Spring loads `reminder.properties`, supplies database and SMTP settings to the configured beans, and schedules `sendDueReminders` for weekdays at 07:00 with a 14-day grace period.
- A shipment notice is validated against `shipment.xsd`, including carrier codes, parcel weights, required fields, and parcel sequencing.
- A statement in the `urn:example:statement` namespace is transformed into HTML with account/period details, booking rows, and a formatted total.

#### Dependencies
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; its `native-pdf` profile adds PDFBox.
- Reminder configuration depends on HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, Spring task scheduling, and `classpath:reminder.properties`.
- `shipment.xsd` uses XML Schema built-in types; `statement-to-html.xsl` consumes the `urn:example:statement` input vocabulary and XSLT functions.

#### Cross-cutting
- Configuration is externalized through Maven properties and reminder property placeholders; reminder scheduling uses a two-thread scheduler.
- XML resources define explicit namespaces: `urn:example:shipment:1` for shipment data and `urn:example:statement` for statement input.
- Maven targets Java 17; Flyway migrations are configured from `src/main/resources/db/migration`.
