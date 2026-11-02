### other/xml
- H: 1.0
- C: 35BC444A
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:33:55Z
- G: 
- A: 0.0.0
- X: package
- F: [invoice-service-pom.xml](invoice-service-pom.xml.ai.md)
- F: [scheduler-context.xml](scheduler-context.xml.ai.md)
- F: [shipment.xsd](shipment.xsd.ai.md)
- F: [statement-to-html.xsl](statement-to-html.xsl.ai.md)
---
> Provides XML-based build, scheduling, shipment-validation, and account-statement rendering support for billing and operations workflows.

#### Purpose
- Defines the invoice-service Maven build, including database migration and optional PDF dependencies.
- Supplies XML configuration and transformation assets for reminder delivery, shipment notices, and account statements.

#### Responsibilities
- **Invoice build:** Maven coordinates, Java 17 compiler settings, dependencies, Flyway migration location, test configuration, and optional `native-pdf` profile.
- **Reminder scheduling:** Spring wiring for database access, SMTP delivery, overdue-reminder processing, and weekday execution.
- **Shipment validation:** XML Schema rules for notice identifiers, recipient addresses, carrier codes, parcel sequences, and weights.
- **Statement presentation:** XSLT transformation of monthly statements into HTML, including booking rows, totals, currency formatting, and threshold highlighting.

#### Key units
- `invoice-service` — Maven JAR project for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — Spring context defining the reminder repository, mail gateway, overdue job, and scheduler.
- `shipmentNotice` / `ShipmentNoticeType` — shipment-notice schema entry point and record structure.
- `CarrierCode`, `WeightGrams`, `AddressType`, `ParcelType` — schema types constraining shipment fields and parcel details.
- `statement-to-html.xsl` — XSLT stylesheet rendering statement headers, bookings, and totals.
- `statementHeader`, `statementTotal`, `bookingsByDay` — XSLT templates and key supporting statement rendering and booking lookup.

#### Data flow
- `scheduler-context.xml` loads `reminder.properties`, configures the data source and SMTP gateway, then invokes `OverdueReminderJob.sendDueReminders` on weekdays at 07:00.
- Shipment notice XML is validated against `shipment.xsd`, which constrains identifiers, carrier values, addresses, and parcel weights.
- Statement XML is transformed by `statement-to-html.xsl` into an HTML page with formatted booking amounts and a calculated total.
- The Maven build resolves service dependencies and configures Flyway migrations from `src/main/resources/db/migration`.

#### Dependencies
- Invoice build: `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL; optional Apache PDFBox.
- Reminder context: HikariCP and `org.example.reminder` repository, SMTP gateway, and overdue job; scans `org.example.reminder.web`.
- Reminder settings: `classpath:reminder.properties`, including `reminder.db.url`, `reminder.db.user`, and `reminder.smtp.host`.
- Shipment schema: XML Schema built-in types and the `urn:example:shipment:1` namespace.
- Statement transform: input elements in `urn:example:statement` and XSLT functions `number()`, `format-number()`, and `sum()`.

#### Cross-cutting
- XML assets centralize workflow configuration and data-format contracts; the shipment schema uses qualified elements in its target namespace.
- Reminder scheduling uses a connection pool of 8 and scheduler pool of 2; reminder grace period is 14 days.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, and formats amounts as `#,##0.00`.
- Invoice tests receive `invoice.archive.dir`, configured to `${project.build.directory}/archive`; Flyway migration tooling uses version `11.4.0`.
