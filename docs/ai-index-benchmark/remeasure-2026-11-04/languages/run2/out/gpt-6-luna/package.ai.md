### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:20Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports order and billing operations by analyzing access traffic, managing releases and workers, exporting payments, and processing customer-facing billing artifacts.

#### Purpose
- Provides operational tooling and billing-service artifacts for order deployment, payment processing, reminders, shipment validation, and statements.

#### Responsibilities
- **Traffic analysis:** Parse access logs, apply host aliases and exclusions, and report ranked request statistics.
- **Service operations:** Deploy tagged `orderd` releases with health checks; manage queue-worker lifecycle and health.
- **Payment export:** Extract settled payments to compressed CSV and produce an export control file.
- **Billing artifacts:** Configure the invoice-service build and migrations; schedule overdue reminders, validate shipment notices, and render statements as HTML.

#### Key units
- `LogDigest` / `log_digest.pl::main` — parse, aggregate, filter, and report access-log statistics in text or JSON.
- `deploy_host` / `wait_healthy` — install a release, restart `orderd`, and poll host health.
- `start_worker` / `stop_worker` / `is_running` / `heartbeat_age` — manage queue-worker processes and check heartbeat freshness.
- `run_export` / `write_control` — export settled payments and record row count and business day.
- `overdueReminderJob` / `reminderScheduler` — deliver overdue reminders and schedule weekday runs.
- `shipmentNotice` / `ShipmentNoticeType` — define required shipment-notice data.
- `statement-to-html.xsl` templates — render statement metadata, booking rows, and totals with threshold-based highlighting.
- `invoice-service` Maven project — configure the Java 17 build, dependencies, tests, and Flyway migrations.

#### Data flow
- Access logs pass through CLI filtering and alias handling into `LogDigest`, producing ranked text or canonical JSON.
- Release artifacts are installed host by host; deployment waits for `orderd` health before proceeding.
- Worker commands use PID and heartbeat files for lifecycle operations and status checks.
- The payment export selects a business day, queries Oracle `settled_payment`, writes CSV and control data, then compresses the CSV.
- Reminder properties wire the datasource, repository, SMTP gateway, and scheduled job; shipment XML is validated against `shipment.xsd`, while statement XML is transformed to HTML.

#### Dependencies
- Perl analytics uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, host lists, release archives, `rsync`, `ssh`, and `curl`; worker management uses the queue-worker executable and PID/heartbeat paths.
- Payment export depends on Oracle `sqlplus`, `settled_payment`, and system utilities including `gzip`, `wc`, and `mkdir`.
- Billing artifacts use the Maven parent and Java dependencies including Jackson, Flyway, PostgreSQL, JUnit, and Testcontainers; reminder wiring uses HikariCP and `org.example.reminder` components.
- XML artifacts use XML Schema types and the `urn:example:statement` namespace.

#### Cross-cutting
- External configuration covers log aliases, deployment and worker settings, Oracle SID, reminder database and SMTP properties, and invoice archive directory.
- Operational scripts signal failures with nonzero exit statuses; deployment stops on unhealthy hosts, and payment export retries up to three times.
- Domain thresholds include a 1,500 ms slow-request limit, 120-second worker heartbeat limit, 31,500-gram parcel maximum, and statement highlighting above 1000.
