### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:55:16Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports service operations, payment exports, access-log analysis, and billing and business-document workflows.

#### Purpose
- Provides operational tooling for deploying releases and managing workers, plus settled-payment extraction and access-log reporting.
- Configures billing reminders and invoice builds, and defines shipment validation and account-statement rendering.

#### Responsibilities
- **Access-log analytics:** Parse and filter access logs, apply host aliases, aggregate request metrics, and produce text or JSON reports.
- **Service operations:** Deploy tagged releases to configured hosts, verify service health, manage queue-worker processes, and export settled payments to compressed daily CSV files with control data.
- **Billing and documents:** Configure invoice-service builds and scheduled overdue reminders; validate shipment notices and transform account statements into HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse access-log entries, aggregate metrics, and rank hosts.
- `deploy_release.sh`, `deploy_host`, `wait_healthy`, `load_hosts` — deploy releases and check host health.
- `healthcheck.sh`, `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — control and assess queue-worker processes.
- `nightly_extract.ksh`, `run_export`, `write_control` — export settled-payment fields and write row-count control data.
- `invoice-service` — Maven project configuring migrations, archives, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder persistence and SMTP delivery to weekday scheduling.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — define shipment-notice structure and field constraints.
- `statement-to-html.xsl`, `bookingsByDay` — render statement bookings and totals with configurable currency and large-booking highlighting.

#### Data flow
- Access logs and alias configuration feed `LogDigest`; parsed, non-excluded entries become aggregates reported as text or JSON.
- A release tag and environment select a build archive and host list; each host receives the release, restarts `orderd`, and undergoes an HTTP health check.
- Worker management uses PID and heartbeat files to control or assess the queue worker.
- A business day and output directory drive an Oracle query against `settled_payment`; results are written to compressed CSV with a neighboring control file.
- Reminder configuration connects JDBC persistence and SMTP delivery to the scheduled overdue-reminder job; shipment XML is schema-validated and statement XML is transformed to HTML.

#### Dependencies
- Log processing uses Perl `POSIX`, `List::Util`, `Carp`, and `LOG_DIGEST_CONFIG`; its CLI uses `Getopt::Long` and `JSON::PP`.
- Deployment uses `lib/common.sh`, environment-specific host files, SSH/rsync, systemd, and `curl`; worker management invokes `/usr/local/bin/queue-worker`.
- Payment export uses Oracle SQL*Plus, `ORACLE_SID`, and the `settled_payment` table.
- `invoice-service` depends on the billing Maven parent, Jackson, Flyway, PostgreSQL JDBC, JUnit, and Testcontainers; the `native-pdf` profile optionally enables PDFBox.
- Reminder configuration uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.

#### Cross-cutting
- Runtime behavior is configurable through CLI options, environment variables, properties, and paths, including log exclusions, deployment targets, worker files, archive location, reminder settings, and statement presentation.
- Failures use tool- or script-specific errors and exit statuses; deployment stops at the first failed host, while payment extraction retries up to three times.
- Operational scripts report status; log processing enforces a 50,000-line limit and a 1,500 ms slow-request threshold.
- Reminder scheduling uses a two-thread pool and a JDBC connection pool of up to eight connections.
