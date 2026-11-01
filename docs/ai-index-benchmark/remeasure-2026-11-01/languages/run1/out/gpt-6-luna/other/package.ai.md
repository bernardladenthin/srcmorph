### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:16:51Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> This package supports web-log analysis, service operations and payment exports, invoice and reminder configuration, shipment validation, and account-statement rendering.

#### Purpose
- Combines operational tools for analyzing access logs, deploying and monitoring services, and exporting settled payments.
- Provides XML build, scheduling, validation, and presentation resources for billing and account workflows.

#### Responsibilities
- **Access-log reporting:** Parses and aggregates requests, applies host aliases and exclusions, and emits text or JSON summaries.
- **Service operations:** Deploys tagged releases with health checks and dry-run support; manages queue-worker lifecycle and heartbeat checks.
- **Payment exports:** Queries Oracle for settled payments, generates daily compressed CSV output, and records row counts.
- **Billing and reminders:** Configures the invoice-service Maven build and schedules weekday overdue-reminder jobs.
- **Shipment and statement XML:** Validates shipment notices against an XML Schema and transforms account statements into HTML.

#### Key units
- `LogDigest` — parses access-log entries and accumulates host, status, and latency statistics.
- `log_digest.pl::main` — processes CLI options and input files, then prints the digest as text or canonical JSON.
- `deploy_release.sh` — deploys a tagged archive to selected hosts, restarts `orderd`, and polls service health.
- `healthcheck.sh` — starts, stops, restarts, or checks the status and heartbeat freshness of `queue-worker`.
- `nightly_extract.ksh` — exports settled-payment data from Oracle and writes a compressed CSV with a row-count control file.
- `invoice-service` — Maven JAR module configured for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — wires reminder dependencies and schedules `OverdueReminderJob.sendDueReminders`.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment document structure and parcel requirements.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — constrain carrier values, parcel weights and structure, and recipient address fields.
- `statement-to-html.xsl` — renders namespaced account statements as HTML with formatted totals and threshold-based booking highlights.

#### Data flow
- Access-log files and alias/exclusion settings feed `LogDigest`; its aggregate statistics are formatted as text or JSON.
- Deployment options select hosts and a build archive, which are copied and activated before service restart and HTTP health polling.
- Worker commands inspect PID and heartbeat files and invoke the configured worker executable for lifecycle actions.
- Business-day and directory settings drive the Oracle `settled_payment` query; successful output is counted, written to a control file, and gzipped.
- Reminder properties configure database and SMTP beans used by a weekday scheduled job; shipment documents are schema-validated, while statement XML is transformed into HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, host lists, `rsync`, SSH, `curl`, and remote `systemctl`; worker checks use `/usr/local/bin/queue-worker`.
- Payment export uses Oracle `sqlplus`, `gzip`, and `wc`.
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; its optional `native-pdf` profile adds PDFBox.
- Reminder configuration depends on HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, Spring scheduling, and `reminder.properties`.
- Shipment validation uses `shipment.xsd`; statement rendering consumes the `urn:example:statement` vocabulary.

#### Cross-cutting
- Scripts and configuration span Perl, Bash, POSIX shell, Ksh93, Maven, Spring XML, XML Schema, and XSLT.
- Operational settings are supplied through CLI options, environment variables, and configuration files; reminder settings use property placeholders.
- Access-log aggregation is capped at 50,000 lines, and requests are considered slow above 1,500 ms.
- Deployment and payment export include health checks or retries; reminder scheduling uses a two-thread scheduler.
