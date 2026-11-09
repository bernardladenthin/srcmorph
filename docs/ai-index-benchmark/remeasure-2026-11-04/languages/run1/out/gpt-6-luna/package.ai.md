### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:15Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational and business-document tooling for payment and billing workflows, service deployment, worker health, access-log reporting, and shipment and statement processing.

#### Purpose
- Groups tools and configuration for release operations, worker management, settled-payment exports, and access-log analysis.
- Supports invoice and overdue-reminder services plus shipment validation and account-statement rendering.

#### Responsibilities
- **Operational reporting:** Parse, filter, normalize, and aggregate web access logs; produce ranked text or JSON reports, including slow-request counts.
- **Service and payment operations:** Deploy tagged releases to selected hosts and verify health; inspect and manage `queue-worker`; export settled payments with control metadata.
- **Billing and reminders:** Configure the `invoice-service` build and migrations; wire weekday overdue-reminder processing to database and SMTP components.
- **Business documents:** Validate shipment notices against XML constraints and transform account statements into formatted HTML.

#### Key units
- `LogDigest`, `log_digest.pl` — aggregate access-log records and produce filtered, ranked reports.
- `deploy_host`, `wait_healthy`, `load_hosts` — deploy releases to selected hosts and check service health.
- `start_worker`, `stop_worker`, `is_running`, `heartbeat_age` — manage and inspect queue-worker process and heartbeat state.
- `run_export`, `write_control` — export settled-payment rows and record business day and row count.
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, with Java 17, Flyway migrations, and optional PDFBox support.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — configure and schedule weekday overdue reminders.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — define shipment-notice structure and constraints.
- `/st:statement`, `st:booking`, `statementHeader`, `statementTotal` — render statement details, booking rows, totals, and large-amount styling.

#### Data flow
- Access logs are host-filtered and alias-normalized before `LogDigest` aggregates them for text or JSON output.
- Deployment options select hosts and a build archive; each host receives the release, restarts `orderd`, and is checked through `/health`.
- Worker commands use PID and heartbeat files to determine process state and report status.
- Payment export queries Oracle `settled_payment` for a business day, writes CSV and row-count metadata, then compresses the CSV.
- Spring configuration wires reminder properties, database and SMTP dependencies, and schedules `OverdueReminderJob.sendDueReminders` on weekdays at 07:00.
- Shipment XML is validated in `urn:example:shipment:1`; statement XML in `urn:example:statement` is transformed to formatted HTML.

#### Dependencies
- Log reporting uses Perl modules including `POSIX`, `List::Util`, `Getopt::Long`, and `JSON::PP`; aliases come from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- Deployment uses `lib/common.sh`, host lists, `rsync`, `ssh`, `curl`, and remote `sudo systemctl`; worker tools target `/usr/local/bin/queue-worker`.
- Payment export uses Oracle `sqlplus`, `settled_payment`, and `gzip`; the default SID is `PAYPRD`.
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL.
- Reminder wiring references `reminder.properties`, HikariCP, and `org.example.reminder` repository, gateway, and job classes; XML processing uses XML Schema built-in types and XSLT functions.

#### Cross-cutting
- Configuration comes from command-line options, environment variables, Maven properties, Spring placeholders, and XSLT parameters.
- Log analysis caps input at 50,000 lines, validates levels, ignores invalid lines, and marks requests slower than 1,500 ms.
- Deployment stops on failed health checks; payment export retries up to three times; scripts report failures with exit codes.
- Reminder scheduling uses a two-thread pool and closes its data source on shutdown; statement rendering defaults to `EUR`, a 1000 highlight threshold, and `#,##0.00` formatting.
