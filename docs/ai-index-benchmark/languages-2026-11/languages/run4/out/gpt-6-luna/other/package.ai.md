### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:28:23Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports web-traffic reporting, service operations, and billing workflows through log analysis, deployment and export scripts, and XML build, scheduling, validation, and presentation assets.

#### Purpose
- Combines tools for analyzing access logs and operating services with XML assets for invoice builds, reminders, shipment contracts, and account-statement rendering.
- Covers distinct workflows rather than a single end-to-end pipeline.

#### Responsibilities
- **Traffic reporting (`perl/`):** Parse access logs, normalize and aggregate host and status counts, identify slow requests and busiest hosts, and emit text or JSON reports.
- **Service operations (`shell/`):** Deploy tagged releases and verify host health, manage queue-worker lifecycle and heartbeat checks, and export settled payments to compressed CSV with a row-count control file.
- **Billing and XML workflows (`xml/`):** Configure the invoice-service Maven build, schedule overdue reminders, validate shipment notices against an XSD, and transform account statements to HTML.

#### Key units
- `LogDigest` — parses and accumulates normalized access-log traffic statistics; `log_digest.pl::main` — applies aliases and exclusions and produces reports.
- `deploy_release.sh` — deploys a tagged release host by host and checks health; `healthcheck.sh` — manages `queue-worker` and reports heartbeat freshness.
- `nightly_extract.ksh` — queries Oracle `settled_payment` and creates compressed CSV and row-count outputs.
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml` — wires reminder database and mail services and schedules `OverdueReminderJob`.
- `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `AddressType`, `ParcelType` — shipment-notice structure and constrained nested values.
- `statement-to-html.xsl` — renders statement headers, booking rows, totals, and large-amount highlighting.

#### Data flow
- Access-log lines pass through alias resolution and host exclusions into `LogDigest`, which aggregates counts for text or canonical JSON output.
- Deployment options select a host list and release tarball; each host receives the release, restarts `orderd`, and undergoes an HTTP health check.
- Worker commands check or change process state; the health command additionally checks heartbeat-file age.
- A business day and export directory drive the Oracle payment query, which produces a semicolon-delimited CSV, `.ctl` row-count file, and `.csv.gz`.
- Spring loads reminder configuration and schedules weekday 07:00 delivery; shipment XML is checked against `shipment.xsd`, while statement XML is transformed to HTML.
- Maven builds the invoice-service JAR, runs configured tests, and locates Flyway migrations.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `LogDigest`.
- Deployment uses `lib/common.sh`, environment host lists, `rsync`, SSH, `sudo systemctl`, and `curl`; worker operations use `nohup`, `kill`, and `stat`.
- Payment export uses Oracle `sqlplus` against `settled_payment` and `gzip`.
- Invoice build depends on `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL, and optional PDFBox.
- Reminder scheduling uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- Shipment validation uses XML Schema types and `urn:example:shipment:1`; statement rendering consumes `urn:example:statement`.

#### Cross-cutting
- Runtime and build configuration comes from CLI options, environment variables, Maven properties, and `reminder.properties`.
- Log processing caps input at 50,000 lines, counts requests over 1,500 ms as slow, and lowercases hostnames while removing trailing dots.
- Deployment stops at the first failed host; payment extraction retries up to three times. Reminder scheduling uses a two-thread pool and a database pool capped at eight.
- Shipment weights must be positive and no more than 31,500 grams; omitted recipient country defaults to `DE`. Statement rendering defaults to `EUR` and highlights amounts above `1000`.
