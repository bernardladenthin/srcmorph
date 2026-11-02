### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:56:58Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and configuration for log analysis, release and worker operations, payment exports, billing reminders, shipment validation, and account-statement rendering.

#### Purpose
- Supports business operations spanning web-server log reporting, service deployment and health management, settled-payment exports, invoice-service setup, reminders, and statement presentation.
- Groups Perl utilities, shell scripts, and XML build, wiring, schema, and transformation assets.

#### Responsibilities
- **Log analysis (`perl/`):** Parses access logs, aggregates host and status counts, tracks slow requests, applies host exclusions, and reports results as text or JSON.
- **Deployment and worker operations (`shell/`):** Rolls out tagged `orderd` releases with health checks; manages `queue-worker` lifecycle and heartbeat-based health.
- **Payment export (`shell/`):** Queries settled Oracle payments, generates a CSV and control file, and compresses the extract with retry handling.
- **Billing and reminders (`xml/`):** Configures the `invoice-service` build and Flyway migrations; wires scheduled overdue reminders to database and SMTP components.
- **Shipment and statement XML (`xml/`):** Validates shipment notices and transforms namespaced account statements into formatted HTML.

#### Key units
- `LogDigest` — parses access-log entries and accumulates request, host, status, and slow-request statistics.
- `log_digest.pl::main` — processes CLI options and log files, applies aliases and exclusions, and emits text or canonical JSON.
- `deploy_release.sh` — deploys a tagged archive to configured hosts and polls the `/health` endpoint.
- `healthcheck.sh` — starts, stops, restarts, and checks `queue-worker` using PID and heartbeat files.
- `nightly_extract.ksh` — exports `settled_payment` rows to CSV, creates a control file, and gzips the extract.
- `invoice-service` Maven POM — configures Java 17 build dependencies, Flyway, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder repository, mail gateway, overdue-reminder job, and weekday scheduler.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment notice schema, including recipient and parcel structure.
- `CarrierCode` / `WeightGrams` — constrain supported carrier values and parcel weight to a maximum of 31,500 grams.
- `statement-to-html.xsl` — renders statement account details, bookings, and totals as HTML.

#### Data flow
- Access-log lines are read by `log_digest.pl`, checked against host aliases and exclusions, passed to `LogDigest` for parsing and aggregation, then summarized for text or JSON output.
- Release archives flow from `build/$TAG.tar.gz` through `rsync` to hosts, where they are extracted under `/opt/releases/$TAG`, linked as `current`, and followed by an `orderd` restart and health polling.
- Worker status combines PID/process checks with heartbeat-file age to determine whether `queue-worker` is running and healthy.
- Business-day and export-directory inputs drive an Oracle query of `settled_payment`; results become a CSV, row count, business-day control file, and gzipped extract.
- Reminder properties configure the data source and SMTP gateway; the repository and gateway feed `OverdueReminderJob`, invoked by the scheduler.
- Shipment XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed into HTML with formatted booking amounts and totals.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, host lists, and `rsync`, `ssh`, `curl`, `sudo`, `systemctl`, and `tar`; worker management uses `/usr/local/bin/queue-worker`, `nohup`, `kill`, and `stat`.
- Payment export uses `sqlplus` with `CONNECT / AS SYSDBA`, Oracle table `settled_payment`, and `gzip`, `wc`, and `sleep`.
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL; PDFBox is enabled by the `native-pdf` profile.
- Reminder wiring uses Spring, HikariCP, and `org.example.reminder` classes including `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`.
- Shipment validation uses XML Schema built-in types; statement rendering consumes elements in `urn:example:statement`.

#### Cross-cutting
- Log analysis defaults to `WARN`, accepts `DEBUG`, `INFO`, `WARN`, and `ERROR`, caps input at 50,000 lines, and treats requests over 1,500 ms as slow; host normalization lowercases names and removes one trailing dot.
- Deployment and export operations use timestamped stderr logging; deployment stops at the first host failure, worker health distinguishes stopped from stale-heartbeat states, and payment export retries failed queries up to three times.
- Reminder configuration is property-driven, with an 8-connection Hikari pool, a 2-thread scheduler, a 14-day grace period, and weekday execution at 07:00.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, formats values as `#,##0.00`, and emits indented UTF-8 HTML.
