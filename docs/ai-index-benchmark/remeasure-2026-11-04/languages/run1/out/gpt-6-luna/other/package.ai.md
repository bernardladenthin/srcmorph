### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:07Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports billing and payment operations, release and worker administration, access-log analysis, and XML-based shipment and account-statement processing.

#### Purpose
- Groups operational tools for deployments, worker health, payment exports, and web-log reporting with billing, reminder, shipment, and statement configuration.
- Defines build, scheduling, validation, and presentation behavior for related services and business documents.

#### Responsibilities
- **Access-log analysis (`perl/`):** Parses and aggregates requests by normalized host and HTTP status, tracks slow requests, applies aliases and exclusions, and emits text or JSON reports.
- **Service operations (`shell/`):** Rolls tagged releases across staging or production hosts, manages and checks `queue-worker`, and exports settled payments to compressed CSV with a control file.
- **Billing and reminders (`xml/`):** Configures the invoice-service Maven build and Flyway migrations, and wires weekday overdue-reminder processing through database and SMTP components.
- **Business-document processing (`xml/`):** Defines shipment-notice validation rules and transforms account statements into formatted HTML.

#### Key units
- `LogDigest` — parses access-log lines and summarizes host, status, and slow-request counts.
- `log_digest.pl` — loads aliases, filters logs, and produces ranked text or canonical JSON reports.
- `deploy_host`, `wait_healthy`, `load_hosts` — deploy and verify a release on selected hosts.
- `start_worker`, `stop_worker`, `is_running`, `heartbeat_age` — manage and inspect queue-worker process and heartbeat state.
- `run_export`, `write_control` — export settled-payment rows and record business day and row count.
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, with Java 17 compilation, Flyway migrations, and optional PDFBox support.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — configure and schedule weekday overdue reminders with a 14-day grace period.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — define shipment-notice structure and constraints.
- `/st:statement`, `st:booking`, `statementHeader`, `statementTotal` — render statement details, booking rows, totals, and large-amount styling.

#### Data flow
- Access-log lines are host-filtered and alias-normalized by the CLI, then parsed and aggregated by `LogDigest` for ranked text or JSON output.
- Deployment options select a host list and build archive; each host receives the release, restarts `orderd`, and is checked through its `/health` endpoint.
- Worker commands use PID and heartbeat files to determine process state and report command-specific status.
- The payment export queries Oracle `settled_payment` for a business day, writes CSV and row-count metadata, then compresses the CSV.
- Spring configuration loads reminder properties, wires database and SMTP dependencies, and schedules `OverdueReminderJob.sendDueReminders` on weekdays at 07:00.
- Shipment XML is validated against `urn:example:shipment:1`; statement XML in `urn:example:statement` is transformed to HTML with formatted booking amounts and totals.

#### Dependencies
- Perl reporting uses `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`, and `LogDigest`; aliases come from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- Deployment uses `lib/common.sh`, host-list files, `rsync`, `ssh`, `curl`, and remote `sudo systemctl`; worker tools target `/usr/local/bin/queue-worker`.
- Payment export uses Oracle `sqlplus` (`/ AS SYSDBA`), `settled_payment`, and `gzip`; default SID is `PAYPRD`.
- Invoice build inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL.
- Reminder wiring references `reminder.properties`, HikariCP, and `org.example.reminder` repository, gateway, and job classes.
- Shipment validation uses XML Schema built-in types; statement rendering uses XSLT functions and the statement namespace.

#### Cross-cutting
- Configuration is supplied through command-line options, environment variables, Maven properties, Spring placeholders, and XSLT parameters.
- Log analysis enforces a 50,000-line maximum, validates levels, ignores invalid lines, and marks requests slower than 1,500 ms.
- Deployment halts on failed health checks; payment export retries up to three times; scripts signal failures with exit codes.
- Reminder scheduling uses a two-thread pool and closes its data source on shutdown; statement rendering defaults to `EUR`, a 1000 highlight threshold, and `#,##0.00` formatting.
