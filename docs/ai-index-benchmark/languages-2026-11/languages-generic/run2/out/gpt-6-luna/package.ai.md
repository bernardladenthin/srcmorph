### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:53Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational reporting and automation for billing, payment, shipment, reminder, and account-statement workflows.

#### Purpose
- Combines access-log analytics and production operations with configuration and document handling for invoice and payment services.
- Supports release and worker management, settled-payment exports, overdue reminders, shipment validation, and statement rendering.

#### Responsibilities
- **Traffic analytics:** Parse access logs, aggregate hosts and HTTP statuses, identify slow requests, and emit ranked text or JSON summaries.
- **Production operations:** Deploy tagged `orderd` releases with health checks, manage `queue-worker` lifecycle and heartbeat status, and export daily settled payments from Oracle.
- **Billing and reminders:** Configure the `invoice-service` Maven build and wire persistence, email delivery, and weekday scheduling for overdue reminders.
- **Billing documents:** Validate shipment notices against an XSD and transform account-statement XML into HTML.

#### Key units
- `LogDigest` — parses log lines and aggregates host, status, and slow-request counts.
- `log_digest.pl::main` — handles CLI options, aliases, exclusions, input files, and text or JSON output.
- `deploy_release.sh` — distributes a tagged archive, switches the `current` release, restarts `orderd`, and polls host health.
- `healthcheck.sh` — controls `queue-worker` and checks process and heartbeat status.
- `nightly_extract.ksh` — queries Oracle `settled_payment` for a business day and creates compressed CSV and row-count control output.
- `org.example.billing:invoice-service` — Java 17 Maven JAR configured with database migrations, tests, and optional PDFBox support.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — Spring beans composing reminder persistence, email delivery, and weekday scheduling.
- `shp:shipmentNotice` / `ShipmentNoticeType` — defines shipment XML structure and validation rules in `urn:example:shipment:1`.
- `statementHeader()`, `statementTotal()`, `/st:statement`, `st:booking` — XSLT templates for statement metadata, booking rows, and totals.

#### Data flow
- Access logs pass through CLI aliasing and exclusions into `LogDigest`; aggregated summaries are emitted as text or JSON.
- Deployment options select hosts and an archive, followed by remote extraction, release switching, service restart, and health polling; worker commands use PID and heartbeat files.
- Export options select a business day and destination; an Oracle query produces CSV and a control file before CSV compression.
- Maven and Spring configuration support the invoice build and migrations, and connect reminder properties to persistence, mail delivery, and a scheduled job.
- Shipment XML is validated against the schema; statement XML is transformed into HTML with formatted booking amounts and a calculated total.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, and `List::Util`; deployment uses `lib/common.sh`, SSH, `rsync`, and remote `tar`, `systemctl`, and `curl`.
- Worker management invokes `/usr/local/bin/queue-worker`; payment export uses `sqlplus`, `gzip`, and Oracle’s `settled_payment` table.
- The Maven build uses `billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; the `native-pdf` profile enables PDFBox.
- Reminder wiring uses Spring Beans, Context, and Task, HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- Shipment validation uses XML Schema built-in types; statement rendering uses XSLT 1.0 and XPath functions.

#### Cross-cutting
- Configuration spans CLI options, environment overrides, properties, Maven, Spring XML, XSD, and XSLT.
- Log processing ignores malformed lines, limits input to 50,000 lines, classifies requests slower than 1,500 ms, and lowercases host names while stripping one trailing dot.
- Operational scripts report progress and failures; deployment aborts on failed health checks, worker health distinguishes stopped and stale-heartbeat states, and export retries up to three times.
- Reminder scheduling runs weekdays at 07:00 with scheduler pool size 2 and data-source maximum pool size 8; shipment rules constrain carrier codes and parcel weight, while statement rendering defaults to EUR and highlights amounts above 1,000.
