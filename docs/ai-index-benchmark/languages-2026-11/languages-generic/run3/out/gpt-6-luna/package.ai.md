### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:40:00Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports payment and billing operations by monitoring access, deploying and managing services, exporting settled payments, scheduling overdue reminders, validating shipment notices, and rendering account statements.

#### Purpose
- Provides operational tools for service deployment, worker health, access-log reporting, and settled-payment export.
- Supports billing and logistics workflows, from overdue reminders and shipment validation to statement presentation.

#### Responsibilities
- **Monitoring:** Parse access logs, aggregate request metrics, identify slow requests, and rank hosts with configurable aliases and exclusions.
- **Operations and exports:** Deploy tagged releases with host health checks, manage `queue-worker`, and export Oracle settled-payment rows as compressed CSV with count controls and retries.
- **Billing and reminders:** Configure the invoice-service build and schedule weekday overdue-reminder delivery.
- **Logistics and statements:** Define shipment-notice XML constraints and transform account-statement XML into formatted HTML.

#### Key units
- `LogDigest` and `log_digest.pl::main` — aggregate access-log metrics and produce text or JSON reports.
- `deploy_release.sh::deploy_host(host)` — transfer and activate a tagged release, restart `orderd`, and check host health.
- `healthcheck.sh` — control and report `queue-worker` status using PID and heartbeat files.
- `nightly_extract.ksh::run_export(outfile)` — export Oracle `settled_payment` rows to compressed CSV and write a row-count control file.
- `invoice-service-pom.xml` — configure invoice-service dependencies, migrations, tests, and the optional `native-pdf` profile.
- `scheduler-context.xml` — wire reminder persistence and SMTP delivery to weekday `OverdueReminderJob` scheduling.
- `shipmentNotice` / `ShipmentNoticeType` — define shipment-notice XML structure and required fields.
- `statement-to-html.xsl` — render statement XML with booking rows, formatted totals, and large-amount highlighting.

#### Data flow
- Access logs undergo alias resolution and exclusion filtering before `LogDigest` aggregates requests for ranked text or JSON output.
- Release arguments select an archive and hosts; deployment transfers and activates the release, restarts `orderd`, then polls host health.
- Worker commands and PID/heartbeat files drive process checks and status reporting.
- A business day selects Oracle `settled_payment` rows for CSV export; the script records the row count, compresses the CSV, and retries failed exports.
- Reminder configuration feeds Spring-wired database and mail components that schedule `OverdueReminderJob.sendDueReminders()`.
- Shipment notices are checked against `shipment.xsd`; statement XML is transformed into HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, and `Carp`.
- Deployment uses `lib/common.sh`, environment-specific host lists, SSH, `rsync`, and remote service-management commands; worker control uses `/usr/local/bin/queue-worker`.
- Payment export uses Oracle `sqlplus`, the `settled_payment` table, and `gzip`.
- Invoice build uses `billing-parent:2.7.0`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is profile-only.
- Reminder wiring uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, and `OverdueReminderJob`; XML artifacts use their namespaces, XSD, and XSLT 1.0.

#### Cross-cutting
- Command-line tools signal failures with nonzero exits; deployment stops on health-check failure and payment exports use bounded retries.
- Access-log aggregation ignores malformed lines, caps input at 50,000 lines, and treats requests over 1,500 ms as slow.
- Operational defaults include a 30-second deployment health timeout, 120-second worker heartbeat limit, and 60-second export retry delay.
- Reminder configuration uses an eight-connection HikariCP pool, SMTP port 587, a 14-day grace period, and two scheduler threads.
- Shipment constraints include required notice and parcel identifiers, carriers `DHL`, `UPS`, `GLS`, and `POST`, positive weights up to 31,500 grams, and default country `DE`; statement rendering defaults to `EUR` and highlights amounts above `1000`.
