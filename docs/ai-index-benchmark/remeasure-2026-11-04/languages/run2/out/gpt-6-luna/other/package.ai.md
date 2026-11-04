### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:12Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports order and billing operations through access-log reporting, release and worker management, payment exports, reminder scheduling, shipment validation, and statement rendering.

#### Purpose
- Groups operational scripts, log-analysis tools, and XML build, scheduling, data-contract, and presentation artifacts for order and billing services.
- Covers both runtime operations and business-data processing, from deployment and payment extraction to reminders and customer statements.

#### Responsibilities
- **Access-log analytics (`perl/`):** Parse and aggregate requests by host and status; filter hosts and report ranked statistics as text or JSON.
- **Service operations (`shell/`):** Deploy tagged `orderd` releases, manage queue-worker lifecycle and health, and export settled payments to compressed CSV with a control file.
- **Billing and customer artifacts (`xml/`):** Configure invoice-service builds and migrations, schedule overdue reminders, define shipment XML validation, and transform statements into HTML.

#### Key units
- `LogDigest` — parses access-log entries and summarizes normalized host, status, and slow-request counts.
- `log_digest.pl::main` — applies CLI options, aliases, and exclusions before emitting ranked text or JSON reports.
- `deploy_host` / `wait_healthy` — install a release on a host, restart `orderd`, and poll its health endpoint.
- `start_worker` / `stop_worker` / `is_running` / `heartbeat_age` — manage queue-worker processes and assess heartbeat freshness.
- `run_export` / `write_control` — query settled payments and record export row count and business day.
- `overdueReminderJob` / `reminderScheduler` — wire reminder delivery and schedule weekday runs.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment notice contract and its required data.
- `statement-to-html.xsl` templates — render statement metadata, booking rows, and totals, including threshold-based highlighting.
- `invoice-service` Maven project — configures the Java 17 service build, dependencies, tests, and Flyway migrations.

#### Data flow
- Access-log files pass through CLI filtering and host alias handling, then `LogDigest` parsing and aggregation, producing ranked text or canonical JSON.
- Release artifacts are copied and installed host by host; each `orderd` restart is followed by health polling before deployment proceeds.
- Worker commands use PID and heartbeat files to perform lifecycle actions and report status.
- The payment export selects a business day, queries Oracle `settled_payment`, writes CSV and a control file, then gzips the CSV; failed exports are retried.
- Reminder configuration loads properties and wires the datasource, repository, SMTP gateway, and scheduled job.
- Shipment XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed to HTML.

#### Dependencies
- Perl analytics uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`; the CLI reads access logs and optional alias configuration.
- Deployment depends on `lib/common.sh`, environment host lists, release archives, and `rsync`, `ssh`, and `curl`; worker management uses the queue-worker executable and PID/heartbeat paths.
- Payment exports use Oracle `sqlplus`, `settled_payment`, and `gzip`, `wc`, and `mkdir`.
- XML artifacts depend on the billing Maven parent and Java libraries including Jackson, Flyway, PostgreSQL, JUnit, and Testcontainers; reminder wiring uses HikariCP and `org.example.reminder` components.
- Shipment schema uses XML Schema types; statement transformation consumes the statement namespace.

#### Cross-cutting
- Configuration is externalized where applicable: log aliases, deployment and worker settings, Oracle SID, reminder database and SMTP properties, and invoice archive directory.
- Operational scripts report failures with nonzero exit statuses; deployment stops on an unhealthy host and payment export retries up to three times.
- Domain constraints include a 1,500 ms slow-request threshold, 120-second worker heartbeat limit, 31,500-gram shipment parcel maximum, and statement highlighting above 1000.
