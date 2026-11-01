### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:35:13Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports billing and payment operations through access-log analysis, service and worker management, payment exports, and invoice, reminder, shipment, and statement workflows.

#### Purpose
- Provides operational tools and configuration for analyzing web-server traffic, deploying and monitoring services, exporting settled payments, and supporting billing workflows.
- Combines Perl reporting utilities, shell scripts, and XML build, scheduling, validation, and rendering assets.

#### Responsibilities
- **Access-log reporting (`perl/`)** — parses and aggregates requests by host and status, identifies slow requests, applies alias-aware host exclusions, and emits text or canonical JSON summaries.
- **Service operations (`shell/`)** — deploys tagged releases across staging or production hosts and controls or health-checks the `queue-worker` process.
- **Payment export (`shell/`)** — queries Oracle `settled_payment` records for a business day and produces compressed CSV output with a row-count control file.
- **Billing workflows (`xml/`)** — configures the invoice-service build and overdue-reminder scheduler, defines shipment-notice validation rules, and transforms statement XML into HTML.

#### Key units
- `LogDigest` — parses access-log records and aggregates request, host, status, and slow-request statistics; `top_n` ranks host counts.
- `log_digest.pl::main` — handles options, aliases, exclusions, log files, and text or JSON reporting.
- `deploy_release.sh` — deploys a tagged archive host by host, restarts `orderd`, and checks `/health`.
- `healthcheck.sh` — manages `queue-worker` using PID and heartbeat files.
- `nightly_extract.ksh` — exports selected Oracle payments to compressed CSV and writes a `.ctl` row count.
- `invoice-service-pom.xml` — configures the Java 17 `org.example.billing:invoice-service` build, migrations, tests, and optional PDFBox support.
- `scheduler-context.xml` — wires reminder database, repository, mail gateway, and weekday `reminderScheduler` for `OverdueReminderJob.sendDueReminders`.
- `shipment.xsd` — defines shipment notice, parcel, address, carrier, and weight constraints in `urn:example:shipment:1`.
- `statement-to-html.xsl` — renders `urn:example:statement` XML as HTML with booking rows, totals, and threshold-based highlighting.

#### Data flow
- **Log analysis:** `log_digest.pl` loads aliases and exclusions, filters log lines, passes accepted records to `LogDigest`, then emits an aggregate summary.
- **Deployment and worker control:** deployment options and environment select hosts and a build archive; each host is updated and checked before rollout continues. Worker operations use PID status and heartbeat age.
- **Payment export:** a business-day filter selects rows from `settled_payment`; query results become CSV, are counted in a control file, and are compressed, with retries on failed exports.
- **Billing assets:** reminder properties configure database and SMTP beans for scheduled delivery; shipment notices are validated against the schema; statement XML is transformed into formatted HTML.

#### Dependencies
- Perl utilities use `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, `JSON::PP`, and `FindBin`.
- Deployment uses `lib/common.sh`, host lists, SSH/`rsync`, `curl`, and remote systemd commands; worker control invokes `/usr/local/bin/queue-worker`.
- Payment export uses `sqlplus`, `ORACLE_SID`, and Oracle table `settled_payment`.
- Invoice build depends on parent POM `org.example.billing:billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers.
- Reminder configuration uses HikariCP, `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`, and `reminder.properties`.
- XML assets use XML Schema and XSLT; the statement stylesheet consumes the `urn:example:statement` vocabulary.

#### Cross-cutting
- Configuration is supplied through command-line options, environment variables, properties, and build settings; defaults include log limits and thresholds, worker paths, Oracle settings, and billing properties.
- Failure handling is tool-specific: log validation and limits can raise errors, deployment stops on detected errors, worker commands use distinct exit codes, and payment exports retry failed queries.
- XML assets use explicit namespaces; shell tools use Bash, POSIX `sh`, and `ksh93` according to their roles.
