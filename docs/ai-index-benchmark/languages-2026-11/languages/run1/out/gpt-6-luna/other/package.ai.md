### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:49Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and application artifacts for analyzing access logs, deploying and monitoring services, exporting payment data, and supporting billing and fulfillment workflows.

#### Purpose
- Groups command-line log analytics and operations tools with XML configuration, build, validation, and presentation assets.
- Supports billing, payments, fulfillment, release management, queue-worker health, and access-log reporting.

#### Responsibilities
- **Access-log analysis (`perl/`)** — parses and aggregates requests by host and status, identifies slow requests, and reports results as text or JSON.
- **Operations (`shell/`)** — deploys tagged releases, manages and checks a queue worker, and exports settled payments to CSV with a control file.
- **Billing and fulfillment assets (`xml/`)** — configures the invoice-service build and overdue-reminder scheduling, validates shipment notices, and renders account statements as HTML.

#### Key units
- `LogDigest` — parses accepted access-log records and accumulates request and host statistics.
- `log_digest.pl::main` — reads options, aliases, exclusions, and log files, then emits text or canonical JSON reports.
- `deploy_host` / `wait_healthy` — deploy a release to a host and poll its health endpoint.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage queue-worker state and assess heartbeat freshness.
- `run_export` / `write_control` — export settled Oracle payments and record the business day and CSV row count.
- `invoice-service` — Maven JAR configuration for `org.example.billing:invoice-service:2.7.0`, including migrations and an optional `native-pdf` profile.
- `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob` — persistence, email delivery, and due-reminder processing components.
- `reminderScheduler` — schedules reminder processing at 07:00 on weekdays.
- `shipmentNotice` / `ShipmentNoticeType` — define the XML shipment-notice structure and parcel requirements.
- `statement-to-html.xsl` — transforms account-statement XML into HTML with formatted booking rows and totals.

#### Data flow
- **Log reporting:** CLI options and alias configuration → log lines → host normalization and exclusion filtering → `LogDigest` aggregation → text or JSON report.
- **Release rollout:** choose environment and tag → load host list → transfer and activate archive → restart `orderd` → verify health before continuing.
- **Worker checks:** inspect PID/process state and heartbeat age → report worker status and exit accordingly.
- **Payment export:** select business day and destination → query settled Oracle payments → write CSV and `.ctl` row count → gzip, retrying failed exports up to three times.
- **Reminders:** Spring loads properties and configures persistence and SMTP components → weekday scheduler invokes overdue-reminder processing with a 14-day grace period.
- **XML assets:** shipment documents are validated against `shipment.xsd`; statement XML is transformed by XSLT; Maven builds the invoice-service JAR and configures Flyway migrations.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `FindBin`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `LogDigest`.
- Deployment uses `lib/common.sh`, environment-specific host lists, and `rsync`, `ssh`, `curl`, and remote system utilities; worker tooling invokes `/usr/local/bin/queue-worker`.
- Payment extraction uses Oracle `sqlplus` against `settled_payment`, plus `gzip` and `wc`.
- Invoice build inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder configuration uses Spring, HikariCP, and `org.example.reminder` repository, gateway, and job components.
- Shipment schema uses the `urn:example:shipment:1` namespace; statement rendering consumes `urn:example:statement`.

#### Cross-cutting
- CLI tools expose options or environment-based configuration and automation-oriented exit statuses; deployment and export scripts emit timestamped diagnostics.
- Log processing normalizes hosts, applies aliases and exclusions, caps input at 50,000 lines, and counts requests slower than 1,500 ms.
- Shell scripts use configurable paths and defaults; deployment and export include failure handling, while the worker wrapper has no traps or retries.
- Reminder configuration uses property placeholders; its scheduler pool size is 2 and Hikari data-source maximum pool size is 8.
- Shipment constraints include carriers `DHL`, `UPS`, `GLS`, and `POST`, maximum weight 31,500 grams, and default country `DE`; statement output defaults to `EUR` and highlights amounts above 1,000.
