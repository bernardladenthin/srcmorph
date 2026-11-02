### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:41:25Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports operational reporting and service delivery by analyzing web traffic, managing releases and workers, exporting settled payments, and integrating billing documents.

#### Purpose
- Provides scripts, reporting tools, and document/configuration artifacts for service operations and billing workflows.
- Produces access-log summaries, payment extracts, and validated or rendered business documents.

#### Responsibilities
- **Traffic reporting:** Parse access logs, aggregate host and HTTP-status counts, rank results, and emit text or JSON summaries with alias and exclusion support.
- **Service operations:** Deploy tagged `orderd` builds, manage and monitor `queue-worker`, and export daily settled payments from Oracle.
- **Billing and documents:** Configure invoice creation and overdue reminders; validate shipment notices and transform account statements into HTML.

#### Key units
- `LogDigest` and `log_digest.pl` — parse, aggregate, and present access-log digests.
- `deploy_release.sh` — deploy tagged archives and check `orderd` health.
- `healthcheck.sh` — dispatch `queue-worker` lifecycle and health actions.
- `nightly_extract.ksh` — extract Oracle `settled_payment` data into compressed daily exports and control files.
- `invoice-service` — Maven JAR module for creating, numbering, and archiving customer invoices.
- `scheduler-context.xml`, `reminderRepository`, `mailGateway`, `overdueReminderJob` — configure reminder scheduling, JDBC access, SMTP delivery, and due-reminder processing.
- `shipment.xsd` — define shipment-notice and parcel constraints, including `ShipmentNoticeType`, `CarrierCode`, and `WeightGrams`.
- `statement-to-html.xsl` — render account-statement XML as HTML.

#### Data flow
- **Log reporting:** Alias and exclusion configuration plus logs → `LogDigest` parsing and aggregation → timestamped text or JSON summary.
- **Deployment:** Environment and release tag → host selection and archive transfer → remote installation and `orderd` restart → `/health` polling.
- **Worker management:** Requested action → PID and, for health checks, heartbeat inspection → status output and action-specific exit code.
- **Payment export:** Business day and export directory → Oracle query → CSV row count and control file → gzip-compressed extract.
- **Reminders and documents:** Scheduled job invokes `OverdueReminderJob` through JDBC and SMTP; shipment XML is validated against `shipment.xsd`, and statement XML is transformed by XSLT.

#### Dependencies
- Perl tooling uses `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, and `JSON::PP`; the CLI loads `LogDigest`.
- Deployment uses `lib/common.sh`, `rsync`, `ssh`, and `curl`; remote operations use `bash`, `tar`, and `systemctl`. Worker checks use `/usr/local/bin/queue-worker` and PID/heartbeat files.
- Payment extraction uses Oracle `sqlplus`, the `settled_payment` table, `gzip`, and `wc`.
- Invoice build uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; the `native-pdf` profile adds PDFBox.
- Reminder configuration uses HikariCP, `org.example.reminder` components, and `reminder.properties`; XML artifacts consume their respective shipment and account-statement vocabularies.

#### Cross-cutting
- Configuration comes from CLI options, host and alias files, environment variables, properties, and Maven profiles.
- Error handling is tool-specific: Perl validation and input errors raise errors; shell commands use exit codes, and deployment installs an `EXIT` cleanup trap.
- Log digests cap input at 50,000 lines and count requests slower than 1,500 ms; reminder scheduling uses a two-thread pool.
- Shipment XML contracts enforce qualified elements and parcel-weight limits; statement rendering defaults to EUR and highlights amounts above 1,000.
