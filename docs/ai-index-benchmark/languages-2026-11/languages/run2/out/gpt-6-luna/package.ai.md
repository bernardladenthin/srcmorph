### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:34:09Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> This package supports service operations and billing workflows through access-log reporting, release and worker management, payment exports, and invoice, reminder, shipment, and statement processing.

#### Purpose
- Provides operational tools for deployment, worker health checks, payment extraction, and access-log analysis.
- Supplies build and XML resources for invoice creation, reminders, shipment validation, and statement rendering.

#### Responsibilities
- **Access-log reporting:** Parse and aggregate requests, apply host aliases and exclusions, and produce text or JSON summaries.
- **Service operations:** Deploy tagged releases to staging or production, manage `queue-worker`, and export settled payments as compressed CSV with a control file.
- **Billing and document workflows:** Configure the invoice-service build and reminder schedule, validate shipment notices, and transform account statements to HTML.

#### Key units
- `LogDigest` — parses access-log entries and tracks request, status, and ranked-count statistics.
- `log_digest.pl::main` — applies aliases and exclusions, processes logs, and emits text or JSON reports.
- `deploy_release.sh` — installs tagged releases on selected hosts, restarts `orderd`, and checks host health.
- `healthcheck.sh` — starts, stops, restarts, and reports `queue-worker` status and health.
- `nightly_extract.ksh` — queries Oracle `settled_payment` records and writes compressed CSV and a row-count control file.
- `scheduler-context.xml` — wires reminder database access and SMTP delivery to overdue-reminder processing and weekday scheduling.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment-notice XML contract and constrained fields.
- `statement-to-html.xsl` — renders statement headers, bookings, and totals as HTML.
- `invoice-service` — Maven JAR build for invoice creation, numbering, and archiving.

#### Data flow
- Access logs are filtered and normalized, aggregated by `LogDigest`, then rendered as text or canonical JSON; processing is capped at 50,000 lines and treats requests over 1,500 ms as slow.
- Deployment options select environment hosts and an archive; each host receives the release, switches its `current` link, restarts `orderd`, and undergoes health polling.
- Worker commands manage processes using PID and heartbeat files.
- Payment extraction queries settled records for a business day, counts successful CSV rows, writes a control file, and compresses the export.
- Reminder configuration wires database and SMTP services to a weekday 07:00 job; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.
- The invoice-service Maven build resolves dependencies and configures Flyway migrations from `src/main/resources/db/migration`.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX::strftime`, `List::Util`, and `Carp`.
- Deployment uses `lib/common.sh`, environment host lists, `rsync`, `ssh`, `curl`, and remote system utilities; worker management invokes `/usr/local/bin/queue-worker`.
- Payment extraction uses Oracle `sqlplus` and `gzip`, querying `settled_payment`.
- Invoice-service depends on `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL; PDFBox is optional.
- Reminder configuration uses HikariCP and `org.example.reminder` components; shipment schema and statement stylesheet use XML namespaces and XML/XSLT facilities.

#### Cross-cutting
- Operational settings come from command options and environment values, including deployment credentials, worker file paths, Oracle SID, business date, and export directory.
- Invalid options, log levels, or unreadable logs produce errors; scripts timestamp logs to standard error.
- Deployment stops at the first host failure; payment export retries failed queries up to three times.
- Reminder scheduling uses a connection pool of 8, a scheduler pool of 2, and a 14-day grace period.
- Statement rendering defaults to `EUR`, formats amounts as `#,##0.00`, and highlights amounts above `1000`.
