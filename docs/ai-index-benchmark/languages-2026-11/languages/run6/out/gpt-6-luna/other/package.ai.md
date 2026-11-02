### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:38:41Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports billing and service operations through payment exports, invoice and reminder configuration, shipment and statement processing, release deployment, worker health checks, and access-log reporting.

#### Purpose
- Provides operational scripts, log-analysis tooling, and XML build and business-process configuration for billing services.
- Covers deployment and worker maintenance as well as payment, reminder, shipment, and statement workflows.

#### Responsibilities
- **Access-log reporting (`perl/`):** Parse server logs, aggregate host and status counts, identify slow requests, and emit ranked text or JSON summaries.
- **Release and worker operations (`shell/`):** Deploy tagged `orderd` releases with health probes; manage `queue-worker` lifecycle and heartbeat checks.
- **Payment export (`shell/`):** Query settled payments for a business day and produce compressed CSV output with row-count control files.
- **Billing and document configuration (`xml/`):** Configure invoice-service builds and migrations, schedule overdue reminders, define shipment-notice validation, and transform account statements to HTML.

#### Key units
- `LogDigest` — parses and aggregates access-log entries; `log_digest.pl` provides filtering and digest output.
- `deploy_release.sh` — deploys a tagged release to staging or production and checks each host’s `/health` endpoint.
- `healthcheck.sh` — manages `queue-worker` commands and reports liveness and heartbeat freshness.
- `nightly_extract.ksh` — exports settled payments to compressed CSV and writes a `.ctl` row count.
- `invoice-service-pom.xml` — configures the Java 17 Maven build, dependencies, migrations, tests, and optional PDFBox profile.
- `scheduler-context.xml` — wires reminder repository, SMTP gateway, and job; schedules weekday reminder delivery.
- `shipment.xsd` — constrains shipment notices, addresses, carriers, and parcel weights.
- `statement-to-html.xsl` — renders statement details and totals, highlighting bookings above a threshold.

#### Data flow
- Access-log lines pass through CLI exclusions and `LogDigest` parsing and aggregation, then become ranked text or JSON summaries.
- Deployment options and host lists select a local release archive, which is transferred to remote hosts, activated, restarted, and health-checked.
- Worker PID and heartbeat files feed liveness and freshness checks.
- Business-day input and Oracle `settled_payment` data produce a CSV, row-count control file, and compressed export.
- Reminder properties configure database and SMTP beans used by scheduled overdue-reminder jobs.
- Shipment XML is validated against `shipment.xsd`; statement XML is transformed into HTML.
- Maven builds `invoice-service` and applies migrations from the configured resource location.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, `Carp`, and `List::Util`.
- Deployment uses `lib/common.sh`, host lists, SSH, `rsync`, `curl`, `/opt/releases`, and the `orderd` systemd service.
- Worker checks use `/usr/local/bin/queue-worker` and configurable PID and heartbeat files.
- Payment exports use Oracle (`/ AS SYSDBA`), `sqlplus`, and `gzip`.
- XML build and reminder configuration references the `billing-parent` Maven project, PostgreSQL, Flyway, Spring, HikariCP, and `org.example.reminder` classes; PDFBox is optional.
- Shipment validation uses XML Schema built-in types; statement rendering uses XSLT 1.0.

#### Cross-cutting
- Configuration comes from CLI options, host lists, environment or properties files, and Maven properties.
- Log digestion normalizes host names; its defaults include a 50,000-line limit and a 1,500 ms slow-request threshold.
- Operational failure handling is tool-specific: deployment stops at the first unhealthy host, payment export retries up to three times, and worker checks distinguish stopped workers from stale heartbeats.
- XML rules encode explicit constraints, including allowed carrier codes, a 31,500-gram parcel limit, and formatted statement amounts.
