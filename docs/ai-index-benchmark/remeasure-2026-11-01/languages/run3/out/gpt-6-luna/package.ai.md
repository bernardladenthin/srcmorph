### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:57:06Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational support for service deployment and monitoring, payment extraction, billing reminders, log reporting, shipment validation, and account-statement presentation.

#### Purpose
- Groups tools and configuration for business operations across log analysis, release and worker management, payment exports, billing reminders, and XML processing.
- Includes Perl and shell utilities alongside Maven, Spring, schema, and XSLT assets.

#### Responsibilities
- **Log reporting:** Parses access logs, aggregates host and status counts, tracks slow requests, applies aliases and exclusions, and emits text or JSON.
- **Service operations:** Deploys tagged `orderd` releases with health checks; starts, stops, restarts, and checks `queue-worker`.
- **Payment export:** Queries settled Oracle payments and produces a CSV, control file, and compressed extract with retry handling.
- **Billing reminders:** Configures the `invoice-service` build and migrations, and wires scheduled overdue reminders to database and SMTP components.
- **Shipment and statement XML:** Defines shipment-notice validation constraints and transforms account statements into formatted HTML.

#### Key units
- `LogDigest` — parses access-log entries and accumulates request, host, status, and slow-request statistics.
- `log_digest.pl::main` — applies CLI options, aliases, and exclusions to logs, then emits text or canonical JSON.
- `deploy_release.sh` — deploys a tagged archive to configured hosts and polls `/health`.
- `healthcheck.sh` — manages `queue-worker` using PID and heartbeat files.
- `nightly_extract.ksh` — exports `settled_payment` rows to CSV, creates a control file, and gzips the extract.
- `invoice-service` Maven POM — configures Java 17 dependencies, Flyway, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires the reminder repository, mail gateway, overdue-reminder job, and weekday scheduler.
- `shipmentNotice` / `ShipmentNoticeType` — define shipment-notice XML structure, including recipients and parcels.
- `CarrierCode` / `WeightGrams` — constrain carrier values and parcel weights to a maximum of 31,500 grams.
- `statement-to-html.xsl` — renders statement account details, bookings, and totals as HTML.

#### Data flow
- Access-log lines pass through `log_digest.pl` alias and exclusion handling into `LogDigest` aggregation, then to text or JSON output.
- `build/$TAG.tar.gz` is transferred to hosts, extracted under `/opt/releases/$TAG`, linked as `current`, and followed by an `orderd` restart and health polling.
- Worker health combines PID/process checks with heartbeat-file age.
- Business-day and export-directory inputs drive an Oracle query of `settled_payment`; results become a CSV, row count, control file, and gzipped extract.
- Reminder properties configure the data source and SMTP gateway; the repository and gateway support `OverdueReminderJob`, invoked by the scheduler.
- Shipment XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed into HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment and worker scripts use `lib/common.sh`, host lists, `rsync`, `ssh`, `curl`, `sudo`, `systemctl`, `tar`, and `/usr/local/bin/queue-worker`.
- Payment export uses `sqlplus` with `CONNECT / AS SYSDBA`, Oracle table `settled_payment`, and `gzip`, `wc`, and `sleep`.
- `invoice-service` inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL; PDFBox is enabled by the `native-pdf` profile.
- Reminder wiring uses Spring, HikariCP, and `org.example.reminder` classes including `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`.
- Shipment validation uses XML Schema built-in types; statement rendering consumes elements in `urn:example:statement`.

#### Cross-cutting
- Log analysis defaults to `WARN`, supports `DEBUG`, `INFO`, `WARN`, and `ERROR`, caps input at 50,000 lines, and treats requests over 1,500 ms as slow; host normalization lowercases names and removes one trailing dot.
- Deployment and export operations use timestamped stderr logging; deployment stops at the first host failure, worker checks distinguish stopped from stale-heartbeat states, and payment export retries failed queries up to three times.
- Reminder configuration is property-driven, with an 8-connection Hikari pool, a 2-thread scheduler, a 14-day grace period, and weekday execution at 07:00.
- Statement rendering defaults to `EUR`, highlights amounts above `1000`, formats values as `#,##0.00`, and emits indented UTF-8 HTML.
