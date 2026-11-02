### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:24:53Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports operational delivery and billing workflows through access-log reporting, release and worker operations, payment exports, invoice/reminder configuration, shipment validation, and statement rendering.

#### Purpose
- Provides utilities and configuration for production operations and billing-related processing.
- Covers log analysis, application rollout and worker health, payment exports, invoice/reminder setup, shipment data contracts, and account-statement presentation.

#### Responsibilities
- **Log reporting (`perl/`):** Parse and aggregate access logs, filter excluded hosts using aliases, and report request, status, host, and slow-request statistics as text or JSON.
- **Production operations (`shell/`):** Deploy tagged releases with health checks, manage queue-worker lifecycle and heartbeat health, and export settled payments to compressed CSV with a control file.
- **Billing and XML workflows (`xml/`):** Configure invoice-service builds and overdue-reminder scheduling, define shipment-notice validation rules, and transform account statements into HTML.

#### Key units
- `LogDigest` — parses access-log entries and maintains request, host, status, and slow-request statistics.
- `log_digest.pl::main` — reads configuration and logs, applies host exclusions, and emits a report.
- `deploy_release.sh` — installs a tagged archive on environment-selected hosts and gates rollout on health checks.
- `healthcheck.sh` — manages `queue-worker` actions and evaluates PID and heartbeat state.
- `nightly_extract.ksh` — exports settled-payment data and produces a compressed CSV with a row-count control file.
- `invoice-service` — Maven JAR configuration for invoice creation, numbering, and archiving.
- `scheduler-context.xml` — wires the overdue-reminder job to repository and mail gateway components and schedules weekday execution.
- `shipmentNotice` / `ShipmentNoticeType` — XML Schema entry point and structure for shipment notices.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — shipment validation types and constraints.
- `statement-to-html.xsl` — transforms statement XML into HTML; `bookingsByDay` indexes bookings by value date.

#### Data flow
- Access-log files and alias/exclusion configuration feed `LogDigest`; the CLI formats its aggregate summary as text or canonical JSON.
- Deployment options select a host list and release archive; remote installation and service restart are followed by per-host health checks.
- Worker action arguments and PID/heartbeat files drive lifecycle operations or health-status results.
- Export options select a business day and destination; an Oracle query writes payment rows, followed by control-file generation and gzip compression.
- Reminder configuration loads properties and wires JDBC and SMTP components into a scheduled job; shipment XML is validated against `shipment.xsd`, while statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment host lists, SSH/rsync, and remote `systemctl`; worker checks depend on the queue-worker executable and PID/heartbeat paths.
- Payment export uses Oracle `sqlplus`, the `settled_payment` table, and `gzip`.
- Invoice build configuration inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder scheduling depends on Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, JDBC, and SMTP.
- Shipment validation uses XML Schema and `urn:example:shipment:1`; statement rendering uses XSLT 1.0 and `urn:example:statement`.

#### Cross-cutting
- Perl log ingestion is capped at 50,000 lines; slow requests exceed 1,500 ms, hostnames are lowercased with one trailing dot removed, and CLI defaults to text output and top 10.
- Shell scripts are standalone operational tools using Bash, POSIX `sh`, and `ksh93`; failures produce script-specific exit behavior, and payment export retries up to three times.
- Invoice compilation targets Java 17; reminder scheduling uses a two-thread pool, an eight-connection pool cap, and a 14-day grace period.
