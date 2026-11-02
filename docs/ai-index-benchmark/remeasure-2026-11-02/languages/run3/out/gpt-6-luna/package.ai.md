### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:55:54Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Supports operational reporting and service workflows for order processing, payment export, billing reminders, shipment validation, and customer statements.

#### Purpose
- Provides tools to analyze access logs and deploy, monitor, and support order services.
- Configures invoice, overdue-reminder, shipment-validation, payment-export, and account-statement workflows.

#### Responsibilities
- **Access-log reporting:** Parse requests, apply host aliases and exclusions, aggregate status and slow-request counts, and emit ranked text or JSON summaries.
- **Order operations:** Deploy tagged `orderd` releases with health checks; manage queue-worker lifecycle and heartbeat monitoring.
- **Payment export:** Query settled Oracle payments by business date and produce compressed CSV plus row-count control output.
- **Billing and XML workflows:** Configure invoice builds and reminder scheduling, define shipment-notice validation, and transform account statements to HTML.

#### Key units
- `LogDigest`, `log_digest.pl::main`, `top_n` — aggregate and rank access-log metrics, with command-line handling and configurable exclusions.
- `deploy_host`, `wait_healthy`, `load_hosts` — select hosts, deploy releases, and verify `orderd` health.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage and monitor queue-worker state.
- `run_export`, `write_control` — export settled-payment data and write row-count control output.
- `invoice-service` — Maven JAR configuration for invoice creation, numbering, and archiving.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — configure and schedule overdue-reminder delivery.
- `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams` — shipment-notice schema types and constraints.
- `statementHeader`, `statementTotal`, `bookingsByDay` — XSLT templates and key for rendering statement content.

#### Data flow
- Access logs and alias configuration feed `log_digest.pl` and `LogDigest`; valid, non-excluded records produce aggregate metrics and ranked output.
- Deployment environment and tag select hosts and a release archive; transfer and installation precede `orderd` health verification.
- Worker action and PID or heartbeat files determine lifecycle operations and monitoring results.
- Business date selects `settled_payment` records for CSV export, control output, and compression.
- Reminder properties configure database and mail beans for scheduled `OverdueReminderJob` execution; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment-specific host files, release archives, `rsync`, `ssh`, and `curl`; worker management targets `/usr/local/bin/queue-worker`.
- Payment export uses local Oracle through `sqlplus`, reads `settled_payment`, and uses `gzip`.
- Invoice build inherits `org.example.billing:billing-parent:2.7.0` and configures Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional PDFBox.
- Reminder configuration uses `reminder.properties`, HikariCP, Spring, and `org.example.reminder` classes; shipment and statement XML use `urn:example:shipment:1` and `urn:example:statement`.

#### Cross-cutting
- Configuration comes from command-line options, environment defaults, properties, and XML; operational scripts use task-specific failure handling.
- Log aggregation defaults to `WARN`, processes at most 50,000 lines, counts requests over 1,500 ms as slow, and skips malformed records.
- Reminder scheduling uses a two-thread pool and a 14-day grace period; invoice builds target Java 17 and Flyway 11.4.0.
