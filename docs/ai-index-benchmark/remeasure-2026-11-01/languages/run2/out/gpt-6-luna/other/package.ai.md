### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:39:50Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling for web-log analysis, service deployment and payment exports, alongside XML configuration and document processing for billing, reminders, shipments, and statements.

#### Purpose
- Combines command-line log aggregation and service operations with billing, reminder, shipment-validation, and statement-rendering assets.
- Supports release and worker management, settled-payment exports, and invoice-service build configuration.

#### Responsibilities
- **Access-log analysis (`perl/`):** Parse and aggregate requests by host and status, identify slow requests, apply aliases and exclusions, and emit ranked text or JSON summaries.
- **Service operations (`shell/`):** Deploy tagged releases sequentially, manage and health-check `queue-worker`, and export settled Oracle payments with retry handling.
- **Billing and customer XML (`xml/`):** Configure invoice-service builds and scheduled overdue reminders; validate shipment notices and transform account statements into HTML.

#### Key units
- `LogDigest`, `parse_line`, `top_n` — parse, normalize, aggregate, and rank access-log data.
- `log_digest.pl::main`, `read_aliases` — apply CLI options and host mappings, process logs, and output summaries.
- `deploy_release.sh` — deploy a release to environment hosts, restart `orderd`, and poll host health.
- `healthcheck.sh` — manage `queue-worker` and assess heartbeat freshness.
- `nightly_extract.ksh` — export settled-payment fields from `settled_payment` to CSV and a control file.
- `invoice-service` Maven module — configure Java 17 builds, Flyway migrations, dependencies, and optional PDFBox support.
- `scheduler-context.xml` — wire reminder repository, SMTP gateway, and the scheduled `OverdueReminderJob`.
- `shipmentNotice` / `ShipmentNoticeType` — define the shipment notice XML contract and its required identifier.
- `CarrierCode`, `WeightGrams`, `ParcelType`, `AddressType` — constrain shipment carriers, parcel data, weights, and recipient addresses.
- `statement-to-html.xsl` — transform statement XML into formatted HTML with booking rows and totals.

#### Data flow
- The log CLI loads aliases, filters excluded hosts, passes remaining lines to `LogDigest`, and prints aggregated counts and host rankings.
- Deployment reads a tag and host list, transfers and activates the release, restarts `orderd`, then checks `/health`; worker checks use PID and heartbeat files.
- The payment export queries `settled_payment` for a business day, writes semicolon-separated CSV and row-count control data, then compresses the CSV; failures are retried.
- Reminder properties configure database and SMTP dependencies injected into `OverdueReminderJob`, scheduled for weekday mornings.
- Shipment notice XML is validated against `shipment.xsd`; statement XML in `urn:example:statement` is transformed by XSLT into HTML.
- Maven configuration builds the invoice-service JAR with migration resources and passes the archive directory to tests.

#### Dependencies
- Perl tooling uses `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment host lists, `rsync`, `ssh`, `systemctl`, and `curl`; worker operations invoke `/usr/local/bin/queue-worker`.
- Payment export uses SQL*Plus with `CONNECT / AS SYSDBA`, the Oracle `settled_payment` table, and `gzip`.
- Invoice-service inherits from `org.example.billing:billing-parent:2.7.0` and uses Jackson, Flyway, PostgreSQL, JUnit Jupiter, and Testcontainers; PDFBox is optional.
- Reminder wiring uses `reminder.properties`, Spring, HikariCP, and `org.example.reminder` repository, gateway, and job classes.

#### Cross-cutting
- Log parsing ignores malformed entries, caps input at 50,000 lines, and counts requests slower than 1,500 ms; CLI rejects invalid top counts and fails on log-file open errors.
- Deployment processes hosts sequentially and stops on unhealthy hosts; payment exports retry up to three times; worker checks have no retry or timeout.
- Operational scripts differ in shell and output conventions; deployment and export log timestamps to stderr, while worker messages go to stdout.
- Reminder database and SMTP settings are externalized; scheduling uses a two-thread pool at 07:00 Monday–Friday with a 14-day grace period.
- Shipment schema defaults country to `DE`, requires positive weights up to 31,500 grams, and permits one or more parcels; statement rendering defaults to `EUR`, highlights amounts above 1,000, and formats values as `#,##0.00`.
