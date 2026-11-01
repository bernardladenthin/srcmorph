### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:35:21Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports billing and payment operations by analyzing service traffic, managing deployments and workers, exporting settled payments, and handling invoice, reminder, shipment, and statement workflows.

#### Purpose
- Provides operational tooling and configuration for service monitoring, payment exports, and billing processes.
- Brings together Perl, shell, and XML assets for reporting, deployment, scheduling, validation, and rendering.

#### Responsibilities
- **Traffic analysis:** Parse access logs, aggregate request and status metrics, identify slow requests, apply host aliases and exclusions, and produce text or JSON reports.
- **Service operations:** Deploy tagged releases across staging or production hosts; manage and health-check the `queue-worker`.
- **Payment export:** Select Oracle `settled_payment` records for a business day and produce compressed CSV with a row-count control file.
- **Billing workflows:** Configure the invoice-service build and overdue-reminder scheduler, validate shipment notices, and render statement XML as HTML.

#### Key units
- `LogDigest` — aggregates access-log request, host, status, and slow-request statistics, with `top_n` host ranking.
- `log_digest.pl::main` — loads log options, aliases, and exclusions, then emits text or JSON summaries.
- `deploy_release.sh` — deploys a tagged archive host by host, restarts `orderd`, and checks `/health`.
- `healthcheck.sh` — manages `queue-worker` using PID and heartbeat files.
- `nightly_extract.ksh` — exports selected Oracle payments to compressed CSV and writes a `.ctl` row count.
- `invoice-service-pom.xml` — configures the Java 17 `org.example.billing:invoice-service` build, migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder persistence and mail delivery to the weekday `reminderScheduler` and `OverdueReminderJob.sendDueReminders`.
- `shipment.xsd` — constrains shipment notices, parcels, addresses, carriers, and weights in `urn:example:shipment:1`.
- `statement-to-html.xsl` — transforms `urn:example:statement` XML into HTML with booking rows, totals, and threshold highlighting.

#### Data flow
- **Logs:** `log_digest.pl` loads aliases and exclusions, filters log lines, sends accepted records to `LogDigest`, and emits aggregated output.
- **Deployment and workers:** Environment and options select hosts and a build archive; hosts are updated and checked in sequence. Worker control uses PID status and heartbeat age.
- **Payments:** A business-day filter selects `settled_payment` rows; query results become CSV, are counted in a control file, and are compressed, with retries for failed exports.
- **Billing documents and reminders:** Properties configure reminder database and SMTP beans for scheduled delivery; shipment notices are schema-validated; statement XML is transformed to HTML.

#### Dependencies
- Perl utilities use `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- Deployment uses `lib/common.sh`, host lists, SSH/`rsync`, `curl`, and remote systemd commands; worker control invokes `/usr/local/bin/queue-worker`.
- Payment export uses `sqlplus`, `ORACLE_SID`, and Oracle table `settled_payment`.
- Invoice build depends on parent POM `org.example.billing:billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers.
- Reminder configuration uses HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`; XML assets use XML Schema and XSLT.

#### Cross-cutting
- Configuration comes from command-line options, environment variables, properties, and build settings.
- Error handling is tool-specific: log validation and limits can fail, deployment stops on detected errors, worker commands use distinct exit codes, and payment exports retry failed queries.
- XML assets use explicit namespaces; shell tools use Bash, POSIX `sh`, or `ksh93` according to role.
