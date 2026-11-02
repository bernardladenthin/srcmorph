### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:34:01Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports service operations and billing workflows through access-log analysis, release and worker management, payment exports, and invoice, reminder, shipment, and statement assets.

#### Purpose
- Provides command-line tools for operational deployment, queue-worker health, and payment extraction, alongside XML build and workflow resources for billing.
- Includes access-log aggregation and reporting for request, host, status, and slow-request statistics.

#### Responsibilities
- **Log analysis (`perl/`):** Parse and aggregate access logs; apply host aliases and exclusions; report totals and ranked hosts as text or JSON.
- **Service operations (`shell/`):** Deploy tagged releases to staging or production, manage and health-check `queue-worker`, and export settled payments to compressed CSV with a control file.
- **Billing and operations XML (`xml/`):** Configure the invoice-service Maven build and reminder scheduling; define shipment-notice validation rules; transform account statements into HTML.

#### Key units
- `LogDigest` — parses access-log entries, updates request and status counters, and provides summaries and ranked counts.
- `log_digest.pl::main` — applies aliases and exclusions, processes logs, and emits text or JSON reports.
- `deploy_release.sh` — installs a tagged archive across selected hosts, restarts `orderd`, and checks host health.
- `healthcheck.sh` — starts, stops, restarts, and reports the status and health of `queue-worker`.
- `nightly_extract.ksh` — queries Oracle `settled_payment` data, retries exports, and writes a compressed CSV and row-count control file.
- `scheduler-context.xml` — wires reminder database access, SMTP delivery, overdue-reminder processing, and weekday scheduling.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment-notice XML contract and its constrained fields.
- `statement-to-html.xsl` — renders statement headers, bookings, and totals as HTML.
- `invoice-service` — Maven JAR build for invoice creation, numbering, and archiving.

#### Data flow
- Access-log files pass through alias and exclusion handling, then `LogDigest` parsing and aggregation; summaries and ranked hosts are rendered as text or canonical JSON.
- Release options select an environment’s host list and archive; each host receives the release, switches its `current` link, restarts `orderd`, and undergoes health polling.
- Worker commands use PID and heartbeat files to manage processes and assess health.
- The payment export queries settled records for a business day, counts successful CSV rows, writes a control file, and compresses the CSV.
- Reminder configuration loads properties and wires database and SMTP services to a weekday 07:00 job; shipment XML is checked against `shipment.xsd`, while statement XML is transformed to HTML.
- The Maven build resolves invoice-service dependencies and configures Flyway migrations from `src/main/resources/db/migration`.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `POSIX::strftime`, `List::Util`, and `Carp`.
- Deployment uses `lib/common.sh`, environment-specific host lists, `rsync`, `ssh`, `curl`, and remote system utilities; worker management invokes `/usr/local/bin/queue-worker`.
- Payment extraction uses Oracle `sqlplus` and `gzip`; the query targets `settled_payment`.
- Invoice build depends on `billing-parent`, Jackson Databind, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers PostgreSQL, with optional PDFBox.
- Reminder configuration uses HikariCP and `org.example.reminder` components; shipment schema and statement stylesheet use their respective XML namespaces and built-in XML/XSLT facilities.

#### Cross-cutting
- Operational controls are supplied through command options and environment settings, including deployment credentials, worker file paths, Oracle SID, business date, and export directory.
- Log processing caps input at 50,000 lines, counts requests over 1,500 ms as slow, and normalizes host names; invalid options, levels, and unreadable logs produce errors.
- Deployment and export scripts log timestamps to standard error; deployment stops at the first host failure, and export retries failed queries up to three times.
- Reminder scheduling uses a connection pool of 8, a scheduler pool of 2, and a 14-day grace period.
- Statement rendering defaults to `EUR`, formats amounts as `#,##0.00`, and highlights amounts above `1000`.
