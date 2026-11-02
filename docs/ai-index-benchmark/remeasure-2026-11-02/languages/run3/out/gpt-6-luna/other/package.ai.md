### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:55:47Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Provides operational tooling and configuration for access-log reporting, order-service operations, payment exports, and billing, shipment, and statement workflows.

#### Purpose
- Aggregates web-server access logs into host and status metrics, and supports order-service deployment, worker monitoring, and settled-payment exports.
- Defines build and XML configuration for invoice, reminder, shipment-validation, and account-statement workflows.

#### Responsibilities
- **Access-log reporting (`perl/`):** Parses and aggregates requests, applies host aliases and exclusions, ranks busiest hosts, and emits text or JSON summaries.
- **Order operations (`shell/`):** Deploys tagged `orderd` releases with health checks, manages queue-worker lifecycle and heartbeat monitoring, and exports settled Oracle payments to CSV.
- **Billing and XML workflows (`xml/`):** Configures the invoice-service Maven build and reminder scheduler, defines shipment-notice validation, and transforms account statements to HTML.

#### Key units
- `LogDigest` — parses and aggregates request counts by host and status, including slow requests.
- `log_digest.pl::main` — handles options and log files, applies exclusions, and outputs summaries; `top_n` ranks count keys.
- `deploy_host`, `wait_healthy`, `load_hosts` — deploy and verify a release across selected hosts.
- `is_running`, `start_worker`, `stop_worker`, `heartbeat_age` — manage and monitor the queue worker.
- `run_export`, `write_control` — query settled payments and produce CSV and row-count control output.
- `invoice-service` — Maven JAR module configured for invoice creation, numbering, and archiving.
- `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler` — Spring beans and scheduled overdue-reminder delivery.
- `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams` — shipment-notice schema types and constraints.
- `statementHeader`, `statementTotal`, `bookingsByDay` — XSLT templates and key for rendering statement content.

#### Data flow
- Access logs and alias configuration flow through `log_digest.pl` into `LogDigest`; valid, non-excluded records become aggregate metrics and ranked host output.
- Deployment inputs (environment and tag) select hosts and an archive, which are transferred and installed before `orderd` health verification.
- Worker action and PID or heartbeat files determine lifecycle operations and monitoring status.
- A business date selects Oracle `settled_payment` data for CSV export, row-count control output, and compression.
- Reminder properties configure database and mail beans used by the scheduled `OverdueReminderJob`; shipment XML is validated against `shipment.xsd`, and statement XML is transformed to HTML.

#### Dependencies
- Perl reporting uses `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`.
- Deployment uses `lib/common.sh`, environment-specific host files, release archives, `rsync`, `ssh`, and `curl`; worker management targets `/usr/local/bin/queue-worker`.
- Payment export uses local Oracle via `sqlplus`, reads `settled_payment`, and compresses output with `gzip`.
- Invoice build inherits `org.example.billing:billing-parent:2.7.0` and configures Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, and optional PDFBox.
- Reminder configuration depends on `reminder.properties`, HikariCP, Spring, and `org.example.reminder` classes; shipment and statement XML use namespaces `urn:example:shipment:1` and `urn:example:statement`.

#### Cross-cutting
- Configuration is supplied through command-line options, environment defaults, properties, and XML; operational scripts have task-specific failure handling.
- Log aggregation defaults to `WARN`, limits input to 50,000 lines, counts requests over 1,500 ms as slow, and skips malformed records.
- Reminder scheduling uses a two-thread pool and a 14-day grace period; invoice build targets Java 17 and Flyway 11.4.0.
