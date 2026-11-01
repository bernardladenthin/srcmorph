### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:29:12Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports operational reporting, production releases and worker health, payment extraction, and the configuration and validation of billing workflows.

#### Purpose
- Provides tools for analyzing access logs and managing production services and payment exports.
- Configures invoice builds and reminder, shipment, and statement-processing workflows.

#### Responsibilities
- **Access-log analytics:** Parse and aggregate web requests by host and HTTP status, identify slow requests and busy hosts, and produce text or JSON reports.
- **Production operations:** Deploy tagged `orderd` releases, manage `queue-worker` lifecycle and health checks, and export settled payments from Oracle.
- **Billing workflows:** Configure invoice-service builds and tests, schedule overdue reminders, validate shipment notices, and render statement XML as HTML.

#### Key units
- `LogDigest` — parses access-log records, accumulates statistics, and provides summaries and ranked counts.
- `log_digest.pl::main` — processes CLI options and logs, then emits text or JSON reports.
- `deploy_release.sh` — deploys a tagged archive to staging or production and checks `/health`.
- `healthcheck.sh` — manages `queue-worker` commands and checks process and heartbeat status.
- `nightly_extract.ksh` — exports Oracle `settled_payment` records to compressed CSV and writes a control file.
- `invoice-service-pom.xml` — configures invoice-service builds, migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder data access and SMTP delivery, and schedules `sendDueReminders`.
- `shipment.xsd` — defines shipment-notice structure and constraints for addresses, carriers, and parcels.
- `statement-to-html.xsl` — transforms statement XML into HTML with configurable currency and booking highlighting.

#### Data flow
- CLI options and alias configuration guide access-log processing; accepted records update `LogDigest` statistics, which are rendered as text or JSON.
- A release tag drives per-host transfer, extraction, `current` update, service restart, and HTTP health checks.
- Worker commands lead to PID and heartbeat checks, with status messages and command-specific exit codes.
- Export parameters drive an Oracle query; results are counted, recorded in a control file, compressed, and retried on failure.
- Maven properties and migration scripts configure invoice-service builds and tests; Spring properties configure reminder components and scheduling.
- Shipment notices are checked against `shipment.xsd`; statement XML is transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl:** `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`; alias configuration from `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- **Shell operations:** `lib/common.sh`, environment host lists, SSH/rsync, `tar`, `systemctl`, `curl`, `/usr/local/bin/queue-worker`, Oracle `sqlplus`, and system utilities.
- **Invoice build:** `billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, Maven Surefire, and optional PDFBox.
- **Reminder wiring:** Spring Beans/Context/Task, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- **XML processing:** XML Schema built-in types and XSLT 1.0/XPath functions.

#### Cross-cutting
- Configuration comes from CLI options and environment variables, Maven and Spring properties, schema constraints, and XSLT parameters.
- Operational scripts log to standard error and return nonzero statuses on failure; deployment stops at the first failed host, and payment-export retries are limited.
- `LogDigest` validates log levels, limits input to `MAX_LINES` (50,000), and counts requests strictly above `SLOW_REQUEST_MS` (1500 ms).
- Reminder configuration uses a pool size of `2`, SMTP port `587`, a 14-day grace period, and a weekday 07:00 cron schedule.
