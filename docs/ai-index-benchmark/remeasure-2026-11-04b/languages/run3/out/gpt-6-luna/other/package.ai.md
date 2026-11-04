### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:54Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and billing resources for analyzing access traffic, deploying and monitoring services, exporting settled payments, and supporting invoice, reminder, shipment, and statement workflows.

#### Purpose
- Combines log summarization, release and worker operations, payment export, and XML/Maven resources for billing workflows.
- Supplies command-line tools and configuration for producing traffic reports, operational health checks, payment files, and billing-related build or document outputs.

#### Responsibilities
- **Access-log analysis (`perl/`):** Parse requests, aggregate host and HTTP-status counts, track slow requests, and emit text or JSON summaries with filtering and aliases.
- **Service operations (`shell/`):** Deploy tagged `orderd` releases to staging or production hosts; manage and check `queue-worker` lifecycle and health.
- **Payment export (`shell/`):** Query Oracle settled payments for a business day and produce a compressed CSV with a row-count control file.
- **Billing resources (`xml/`):** Configure the invoice-service Maven build and reminder scheduler; define shipment validation and statement-to-HTML transformation.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse access-log entries, aggregate traffic metrics, and rank hosts.
- `log_digest.pl` `main`, `read_aliases` — handle CLI input and host mappings, then emit filtered summaries.
- `deploy_host`, `wait_healthy`, `load_hosts` — deploy to selected hosts and verify service health.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — manage queue-worker process state and heartbeat checks.
- `run_export`, `write_control` — query `settled_payment` and record export counts and business day.
- `invoice-service-pom.xml` — configure invoice-service dependencies, Flyway migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wire reminder data access, mail delivery, job execution, and weekday scheduling.
- `shipment.xsd` — define shipment notice, address, carrier, and parcel structure and constraints.
- `statement-to-html.xsl` — render statement headers, booking rows, totals, and threshold-based highlighting.

#### Data flow
- Access logs are read and filtered using aliases and exclusions; valid entries update `LogDigest` host, status, and slow-request metrics, which are emitted as text or canonical JSON.
- Deployment selects an environment’s host list and release archive, then transfers, activates, restarts, and health-checks hosts sequentially.
- Worker lifecycle commands use PID and heartbeat files to determine process status and health.
- The payment export queries Oracle for a selected business day and writes semicolon-separated results, a control count, and a gzipped CSV.
- Reminder properties configure database and SMTP connections that feed `OverdueReminderJob`; shipment XML is validated against `shipment.xsd`, while statement XML is transformed to HTML.
- Maven builds the invoice JAR, runs Flyway migrations, and supplies the configured archive directory to tests.

#### Dependencies
- Perl tooling uses `JSON::PP`, `Getopt::Long`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment-specific host lists, SSH/rsync, `systemctl`, and the service `/health` endpoint; worker scripts invoke `/usr/local/bin/queue-worker`.
- Payment export uses `sqlplus` and Oracle’s `settled_payment` table.
- Invoice build depends on the billing parent POM, Jackson Databind, Flyway, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, and optionally PDFBox.
- Reminder configuration uses HikariCP and `org.example.reminder` repository, gateway, and job classes; XML resources use XML Schema types and XSLT functions.

#### Cross-cutting
- Configuration comes from CLI options, environment variables, or properties; defaults cover deployment credentials, worker paths, Oracle SID, and export directory.
- Failures use tool-specific errors and exit statuses; deployment stops at the first failed host, while payment export retries up to three times.
- Log processing skips malformed entries, limits input to 50,000 lines, and counts requests over 1,500 ms as slow.
- XML resources use explicit namespaces; the reminder scheduler uses a two-thread pool, weekday 7:00 schedule, and 14-day grace period.
