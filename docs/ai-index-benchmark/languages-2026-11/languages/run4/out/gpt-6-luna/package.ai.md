### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:28:33Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports operational reporting, service maintenance, payment extraction, and customer billing workflows.

#### Purpose
- Groups tools for access-log analysis, release and worker operations, settled-payment export, and invoice/reminder and XML document workflows.
- Provides separate workflows rather than a single end-to-end process.

#### Responsibilities
- **Traffic reporting:** Normalize access-log hosts, apply aliases and exclusions, aggregate traffic and slow-request statistics, and emit text or JSON reports.
- **Service operations:** Deploy tagged releases with host health checks; manage `queue-worker` lifecycle and heartbeat checks.
- **Payment export:** Query settled payments and produce compressed CSV output with a row-count control file.
- **Billing and XML workflows:** Build the invoice-service JAR, schedule overdue reminders, validate shipment notices, and render account statements as HTML.

#### Key units
- `LogDigest`, `log_digest.pl::main` — aggregate normalized access-log statistics and produce reports.
- `deploy_release.sh`, `healthcheck.sh` — deploy releases and manage worker state and heartbeat freshness.
- `nightly_extract.ksh` — export Oracle `settled_payment` rows as CSV, a `.ctl` row-count file, and `.csv.gz`.
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml` — wire reminder services and schedule `OverdueReminderJob`.
- `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `AddressType`, `ParcelType` — define shipment-notice structure and constrained values.
- `statement-to-html.xsl` — render statement headers, booking rows, totals, and large-amount highlighting.

#### Data flow
- Access-log lines pass through alias resolution and host exclusions into `LogDigest`, which aggregates results for text or canonical JSON output.
- Deployment options select hosts and a release tarball; each host receives the release, restarts `orderd`, and undergoes an HTTP health check.
- Worker commands inspect or change process state; health checks also test heartbeat-file age.
- A business day and export directory drive the Oracle payment query, yielding semicolon-delimited CSV, row-count control data, and compressed CSV.
- Spring loads reminder configuration and schedules weekday 07:00 delivery; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.
- Maven builds the invoice-service JAR, runs configured tests, and locates Flyway migrations.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `LogDigest`.
- Deployment uses `lib/common.sh`, environment host lists, `rsync`, SSH, `sudo systemctl`, and `curl`; worker operations use `nohup`, `kill`, and `stat`.
- Payment export uses Oracle `sqlplus` against `settled_payment` and `gzip`.
- Invoice build depends on `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers PostgreSQL, and optional PDFBox.
- Reminder scheduling uses Spring, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- Shipment validation uses XML Schema types and `urn:example:shipment:1`; statement rendering consumes `urn:example:statement`.

#### Cross-cutting
- Configuration comes from CLI options, environment variables, Maven properties, and `reminder.properties`.
- Log processing caps input at 50,000 lines, treats requests over 1,500 ms as slow, lowercases hostnames, and removes trailing dots.
- Deployment stops at the first failed host; payment extraction retries up to three times. Reminder scheduling uses a two-thread pool and a database pool capped at eight.
- Shipment weights must be positive and at most 31,500 grams; omitted recipient country defaults to `DE`. Statement rendering defaults to `EUR` and highlights amounts above `1000`.
