### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:35Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports billing and commerce operations through access-log analytics, release and worker operations, payment exports, and XML-defined build, scheduling, validation, and statement rendering.

#### Purpose
- Provides operational tools for monitoring web access, deploying and managing services, and exporting settled payments.
- Defines build and application configuration plus XML contracts and presentation rules for billing, reminders, shipments, and statements.

#### Responsibilities
- **Access-log analytics (`perl/`):** Parses and aggregates host, request, HTTP-status, and slow-request statistics; supports configurable aliases, exclusions, and text or JSON reporting.
- **Service operations (`shell/`):** Deploys tagged `orderd` releases across staging or production hosts, manages queue-worker lifecycle and heartbeat checks, and exports settled payments to compressed CSV with a control file.
- **Billing and document configuration (`xml/`):** Configures the invoice-service Maven build and weekday overdue-reminder job; defines shipment-notice validation and transforms account statements into HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n`, `summarize` — access-log parsing, aggregation, and summary generation.
- `log_digest.pl::main`, `read_aliases` — CLI orchestration and host-alias configuration.
- `deploy_host`, `wait_healthy`, `load_hosts` — staged release installation and per-host health verification.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — queue-worker lifecycle and health reporting.
- `run_export`, `write_control` — settled-payment extraction and export metadata.
- `invoice-service` — Maven project `org.example.billing:invoice-service:2.7.0`, including Flyway setup and optional PDFBox profile.
- `scheduler-context.xml` — Spring wiring and weekday scheduling of `OverdueReminderJob.sendDueReminders`.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams` — shipment XML structure and validation constraints.
- `/st:statement`, `statementHeader`, `statementTotal` — XSLT rendering of statement details, bookings, and totals.

#### Data flow
- Access logs pass through alias resolution and exclusions into `LogDigest`; aggregated results are rendered as text or canonical JSON.
- Deployment options select a host inventory and release archive; hosts receive the release, switch the `current` symlink, restart `orderd`, and undergo HTTP health checks.
- Worker commands manage the process using PID and heartbeat files; payment exports query `settled_payment`, write a CSV and control file, then gzip the CSV.
- Spring loads reminder settings and wires database and SMTP components to the scheduled job; shipment documents are validated against an XSD, while statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `FindBin`, and `LogDigest`; alias configuration comes from `LOG_DIGEST_CONFIG`.
- Shell operations use `lib/common.sh`, `rsync`, `ssh`, `sudo systemctl`, `curl`, `/usr/local/bin/queue-worker`, and Oracle `sqlplus`; deployment uses staging and production host inventories.
- XML build configuration depends on the billing parent POM, Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, and optional PDFBox; reminder wiring uses `reminder.properties`, HikariCP, `JdbcReminderRepository`, and `SmtpMailGateway`.

#### Cross-cutting
- Access-log aggregation caps input at 50,000 lines and counts requests slower than 1,500 ms; hostnames are lowercased and stripped of one trailing dot.
- Deployment stops on the first host failure; payment export retries failures up to three times; worker health checks use a 120-second heartbeat limit.
- XML configuration uses Maven properties, Spring placeholders, XSD restrictions, and XSLT parameters; reminder pools are capped at 8 database connections and 2 scheduler threads.
