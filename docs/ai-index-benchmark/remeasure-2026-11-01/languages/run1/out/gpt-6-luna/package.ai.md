### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:16:59Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational and billing workflows for analyzing service traffic, managing deployments and workers, exporting payments, scheduling reminders, validating shipments, and rendering account statements.

#### Purpose
- Groups tools and configuration for service operations, payment processing, and billing-related document workflows.
- Supports reporting, scheduled jobs, XML validation, and statement presentation.

#### Responsibilities
- **Traffic analysis:** Parse access logs, apply host aliases and exclusions, aggregate request statistics, and emit text or JSON reports.
- **Service operations:** Deploy tagged releases with health checks and dry-run support; manage queue-worker lifecycle and heartbeat checks.
- **Payment exports:** Query Oracle for settled payments and produce daily compressed CSV output with row-count records.
- **Billing and reminders:** Configure the invoice-service Maven build and schedule weekday overdue-reminder jobs.
- **Billing documents:** Validate shipment notices against an XML Schema and transform account statements into HTML.

#### Key units
- `LogDigest` and `log_digest.pl::main` — aggregate access-log statistics and format CLI output.
- `deploy_release.sh` — deploy a tagged archive to selected hosts, restart `orderd`, and poll service health.
- `healthcheck.sh` — manage `queue-worker` and check process status and heartbeat freshness.
- `nightly_extract.ksh` — export settled-payment data from Oracle to compressed CSV and write a row-count control file.
- `invoice-service` — Maven JAR module configured for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — wires reminder dependencies and schedules `OverdueReminderJob.sendDueReminders`.
- `shipmentNotice` / `ShipmentNoticeType` — define shipment document structure and parcel requirements; `CarrierCode`, `WeightGrams`, `ParcelType`, and `AddressType` constrain shipment fields.
- `statement-to-html.xsl` — transform namespaced account statements into HTML with formatted totals and threshold-based booking highlights.

#### Data flow
- Access-log files plus alias and exclusion settings feed `LogDigest`, which emits aggregated text or JSON.
- Deployment options select hosts and a build archive; the archive is copied and activated before service restart and HTTP health polling.
- Worker lifecycle commands inspect PID and heartbeat files and invoke the configured worker executable.
- Business-day and directory settings drive the Oracle `settled_payment` query; successful output is counted, recorded in a control file, and gzipped.
- Reminder properties configure database and SMTP beans for the scheduled job; shipment XML is schema-validated, and statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, host lists, `rsync`, SSH, `curl`, and remote `systemctl`; worker checks use `/usr/local/bin/queue-worker`.
- Payment export uses Oracle `sqlplus`, `gzip`, and `wc`.
- `invoice-service` inherits `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; its optional `native-pdf` profile adds PDFBox.
- Reminder configuration uses HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, Spring scheduling, and `reminder.properties`.
- Shipment validation uses `shipment.xsd`; statement rendering consumes the `urn:example:statement` vocabulary.

#### Cross-cutting
- Resources span Perl, shell scripts, Maven, Spring XML, XML Schema, and XSLT.
- Settings come from CLI options, environment variables, and configuration files; reminder properties use placeholders.
- Access-log aggregation is capped at 50,000 lines, with requests above 1,500 ms considered slow.
- Deployment and payment export include health checks or retries; reminder scheduling uses a two-thread scheduler.
