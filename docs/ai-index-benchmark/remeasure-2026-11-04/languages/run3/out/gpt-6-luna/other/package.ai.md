### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:12Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> Supports billing and order operations through access-log analytics, service deployment and worker management, payment exports, reminder scheduling, shipment validation, and statement rendering.

#### Purpose
- Provides operational tools for monitoring and releasing services, exporting settlement data, and summarizing web-server traffic.
- Defines billing-related build and reminder configuration, shipment XML constraints, and account-statement presentation.

#### Responsibilities
- **Log analytics (`perl/`):** Parses access logs, aggregates host and status counts, tracks slow requests, and reports summaries as text or canonical JSON.
- **Service operations (`shell/`):** Deploys tagged `orderd` releases with health checks and manages queue-worker lifecycle and heartbeat status.
- **Settlement exports (`shell/`):** Queries Oracle `settled_payment` for a business day and writes gzipped CSV plus a row-count control file, retrying failures.
- **Billing and reminders (`xml/`):** Configures the `invoice-service` Maven build and Spring overdue-reminder scheduling.
- **Shipment and statement XML (`xml/`):** Constrains shipment notices with `shipment.xsd` and transforms statement XML into HTML with `statement-to-html.xsl`.

#### Key units
- `LogDigest` — parses and aggregates requests by normalized host and status, including slow-request counts.
- `log_digest.pl::main` — reads CLI options and log files, applies host exclusions, and emits text or JSON summaries.
- `deploy_host`, `load_hosts`, `wait_healthy` — select deployment hosts, install a release, restart `orderd`, and poll its `/health` endpoint.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — check and manage the queue worker using its PID and heartbeat files.
- `run_export`, `write_control` — produce the Oracle payment CSV and its business-day and row-count control file.
- `invoice-service` Maven project — packages the service with Flyway migrations and optional `native-pdf` support.
- `overdueReminderJob` — runs `OverdueReminderJob.sendDueReminders` on weekday mornings with a 14-day grace period.
- `shipmentNotice` / `ShipmentNoticeType` — define shipment payload structure and required notice and parcel data.
- `CarrierCode` / `WeightGrams` — constrain shipment carrier values and parcel weights.
- `/st:statement`, `statementHeader`, `statementTotal`, `bookingsByDay` — render statement HTML, headers, totals, and booking groupings.

#### Data flow
- Access-log files → CLI filtering and alias resolution → `LogDigest` parsing and aggregation → text or canonical JSON summary.
- Deployment options and tag → host-list and prerequisite checks → archive transfer and installation → service restart → per-host health polling.
- Worker operation argument → PID/process or lifecycle action; health checks also compare heartbeat age against a 120-second limit.
- Business day and export directory → Oracle query → CSV and row-count control file → gzip; failed exports are retried up to three times.
- Spring reminder configuration → database and mail gateway → weekday job; shipment XML is validated against the schema, while statement XML is transformed to HTML.

#### Dependencies
- **Perl:** `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, and `Exporter`; configuration from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- **Shell:** `lib/common.sh`, environment host lists, `rsync`, `ssh`, `curl`, remote `orderd` commands, queue-worker files and utilities, Oracle `sqlplus`, `settled_payment`, and `gzip`.
- **XML:** Maven parent `org.example.billing:billing-parent:2.7.0`, Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, optional PDFBox; Spring reminder repository, SMTP gateway, job classes, and `reminder.properties`.
- **XML formats:** Shipment types in `urn:example:shipment:1`; statement vocabulary in `urn:example:statement`.

#### Cross-cutting
- Runtime behavior is configurable through CLI options, environment/config files, Maven properties, reminder properties, and stylesheet parameters.
- Log processing defaults to `WARN`, caps input at 50,000 lines, and marks requests over 1,500 ms as slow.
- Operational failure handling varies by task: deployment halts on failed health checks, worker health returns distinct status codes, and exports retry before reporting exhausted retries.
- Reminder execution uses a two-thread scheduler with a 07:00 Monday–Friday cron schedule.
