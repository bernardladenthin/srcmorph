### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:44Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational tooling and document configuration for billing, payment processing, service operations, and commerce monitoring.

#### Purpose
- Supports access-log analysis, service deployment and worker management, and settled-payment exports.
- Defines billing build and scheduling configuration, shipment validation, and statement rendering.

#### Responsibilities
- **Log analytics:** Parses and aggregates access logs, with host aliases, exclusions, and text or JSON output.
- **Service operations:** Deploys `orderd` releases, manages queue-worker processes and health checks, and exports settled payments to compressed CSV.
- **Billing and documents:** Configures the invoice-service build and overdue-reminder schedule; validates shipment notices and transforms statement XML to HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n`, `summarize` — parse and aggregate access-log data.
- `log_digest.pl::main`, `read_aliases` — coordinate reporting and load host aliases.
- `deploy_host`, `wait_healthy`, `load_hosts` — install releases and verify host health.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage queue-worker lifecycle and report heartbeat status.
- `run_export`, `write_control` — export settled payments and write export metadata.
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, with Flyway configuration and an optional PDFBox profile.
- `scheduler-context.xml` — wires and schedules `OverdueReminderJob.sendDueReminders` on weekdays.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams` — define shipment XML structure and validation constraints.
- `/st:statement`, `statementHeader`, `statementTotal` — render statement details, bookings, and totals.

#### Data flow
- Access logs pass through alias resolution and exclusions into `LogDigest`, then produce text or canonical JSON summaries.
- Deployment selects a host inventory and release archive, installs the release, switches the `current` symlink, restarts `orderd`, and checks HTTP health.
- Worker commands use PID and heartbeat files; payment exports query `settled_payment`, write CSV and control metadata, then gzip the CSV.
- Spring loads reminder settings and connects database and SMTP components to the scheduled job; shipment XML is XSD-validated and statement XML is transformed to HTML.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `FindBin`, and `LogDigest`; aliases are configured via `LOG_DIGEST_CONFIG`.
- Shell operations use `lib/common.sh`, `rsync`, `ssh`, `sudo systemctl`, `curl`, `/usr/local/bin/queue-worker`, and Oracle `sqlplus`.
- The Maven build depends on the billing parent POM, Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, and optional PDFBox.
- Reminder wiring uses `reminder.properties`, HikariCP, `JdbcReminderRepository`, and `SmtpMailGateway`.

#### Cross-cutting
- Access-log processing caps input at 50,000 lines, counts requests slower than 1,500 ms, and normalizes hostnames by lowercasing and removing one trailing dot.
- Deployment stops on the first host failure; payment export retries failures up to three times; worker health checks use a 120-second heartbeat limit.
- XML configuration uses Maven properties, Spring placeholders, XSD restrictions, and XSLT parameters; reminder resources are capped at 8 database connections and 2 scheduler threads.
