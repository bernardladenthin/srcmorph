### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:03Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports billing and service operations by analyzing access traffic, managing deployments and workers, exporting settled payments, and supplying invoice and document-processing resources.

#### Purpose
- Provides operational tools and billing resources for traffic reporting, service health, payment exports, and invoice-related workflows.
- Combines command-line utilities with Maven, scheduler, schema, and transformation configuration.

#### Responsibilities
- **Traffic analysis:** Parse and aggregate access logs by host and HTTP status, track slow requests, apply aliases and exclusions, and emit text or JSON reports.
- **Service operations:** Deploy tagged `orderd` releases to staging or production and manage `queue-worker` lifecycle and health.
- **Payment export:** Query Oracle settled payments for a business day and generate a compressed CSV with a row-count control file.
- **Billing resources:** Configure the invoice-service build and reminder scheduler; define shipment validation and statement-to-HTML transformation.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse log entries, aggregate traffic metrics, and rank hosts.
- `log_digest.pl` `main`, `read_aliases` — process CLI options and host mappings, then emit filtered summaries.
- `deploy_host`, `wait_healthy`, `load_hosts` — select hosts, deploy releases, and verify service health.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — manage worker process state and heartbeat checks.
- `run_export`, `write_control` — query `settled_payment` and write export records and control metadata.
- `invoice-service-pom.xml` — configure invoice-service dependencies, migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wire reminder data access, mail delivery, job execution, and weekday scheduling.
- `shipment.xsd` — define shipment notice, address, carrier, and parcel structure and constraints.
- `statement-to-html.xsl` — render statement headers, bookings, totals, and threshold-based highlighting.

#### Data flow
- Access-log lines are filtered and parsed into host, status, and slow-request metrics, then emitted as text or canonical JSON.
- Deployment selects an environment host list and release archive, transfers and activates the release, restarts the service, and checks health sequentially.
- Worker commands use PID and heartbeat files to determine process status and health.
- Payment export queries Oracle for a selected business day and writes semicolon-separated results, a control count, and a gzipped CSV.
- Reminder properties configure database and SMTP connections for `OverdueReminderJob`; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.
- Maven builds the invoice JAR, runs Flyway migrations, and configures the archive directory used by tests.

#### Dependencies
- Perl utilities use `JSON::PP`, `Getopt::Long`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment host lists, SSH/rsync, `systemctl`, and the service `/health` endpoint; worker scripts invoke `/usr/local/bin/queue-worker`.
- Payment export uses `sqlplus` and Oracle’s `settled_payment` table.
- Invoice build depends on the billing parent POM, Jackson Databind, Flyway, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, and optionally PDFBox.
- Reminder configuration uses HikariCP and `org.example.reminder` repository, gateway, and job classes; XML resources use XML Schema types and XSLT functions.

#### Cross-cutting
- Configuration comes from CLI options, environment variables, or properties, with defaults for deployment credentials, worker paths, Oracle SID, and export directory.
- Tool-specific errors and exit statuses report failures; deployment stops at the first failed host, while payment export retries up to three times.
- Log processing skips malformed entries, limits input to 50,000 lines, and counts requests over 1,500 ms as slow.
- The reminder scheduler uses a two-thread pool, weekday 7:00 schedule, and 14-day grace period.
