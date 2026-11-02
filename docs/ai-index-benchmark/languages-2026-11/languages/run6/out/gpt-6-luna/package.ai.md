### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:38:49Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports billing and service operations by exporting payments, processing billing documents and reminders, deploying releases, monitoring workers, and reporting access-log activity.

#### Purpose
- Provides operational tooling and XML configuration for billing-service workflows and production maintenance.
- Covers payment exports, invoice and reminder processing, shipment and statement documents, release deployment, worker health, and log reporting.

#### Responsibilities
- **Log analysis:** Parse access logs, apply exclusions, aggregate hosts and statuses, detect slow requests, and produce ranked text or JSON summaries.
- **Release and worker operations:** Deploy tagged `orderd` releases with remote health checks; manage `queue-worker` commands and check process liveness and heartbeat freshness.
- **Payment export:** Query settled payments for a business day and generate compressed CSV exports with row-count control files.
- **Billing and document workflows:** Configure invoice-service builds and migrations, schedule overdue reminders, validate shipment notices, and transform statements into HTML.

#### Key units
- `LogDigest` / `log_digest.pl` — parse, filter, and aggregate access-log entries into digest reports.
- `deploy_release.sh` — transfer and activate tagged releases, restart `orderd`, and check host health.
- `healthcheck.sh` — manage `queue-worker` lifecycle and report liveness and heartbeat status.
- `nightly_extract.ksh` — export settled payments to compressed CSV and write a `.ctl` row count.
- `invoice-service-pom.xml` — configure the Java 17 Maven build, dependencies, migrations, tests, and optional PDFBox profile.
- `scheduler-context.xml` — wire reminder repository, SMTP gateway, and scheduled weekday reminder job.
- `shipment.xsd` — constrain shipment notices, addresses, carriers, and parcel weights.
- `statement-to-html.xsl` — render statement details and totals, highlighting bookings above a threshold.

#### Data flow
- Access-log lines are filtered and parsed by `LogDigest`, aggregated, then emitted as ranked text or JSON.
- Deployment options and host lists select a release archive for remote transfer, activation, restart, and health checks.
- Worker PID and heartbeat files provide inputs for liveness and freshness checks.
- Business-day input and Oracle `settled_payment` data produce CSV, a row-count control file, and a compressed export.
- Reminder properties configure database and SMTP components used by scheduled overdue-reminder jobs.
- Shipment XML is validated against `shipment.xsd`; statement XML is transformed into HTML.
- Maven builds `invoice-service` and applies migrations from its configured resource location.

#### Dependencies
- Perl log tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, `Carp`, and `List::Util`.
- Deployment uses `lib/common.sh`, host lists, SSH, `rsync`, `curl`, `/opt/releases`, and the `orderd` systemd service.
- Worker checks use `/usr/local/bin/queue-worker` and configurable PID and heartbeat files.
- Payment exports use Oracle (`/ AS SYSDBA`), `sqlplus`, and `gzip`.
- Build and reminder configuration references the `billing-parent` Maven project, PostgreSQL, Flyway, Spring, HikariCP, and `org.example.reminder` classes; PDFBox is optional.
- Shipment validation uses XML Schema built-in types; statement rendering uses XSLT 1.0.

#### Cross-cutting
- Configuration is supplied through CLI options, host lists, environment or properties files, and Maven properties.
- Log digestion normalizes host names and defaults to a 50,000-line limit and a 1,500 ms slow-request threshold.
- Operational failure handling is tool-specific: deployment stops at the first unhealthy host, payment export retries up to three times, and worker checks distinguish stopped processes from stale heartbeats.
- XML rules encode constraints including allowed carrier codes, a 31,500-gram parcel limit, and formatted statement amounts.
