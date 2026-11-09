### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:19Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports production operations and billing workflows by analyzing logs, managing services, exporting payments, scheduling reminders, and validating and rendering business documents.

#### Purpose
- Provides operational tooling and configuration for service deployment, worker health, and access-log analysis.
- Supports billing and document workflows, including payment export, overdue reminders, shipment validation, and account-statement rendering.

#### Responsibilities
- **Log analysis:** Filter access logs, aggregate host and status statistics, identify busiest hosts, and report slow requests.
- **Service operations:** Deploy tagged `orderd` builds, check service health, and manage the `queue-worker` lifecycle.
- **Payment export:** Query settled Oracle payments for a business day and produce CSV, row-count control, and compressed outputs.
- **Billing and documents:** Configure the invoice-service build and migrations; schedule overdue reminders; constrain shipment notices; render account statements as HTML.

#### Key units
- `LogDigest`, `log_digest.pl` — aggregate access-log entries and emit text or JSON reports.
- `deploy_host`, `wait_healthy`, `load_hosts` — select hosts, deploy a release, and verify health.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — inspect and manage `queue-worker`.
- `run_export`, `write_control` — export settled payments and record the business day and row count.
- `invoice-service` Maven project — configure the Java 17 service build, dependencies, tests, migrations, and optional PDFBox profile.
- `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — connect reminder data and email delivery to weekday scheduling.
- `shipmentNotice`, `ShipmentNoticeType`, `shipment.xsd` — define and validate shipment-notice structure.
- `statement-to-html.xsl` — render statement headers, bookings, and totals as HTML.

#### Data flow
- Access logs are filtered using aliases and exclusions, aggregated by `LogDigest`, then emitted as text or canonical JSON.
- Release settings and a build tag select hosts and an archive; deployment installs the archive, restarts `orderd`, and polls the health endpoint.
- Worker management checks PID and heartbeat files before reporting status or starting, stopping, or restarting the worker.
- A selected business day drives an Oracle `settled_payment` query; results are written to CSV, a row-count control file, and a gzip export.
- Reminder properties configure database and SMTP access; the scheduler invokes `OverdueReminderJob` on weekday mornings.
- Shipment XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed to HTML.

#### Dependencies
- Perl tooling: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment and worker scripts: `lib/common.sh`, host-list files, `rsync`, `ssh`, `curl`, `nohup`, `kill`, and `stat`.
- Payment export: SQL*Plus, Oracle `settled_payment`, and `gzip`.
- Invoice build: `org.example.billing:billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional PDFBox.
- Reminder configuration: HikariCP, `org.example.reminder` classes, and Flyway migrations under `src/main/resources/db/migration`.

#### Cross-cutting
- Configuration comes from CLI options, environment variables, properties, Maven profiles, and XML bean configuration.
- Log aggregation normalizes hosts, limits input to 50,000 lines, flags requests over 1,500 ms as slow, and ranks results deterministically.
- Operational scripts report failures through messages and exit codes; deployment health checks and export retries are bounded.
- Shipment validation defaults recipient country to `DE` and caps parcel weight at 31,500 grams; statement rendering defaults to `EUR` and highlights amounts above 1,000.
