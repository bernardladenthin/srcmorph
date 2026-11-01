### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:39:57Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports operational analysis and service workflows alongside billing, payment, shipment, reminder, and statement-processing tasks.

#### Purpose
- Provides tools for analyzing access logs, deploying and monitoring services, and exporting settled payments.
- Bundles build, scheduling, validation, and rendering assets for invoice and customer-document workflows.

#### Responsibilities
- **Log analysis:** Parse and aggregate web requests, rank hosts and statuses, identify slow requests, and emit text or JSON summaries.
- **Service and payment operations:** Deploy tagged releases, manage and health-check `queue-worker`, and export settled Oracle payments with retry handling.
- **Billing and customer documents:** Configure invoice-service builds and overdue reminders; define shipment XML validation and transform account statements to HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse, normalize, aggregate, and rank access-log data.
- `log_digest.pl::main`, `read_aliases` — apply CLI options and host aliases, process logs, and emit summaries.
- `deploy_release.sh` — deploy releases to environment hosts, restart `orderd`, and poll host health.
- `healthcheck.sh` — manage `queue-worker` and assess heartbeat freshness.
- `nightly_extract.ksh` — export settled-payment fields from `settled_payment` to CSV and a control file.
- `invoice-service` Maven module — configure Java 17 builds, migrations, dependencies, and optional PDFBox support.
- `scheduler-context.xml` — wire reminder dependencies and schedule `OverdueReminderJob`.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — define and constrain the shipment notice XML contract.
- `statement-to-html.xsl` — transform statement XML into HTML with booking rows and totals.

#### Data flow
- Log CLI loads aliases, excludes configured hosts, passes remaining lines to `LogDigest`, and prints aggregate counts and host rankings.
- Deployment reads a tag and host list, transfers and activates the release, restarts `orderd`, then checks `/health`; worker checks use PID and heartbeat files.
- Payment export queries `settled_payment` for a business day, writes semicolon-separated CSV and row-count control data, then compresses the CSV.
- Reminder properties configure database and SMTP dependencies injected into `OverdueReminderJob`, scheduled for weekday mornings.
- Shipment notices are validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed by XSLT into HTML.
- Maven builds the invoice-service JAR with migration resources and passes the archive directory to tests.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment host lists, `rsync`, `ssh`, `systemctl`, and `curl`; worker operations invoke `/usr/local/bin/queue-worker`.
- Payment export uses SQL*Plus, Oracle `settled_payment`, and `gzip`.
- Invoice-service inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder wiring uses `reminder.properties`, Spring, HikariCP, and `org.example.reminder` repository, gateway, and job classes.

#### Cross-cutting
- Log parsing ignores malformed entries, caps input at 50,000 lines, and counts requests slower than 1,500 ms; the CLI validates top-count options and reports log-file open failures.
- Deployment processes hosts sequentially and stops on unhealthy hosts; payment exports retry up to three times; worker checks have no retry or timeout.
- Operational scripts differ in shell and output conventions: deployment and export timestamps go to stderr, while worker messages go to stdout.
- Reminder database and SMTP settings are externalized; scheduling uses a two-thread pool at 07:00 Monday–Friday with a 14-day grace period.
- Shipment schema defaults country to `DE`, requires positive weights up to 31,500 grams, and permits one or more parcels; statement rendering defaults to `EUR`, highlights amounts above 1,000, and formats values as `#,##0.00`.
