### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:25:02Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports production operations and billing workflows, from log analysis and release health checks to payment exports, overdue reminders, shipment validation, and account-statement rendering.

#### Purpose
- Provides operational tools and configuration for delivery, worker health, and payment processing.
- Supports invoice and reminder workflows plus shipment and account-statement document handling.

#### Responsibilities
- **Log reporting:** Parse access logs, apply host exclusions, and report request, status, host, and slow-request statistics.
- **Production operations:** Deploy tagged releases behind health checks, manage queue-worker lifecycle and health, and export settled payments.
- **Billing and documents:** Configure invoice builds and overdue-reminder scheduling, validate shipment notices, and render account statements as HTML.

#### Key units
- `LogDigest` and `log_digest.pl::main` — aggregate access-log statistics and emit text or canonical JSON reports.
- `deploy_release.sh` — install tagged releases on environment-selected hosts and gate rollout on health checks.
- `healthcheck.sh` — manage `queue-worker` actions and evaluate PID and heartbeat state.
- `nightly_extract.ksh` — export settled-payment rows to compressed CSV with a row-count control file.
- `invoice-service` — Maven JAR configuration for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — wire the overdue-reminder job to repository and mail gateway components and schedule weekday execution.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment-notice XML entry point and structure; `CarrierCode`, `WeightGrams`, `ParcelType`, and `AddressType` define validation types and constraints.
- `statement-to-html.xsl` — transform statement XML into HTML, indexing bookings by value date with `bookingsByDay`.

#### Data flow
- Access logs and alias/exclusion configuration feed `LogDigest`, whose aggregated results are formatted by the CLI.
- Deployment options select hosts and a release archive; remote installation and service restart are followed by per-host health checks.
- Worker action arguments and PID/heartbeat files determine lifecycle operations or health results.
- Export options select a business day and destination; an Oracle query produces payment rows, then control-file generation and gzip compression follow.
- Reminder properties configure JDBC and SMTP components for a scheduled job; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment host lists, SSH/rsync, and remote `systemctl`; worker checks depend on the queue-worker executable and PID/heartbeat paths.
- Payment export uses Oracle `sqlplus`, the `settled_payment` table, and `gzip`.
- Invoice build inherits `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder scheduling uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, JDBC, and SMTP.
- Shipment validation uses XML Schema and `urn:example:shipment:1`; statement rendering uses XSLT 1.0 and `urn:example:statement`.

#### Cross-cutting
- Log ingestion is capped at 50,000 lines; slow requests exceed 1,500 ms, hostnames are normalized, and CLI defaults are text output and top 10.
- Shell tools use Bash, POSIX `sh`, or `ksh93`; scripts define their own failure behavior, and payment export retries up to three times.
- Invoice compilation targets Java 17; reminder scheduling uses a two-thread pool, an eight-connection pool cap, and a 14-day grace period.
