### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:20Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> Provides operational and billing support for monitoring traffic, deploying order services, managing workers, exporting payments, scheduling reminders, and validating or rendering business documents.

#### Purpose
- Groups operational automation and billing artifacts for service release, settlement export, reminder delivery, and shipment and account-statement processing.
- Analyzes web access logs and emits summarized traffic metrics.

#### Responsibilities
- **Traffic analytics:** Parse access logs, normalize and filter hosts, aggregate status counts, track slow requests, and produce text or JSON reports.
- **Service and worker operations:** Deploy tagged `orderd` releases with health checks; inspect, start, and stop queue workers using PID and heartbeat state.
- **Settlement export:** Query Oracle `settled_payment` for a business day and generate gzipped CSV output with a row-count control file and retry handling.
- **Billing and reminders:** Define the `invoice-service` Maven build and configure weekday overdue-reminder execution.
- **Business document formats:** Validate shipment notices against an XML schema and transform statement XML into HTML.

#### Key units
- `LogDigest`, `log_digest.pl::main` — parse, aggregate, filter, and report access-log traffic.
- `deploy_host`, `load_hosts`, `wait_healthy` — select hosts, install a release, restart `orderd`, and check `/health`.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — inspect and manage queue-worker lifecycle and health.
- `run_export`, `write_control` — export payment rows and write business-day and row-count metadata.
- `invoice-service` Maven project — package the billing service with Flyway migrations and optional `native-pdf` support.
- `overdueReminderJob` — schedule `OverdueReminderJob.sendDueReminders` with a 14-day grace period.
- `shipmentNotice`, `ShipmentNoticeType`, `CarrierCode`, `WeightGrams` — define and constrain shipment notice and parcel data.
- `/st:statement`, `statementHeader`, `statementTotal`, `bookingsByDay` — render statement structure, totals, and daily booking groups as HTML.

#### Data flow
- Access-log files pass through CLI filtering and host alias resolution into `LogDigest`, then produce text or canonical JSON summaries.
- Deployment options and release tag drive host selection, archive transfer, installation, service restart, and per-host health polling.
- Worker commands inspect PID/process and heartbeat state or perform lifecycle actions; health checks use a 120-second heartbeat limit.
- Business day and export directory drive the Oracle query, CSV and control-file generation, and gzip; failures are retried up to three times.
- Reminder configuration invokes the weekday job through database and mail-gateway dependencies; shipment XML is schema-validated and statement XML is transformed to HTML.

#### Dependencies
- **Log analytics:** Perl modules including `Getopt::Long`, `JSON::PP`, `POSIX`, `Carp`, and `Exporter`; configuration from `$ENV{LOG_DIGEST_CONFIG}` or `/etc/log-digest.conf`.
- **Operations and exports:** `lib/common.sh`, host-list configuration, `rsync`, `ssh`, `curl`, remote `orderd` commands, queue-worker files and utilities, Oracle `sqlplus`, `settled_payment`, and `gzip`.
- **Billing and reminders:** Maven parent `org.example.billing:billing-parent:2.7.0`; Jackson, Flyway, PostgreSQL, JUnit Jupiter, Testcontainers, optional PDFBox; reminder repository, SMTP gateway, job classes, and `reminder.properties`.
- **XML formats:** Shipment namespace `urn:example:shipment:1`; statement namespace `urn:example:statement`.

#### Cross-cutting
- Runtime configuration comes from CLI options, environment/config files, Maven properties, reminder properties, and stylesheet parameters.
- Log processing defaults to `WARN`, limits input to 50,000 lines, and treats requests over 1,500 ms as slow.
- Failure handling is task-specific: deployment stops on failed health checks, worker health reports distinct status codes, and exports retry before reporting exhaustion.
- Reminder scheduling uses a two-thread scheduler and a 07:00 Monday–Friday cron schedule.
