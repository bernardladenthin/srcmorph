### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:45Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports service operations and billing workflows through traffic and payment reporting, release and worker management, reminder scheduling, shipment validation, and statement rendering.

#### Purpose
- Combines access-log analytics and production operations scripts with XML-based build, scheduling, validation, and statement-rendering artifacts.
- Supports invoice-service and related payment, shipment, reminder, and account-statement workflows.

#### Responsibilities
- **Traffic analytics:** Parse access logs, normalize and aggregate hosts and HTTP statuses, identify slow requests, and produce ranked text or JSON summaries.
- **Production operations:** Deploy tagged `orderd` releases with health checks, manage `queue-worker` lifecycle and heartbeat health, and export daily settled payments from Oracle.
- **Billing configuration and workflows:** Configure the `invoice-service` Maven build and weekday overdue-reminder job.
- **Billing data formats:** Validate shipment notices against an XSD and transform account-statement XML into HTML.

#### Key units
- `LogDigest` — parses access-log lines and aggregates host, status, and slow-request counts; provides summaries and ranked results.
- `log_digest.pl::main` — handles CLI options, aliases, exclusions, log-file input, and text or JSON output.
- `deploy_release.sh` — distributes a tagged archive, switches the `current` release, restarts `orderd`, and polls host health.
- `healthcheck.sh` — controls `queue-worker` and checks process and heartbeat status.
- `nightly_extract.ksh` — queries `settled_payment` for a business day and creates a compressed CSV with a row-count control file.
- `org.example.billing:invoice-service` — Maven module configured as a Java 17 JAR with database migrations, tests, and optional PDFBox support.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — Spring beans composing reminder persistence, email delivery, and weekday scheduling.
- `shp:shipmentNotice` / `ShipmentNoticeType` — shipment document structure and validation rules in `urn:example:shipment:1`.
- `statementHeader()`, `statementTotal()`, `/st:statement`, and `st:booking` — XSLT templates for account-statement metadata, booking rows, and totals.

#### Data flow
- Access logs pass through CLI aliasing and exclusions, then `LogDigest` parsing and aggregation; summaries are emitted as text or JSON.
- Deployment options select hosts and an archive, followed by remote extraction, release switching, service restart, and health polling. Worker commands use PID and heartbeat files to report or control state.
- Export options select a business day and destination; an Oracle query produces CSV and a control file, then the CSV is compressed.
- Maven settings configure the invoice-service build and Flyway migrations; reminder properties feed Spring-wired persistence and mail services into a weekday scheduled job.
- Shipment XML is checked against the shipment schema; statement XML is transformed into HTML with formatted booking amounts and a calculated total.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, and `List::Util`; deployment uses `lib/common.sh`, SSH, `rsync`, and remote `tar`, `systemctl`, and `curl`.
- Worker management invokes `/usr/local/bin/queue-worker` and POSIX utilities; payment export uses `sqlplus`, `gzip`, and Oracle’s `settled_payment` table.
- The Maven build depends on `billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; the `native-pdf` profile enables PDFBox.
- Reminder wiring uses Spring Beans, Context, and Task, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- Shipment validation uses XML Schema built-in types; statement rendering uses XSLT 1.0 and XPath functions.

#### Cross-cutting
- Configuration is expressed through CLI options, environment overrides, properties, Maven, Spring XML, XSD, and XSLT.
- Log processing ignores malformed lines, limits input to 50,000 lines, and classifies requests slower than 1,500 ms; host names are lowercased and stripped of one trailing dot.
- Operational scripts report progress and failures; deployment aborts on failed health checks, worker health distinguishes stopped and stale-heartbeat states, and export retries up to three times.
- Reminder scheduling runs weekdays at 07:00 with scheduler pool size 2 and data-source maximum pool size 8; shipment rules constrain carrier codes and parcel weight, while statement rendering defaults to EUR and highlights amounts above 1,000.
