### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:50Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports payment and billing operations through access-log reporting, release and worker operations, payment exports, reminder scheduling, shipment validation, and statement rendering.

#### Purpose
- Provides utilities and configuration for operational monitoring, deployment, and settled-payment delivery.
- Supports invoice, overdue-reminder, shipment-notice, and account-statement workflows.

#### Responsibilities
- **Access-log metrics (`perl/`):** parses and aggregates requests by host and HTTP status; reports slow requests and ranks hosts, with aliases, exclusions, and text or JSON output.
- **Production operations (`shell/`):** deploys tagged releases with per-host health checks, manages `queue-worker`, and exports settled payments to CSV with row-count controls.
- **Billing build and reminders (`xml/`):** configures the invoice-service Maven build and Spring scheduling for weekday overdue-reminder delivery.
- **Logistics and statements (`xml/`):** defines shipment-notice XML constraints and transforms account-statement XML into formatted HTML.

#### Key units
- `LogDigest` — parses access-log lines, aggregates request metrics, summarizes totals, and ranks hosts.
- `log_digest.pl::main` — reads logs, applies aliases and exclusions, and emits text or canonical JSON reports.
- `deploy_release.sh` / `deploy_host(host)` — transfers and activates a tagged release, restarts `orderd`, and verifies host health.
- `healthcheck.sh` — controls and reports `queue-worker` status, including heartbeat freshness.
- `nightly_extract.ksh` / `run_export(outfile)` — exports Oracle `settled_payment` rows and produces compressed CSV and a row-count control file.
- `invoice-service-pom.xml` — configures invoice-service dependencies, migrations, tests, and the optional `native-pdf` profile.
- `scheduler-context.xml` — wires reminder database access, SMTP delivery, and weekday scheduling for `OverdueReminderJob`.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment XML structure and required fields.
- `statement-to-html.xsl` — renders statement XML as HTML with booking rows, formatted totals, and large-amount highlighting.

#### Data flow
- Access logs pass through alias resolution and exclusion filtering, then `LogDigest` parses and aggregates accepted requests for ranked text or JSON reporting.
- Release arguments select an archive and host list; deployment proceeds host by host through transfer, activation, `orderd` restart, and health polling.
- Worker commands and PID/heartbeat files drive process checks and status output.
- A business day selects Oracle `settled_payment` rows for CSV export; the script records the row count and gzips the CSV, retrying failed exports.
- Reminder properties feed Spring-wired database and mail components, which schedule `OverdueReminderJob.sendDueReminders()`.
- Shipment notices are validated against `shipment.xsd`; statement XML is transformed into a formatted HTML page.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, and `Carp`.
- Deployment uses `lib/common.sh`, staging or production host lists, SSH, `rsync`, and remote service-management commands; worker control uses `/usr/local/bin/queue-worker` and PID/heartbeat files.
- Payment extraction uses Oracle `sqlplus`, the `settled_payment` table, and `gzip`.
- Invoice build configuration depends on `billing-parent:2.7.0`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; Apache PDFBox is profile-only.
- Reminder wiring uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`; shipment and statement artifacts use their respective XML namespaces, XSD, and XSLT 1.0.

#### Cross-cutting
- Scripts and reporting tools expose command-line configuration and signal failures through nonzero exits; deployment halts on health-check failure, and exports use bounded retries.
- Access-log aggregation ignores malformed lines, caps input at 50,000 lines, and defines slow requests as exceeding 1,500 ms.
- Operational defaults include a 30-second deployment health timeout, a 120-second worker heartbeat limit, and a 60-second export retry delay.
- Reminder scheduling uses an eight-connection HikariCP pool, SMTP port 587, a 14-day grace period, and two scheduler threads.
- Shipment constraints include required notice and parcel identifiers, allowed carriers `DHL`, `UPS`, `GLS`, and `POST`, positive parcel weights up to 31,500 grams, and default country `DE`; statement rendering defaults to `EUR` and highlights amounts above `1000`.
