### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:57Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides access-log reporting and operational automation alongside billing reminders, payment exports, shipment validation, and account-statement rendering.

#### Purpose
- Groups tools and configuration for service operations, log analysis, payment export, and billing and fulfillment workflows.
- Supports invoice-service builds, overdue reminders, shipment-notice validation, and statement presentation.

#### Responsibilities
- **Log analytics:** Parse access logs, normalize and filter hosts, aggregate request statistics, and produce text or JSON reports.
- **Operations and exports:** Deploy tagged releases, monitor queue-worker state, and export settled payments to CSV with a control file.
- **Billing and reminders:** Configure the invoice-service build and schedule overdue-reminder processing.
- **Fulfillment and statements:** Define shipment-notice validation rules and transform account-statement XML into HTML.

#### Key units
- `LogDigest` — aggregates accepted access-log records into request and host statistics.
- `log_digest.pl::main` — reads CLI options, aliases, exclusions, and logs, then emits text or canonical JSON.
- `deploy_host` / `wait_healthy` — deploy a release to a host and poll its health endpoint.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage queue-worker processes and assess heartbeat freshness.
- `run_export` / `write_control` — export settled Oracle payments and record the business day and CSV row count.
- `invoice-service` — configures the `org.example.billing:invoice-service:2.7.0` Maven JAR, migrations, and optional `native-pdf` profile.
- `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob` — provide reminder persistence, email delivery, and processing.
- `reminderScheduler` — schedules reminder processing for 07:00 on weekdays.
- `shipmentNotice` / `ShipmentNoticeType` — define shipment-notice XML structure and parcel requirements.
- `statement-to-html.xsl` — renders account-statement XML as HTML with formatted bookings and totals.

#### Data flow
- Access-log options and aliases → log parsing, host normalization, and exclusion filtering → `LogDigest` aggregation → text or JSON report.
- Environment and release tag → host list → archive transfer and activation → `orderd` restart → health verification.
- Worker process and heartbeat checks → status report and automation exit status.
- Business day and destination → settled-payment query via Oracle `sqlplus` → CSV and `.ctl` output → gzip; failed exports are retried up to three times.
- Spring properties configure persistence and SMTP components → weekday scheduler invokes overdue-reminder processing with a 14-day grace period.
- Shipment XML is checked against `shipment.xsd`; statement XML is transformed with XSLT; Maven builds the invoice-service JAR and configures Flyway migrations.

#### Dependencies
- Perl tools use `Getopt::Long`, `FindBin`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `LogDigest`.
- Deployment uses `lib/common.sh`, environment-specific host lists, `rsync`, `ssh`, `curl`, and remote system utilities; worker tooling invokes `/usr/local/bin/queue-worker`.
- Payment export queries Oracle `settled_payment` and uses `gzip` and `wc`.
- Invoice build inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder configuration uses Spring, HikariCP, and `org.example.reminder` components; shipment and statement assets use the `urn:example:shipment:1` and `urn:example:statement` namespaces.

#### Cross-cutting
- CLI and shell tooling uses options, environment configuration, automation-oriented exit statuses, and timestamped diagnostics.
- Log processing caps input at 50,000 lines and counts requests slower than 1,500 ms.
- Deployment and export scripts include failure handling; the worker wrapper has no traps or retries.
- Reminder configuration uses property placeholders, a scheduler pool size of 2, and a Hikari data-source maximum pool size of 8.
- Shipment constraints include carriers `DHL`, `UPS`, `GLS`, and `POST`, maximum weight 31,500 grams, and default country `DE`; statement output defaults to `EUR` and highlights amounts above 1,000.
