### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:41:17Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports operational reporting and service delivery through access-log analysis, release and worker management, payment exports, and billing-related configuration and document contracts.

#### Purpose
- Combines tools for web-traffic summaries, service operations, and settled-payment exports with invoice, reminder, shipment, and account-statement configuration.
- Provides both executable scripts and configuration/schema artifacts for downstream processing and presentation.

#### Responsibilities
- **Access-log reporting (`perl/`):** Parse access logs, aggregate host and HTTP-status counts, rank results, and emit text or canonical JSON; support aliases and exclusions.
- **Service operations (`shell/`):** Deploy tagged `orderd` builds, control and monitor `queue-worker`, and export daily settled payments from Oracle with a compressed CSV and row-count control file.
- **Billing and document integration (`xml/`):** Configure the invoice Maven module and overdue-reminder job; define shipment-notice validation and transform account statements into HTML.

#### Key units
- `LogDigest` — parses access-log entries, tracks host, status, and slow-request counts, and generates ranked summaries.
- `log_digest.pl` — configures aliases and exclusions, reads logs, and renders digest results.
- `deploy_release.sh` — rolls out tagged archives to staging or production hosts and checks `orderd` health.
- `healthcheck.sh` — dispatches `queue-worker` lifecycle and health actions.
- `nightly_extract.ksh` — queries Oracle `settled_payment` and produces compressed daily extracts and control files.
- `scheduler-context.xml`, `reminderRepository`, `mailGateway`, `overdueReminderJob` — wire reminder scheduling, JDBC access, SMTP delivery, and due-reminder processing.
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `shipment.xsd` types including `ShipmentNoticeType`, `CarrierCode`, and `WeightGrams` — constrain shipment notices and parcel data.
- `statement-to-html.xsl` — renders account-statement XML as HTML.

#### Data flow
- **Log reporting:** Alias and exclusion configuration plus log files → `LogDigest` parsing and aggregation → timestamped text or JSON summary.
- **Deployment:** Environment and release tag → host selection and archive transfer → remote installation and `orderd` restart → `/health` polling.
- **Worker management:** Requested action → PID and, for health checks, heartbeat inspection → status output and action-specific exit code.
- **Payment export:** Business day and export directory → Oracle query → CSV row count and control file → gzip-compressed extract.
- **Reminder and documents:** Scheduled job invokes `OverdueReminderJob` through JDBC and SMTP; shipment XML is validated against `shipment.xsd`, while statement XML is transformed by XSLT.

#### Dependencies
- Perl tooling uses `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, and `JSON::PP`; the CLI loads `LogDigest`.
- Deployment uses shared `lib/common.sh`, `rsync`, `ssh`, and `curl`, plus remote `bash`, `tar`, and `systemctl`; worker checks use `/usr/local/bin/queue-worker` and PID/heartbeat files.
- Payment extraction uses Oracle `sqlplus`, `settled_payment`, `gzip`, and `wc`.
- Invoice build uses Jackson, Flyway, PostgreSQL at runtime, JUnit Jupiter, and Testcontainers; the `native-pdf` profile adds PDFBox.
- Reminder configuration uses HikariCP, `org.example.reminder` components, and `reminder.properties`; the shipment schema and statement stylesheet consume their respective XML vocabularies.

#### Cross-cutting
- Configuration is supplied through CLI options, host and alias files, environment or properties, and Maven profiles.
- Error handling is tool-specific: Perl validation and input errors raise errors; shell commands use exit codes, and deployment also installs an `EXIT` cleanup trap.
- Log digests cap input at 50,000 lines and count requests slower than 1,500 ms; reminder scheduling uses a two-thread pool.
- XML contracts enforce qualified shipment elements and parcel-weight limits; statement rendering defaults to EUR and highlights amounts above 1,000.
