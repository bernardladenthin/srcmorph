### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:29:05Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and configuration for access-log reporting, production deployment and worker health, payment exports, and invoice, reminder, shipment, and statement workflows.

#### Purpose
- Supports production operations through log analysis, release and worker management, and settled-payment exports.
- Supplies build, scheduling, validation, and presentation configuration for billing-related workflows.

#### Responsibilities
- **Access-log analytics (`perl/`):** Parse and aggregate web requests by host and HTTP status, identify slow requests and busiest hosts, and report results with aliases, exclusions, and optional JSON.
- **Production operations (`shell/`):** Deploy tagged releases of `orderd`, manage `queue-worker` lifecycle and health checks, and export settled payments from Oracle with retries and row-count controls.
- **Billing workflow configuration (`xml/`):** Configure the `invoice-service` build and tests, wire and schedule overdue reminders, validate shipment notices, and transform statement XML into HTML.

#### Key units
- `LogDigest` — parses access-log records, accumulates statistics, and provides summaries and ranked counts.
- `log_digest.pl::main` — handles CLI options and input logs, then emits text or JSON reports.
- `deploy_release.sh` — deploys a tagged archive to staging or production hosts and checks `/health`.
- `healthcheck.sh` — manages `queue-worker` commands and checks process and heartbeat status.
- `nightly_extract.ksh` — exports Oracle `settled_payment` records to compressed CSV and writes a control file.
- `invoice-service-pom.xml` — configures the invoice-service build, migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder data access, SMTP delivery, and weekday execution of `sendDueReminders`.
- `shipment.xsd` — defines shipment-notice structure and constraints for addresses, carriers, and parcels.
- `statement-to-html.xsl` — renders statement XML as HTML with configurable currency and booking highlighting.

#### Data flow
- Access-log CLI options and alias configuration guide line-by-line processing; accepted records update `LogDigest` statistics, which are rendered as text or JSON.
- Deployment configuration and a release tag drive per-host transfer, extraction, `current` update, service restart, and HTTP health checks.
- Worker commands lead to PID and heartbeat checks, with status messages and command-specific exit codes.
- Payment export parameters drive an Oracle query; the resulting CSV is counted, recorded in a control file, compressed, and retried on failure.
- Maven properties and migration scripts configure invoice-service builds and tests; Spring properties configure reminder components and scheduling.
- Shipment notices are validated against `shipment.xsd`; statement XML is transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl:** `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, and `Carp`; alias configuration via `LOG_DIGEST_CONFIG` or `/etc/log-digest.conf`.
- **Shell:** `lib/common.sh`, environment host lists, SSH/rsync, `tar`, `systemctl`, `curl`, `/usr/local/bin/queue-worker`, Oracle `sqlplus`, and system utilities.
- **XML/build:** `billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, Maven Surefire, and optional PDFBox.
- **Reminder wiring:** Spring Beans/Context/Task, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- **Schema and transformation:** XML Schema built-in types and XSLT 1.0/XPath functions.

#### Cross-cutting
- Configuration is supplied through CLI options and environment variables, Maven properties, Spring XML properties, schema constraints, and XSLT parameters.
- Operational scripts log to standard error and use nonzero exit statuses for failures; deployment stops at the first failed host, and payment export retries are limited.
- `LogDigest` validates log levels, limits input to `MAX_LINES` (50,000), and counts requests strictly above `SLOW_REQUEST_MS` (1500 ms).
- Reminder scheduling uses a pool size of `2`, SMTP port `587`, a 14-day grace period, and a weekday 07:00 cron schedule.
