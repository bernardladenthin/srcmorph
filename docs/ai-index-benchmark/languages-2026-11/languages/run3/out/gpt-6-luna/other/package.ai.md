### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:55:06Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and integration artifacts for deploying and monitoring services, exporting payment data, processing access logs, and configuring business-document workflows.

#### Purpose
- Supports application operations and billing workflows through deployment scripts, worker management, payment exports, service configuration, and document contracts.
- Includes access-log parsing and reporting for request, host, status, and slow-request statistics.

#### Responsibilities
- **Log analytics (`perl/`):** Parse access logs, apply host aliases and exclusions, aggregate request metrics, and report text or JSON summaries.
- **Service operations (`shell/`):** Deploy tagged releases, manage queue-worker lifecycle and health, and export settled payments to compressed daily CSV files.
- **Billing and reminders (`xml/`):** Configure invoice-service builds and scheduled overdue reminders, define shipment-notice validation, and transform account statements into HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse access-log entries, aggregate metrics, and rank hosts.
- `deploy_release.sh`, `deploy_host`, `wait_healthy`, `load_hosts` — deploy releases to configured hosts and verify service health.
- `healthcheck.sh`, `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage and assess queue-worker processes.
- `nightly_extract.ksh`, `run_export`, `write_control` — export settled payment fields and record row-count control data.
- `invoice-service` — Maven project configuring database migrations, archive settings, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder persistence, SMTP delivery, and weekday scheduling.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — define shipment-notice structure and field constraints.
- `statement-to-html.xsl`, `bookingsByDay` — render statement bookings and totals, with configurable currency and large-booking highlighting.

#### Data flow
- Access-log files and alias configuration feed `LogDigest`; parsed, non-excluded entries become aggregates reported as text or JSON.
- A release tag and environment select a build archive and host list; each host receives the release, restarts `orderd`, and undergoes an HTTP health check.
- Worker actions use PID and heartbeat files to control or assess the queue worker and report status.
- A business day and output directory drive an Oracle query for settled payments; results become a compressed CSV with a neighboring control file.
- Reminder configuration wires JDBC persistence and SMTP delivery into the scheduled overdue-reminder job; shipment XML is validated against its schema, while statement XML is transformed to HTML.

#### Dependencies
- Log processing uses Perl `POSIX`, `List::Util`, and `Carp`; its CLI uses `Getopt::Long`, `JSON::PP`, and `LOG_DIGEST_CONFIG`.
- Deployment uses `lib/common.sh`, environment-specific host files, SSH/rsync, systemd, and `curl`; worker management invokes `/usr/local/bin/queue-worker`.
- Payment export uses Oracle SQL*Plus, `ORACLE_SID`, and the `settled_payment` table.
- Invoice build depends on the billing Maven parent, Jackson, Flyway, PostgreSQL JDBC, JUnit, and Testcontainers; optional PDFBox is enabled by the `native-pdf` profile.
- Reminder configuration depends on Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.

#### Cross-cutting
- Runtime behavior is parameterized through CLI options, environment variables, properties, and configurable paths, including log exclusions, deployment targets, worker files, archive directory, reminder settings, and statement presentation options.
- Failures use explicit script- or tool-specific errors and exit statuses; deployment stops at the first failed host, and payment extraction retries up to three times.
- Operational scripts log or report status; log processing enforces a 50,000-line limit and a 1,500 ms slow-request threshold.
- Reminder scheduling uses a two-thread pool and a JDBC connection pool of up to eight connections.
