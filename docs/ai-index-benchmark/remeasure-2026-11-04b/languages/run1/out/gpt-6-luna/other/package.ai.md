### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:10Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and configuration for log analysis, service deployment and health, payment export, billing reminders, shipment validation, and account-statement rendering.

#### Purpose
- Groups utilities and configuration that support production operations and billing workflows.
- Covers access-log aggregation, release and worker management, settled-payment exports, scheduled reminders, and XML-based validation and presentation.

#### Responsibilities
- **Log analysis (`perl/`):** Parse access logs, aggregate host and status statistics, filter hosts, rank busiest hosts, and report slow requests.
- **Operational scripts (`shell/`):** Deploy tagged `orderd` builds, manage `queue-worker` lifecycle and health checks, and export settled Oracle payments with a row-count control file.
- **Billing and document configuration (`xml/`):** Configure the invoice-service build and migrations, schedule weekday overdue reminders, define shipment-notice constraints, and transform account statements into HTML.

#### Key units
- `LogDigest` — parses and aggregates access-log entries; `log_digest.pl` supplies command-line filtering and text or JSON output.
- `deploy_host`, `wait_healthy`, `load_hosts` — deploy a release to selected hosts and verify service health.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — check and manage `queue-worker`.
- `run_export`, `write_control` — export settled payments and record the business day and row count.
- `invoice-service` Maven project — configures the Java 17 service build, dependencies, tests, migrations, and optional PDFBox profile.
- `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — connect reminder data and email delivery to a weekday schedule.
- `shipmentNotice` / `ShipmentNoticeType` — define the required shipment notice structure and validation constraints.
- `statement-to-html.xsl` — renders statement headers, bookings, and totals as HTML.

#### Data flow
- Access-log files are filtered using configured aliases and exclusions, aggregated by `LogDigest`, then emitted as text or canonical JSON.
- Release settings and a build tag select hosts and an archive; deployment uploads and installs it, restarts `orderd`, then polls the host health endpoint.
- Worker actions check PID and heartbeat files, then report status or start, stop, or restart the worker.
- A selected business day drives an Oracle `settled_payment` query; results become a CSV, a row-count control file, and a gzipped export.
- Reminder properties configure the database and SMTP gateway; the scheduler invokes `OverdueReminderJob` on weekday mornings.
- Shipment XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed to HTML.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, host-list files, `rsync`, `ssh`, and `curl`; worker management uses `nohup`, `kill`, and `stat`.
- Payment export uses SQL*Plus, Oracle `settled_payment`, and `gzip`.
- Invoice build inherits from `org.example.billing:billing-parent:2.7.0`; dependencies include Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional PDFBox.
- Reminder configuration uses HikariCP and `org.example.reminder` classes; Flyway migrations reside at `src/main/resources/db/migration`.

#### Cross-cutting
- Runtime and build configuration comes from CLI options, environment variables, properties, Maven profiles, and XML bean configuration.
- Log aggregation normalizes hosts, limits input to 50,000 lines, counts requests over 1,500 ms as slow, and ranks deterministically.
- Scripts expose operational failures through messages and exit codes; deployment health checks and export retries are bounded.
- Shipment schema defaults recipient country to `DE` and caps parcel weight at 31,500 grams; statement rendering defaults to `EUR` and highlights amounts above 1,000.
