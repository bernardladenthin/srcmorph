### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:49:00Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit for aggregating web‑server logs, automating deployment and monitoring of a payment platform, and building a Java invoicing service with XML validation and HTML rendering.

#### Purpose
- Aggregate HTTP access logs and produce host‑/status‑level summaries.
- Deploy, monitor, and nightly‑extract data for a distributed payment system.
- Build, schedule, and format invoices and shipment notices in a Java service.

#### Responsibilities
- **Log Processing** – Perl module `LogDigest` and CLI `log_digest.pl` parse and aggregate logs, flag slow requests, and output JSON/text summaries.
- **Deployment & Monitoring** – Shell scripts `deploy_release.sh`, `healthcheck.sh`, and `nightly_extract.ksh` manage tarball distribution, service restarts, worker lifecycle, health polling, and Oracle data export.
- **Invoice Service Build & Runtime** – Maven POM compiles the `invoice-service` JAR, runs Flyway migrations; Spring context schedules overdue reminders; XML schema validates shipment notices; XSLT transforms statements to styled HTML.

#### Key units
- `LogDigest` (Perl) – `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- `log_digest.pl` – CLI entry point; options `--top`, `--level`, `--json`, `--exclude-host`.
- `deploy_release.sh` – Orchestrates rsync, SSH extraction, symlink, `systemctl restart`, health polling.
- `healthcheck.sh` – PID/heartbeat worker controller (`start_worker`, `stop_worker`, `is_running`, `heartbeat_age`).
- `nightly_extract.ksh` – Oracle export driver (`run_export`, `write_control`, retry loop, gzip).
- `invoice-service-pom.xml` – Maven project, Flyway migration path, Java 17.
- `scheduler-context.xml` – Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`; scheduler `reminderScheduler`.
- `shipment.xsd` – XML Schema for `shipmentNotice`.
- `statement-to-html.xsl` – XSLT stylesheet for `st:statement` → styled HTML.

#### Data flow
1. **Log Aggregation** – CLI → `LogDigest` → per‑host/status totals → JSON/text output.
2. **Deployment** – `deploy_release.sh` → rsync tarball → SSH extraction → symlink update → `systemctl restart` → HTTP health check.
3. **Health Monitoring** – `healthcheck.sh` reads PID/heartbeat → reports status/health.
4. **Nightly Extraction** – `nightly_extract.ksh` runs SQL query → CSV + control file → gzip.
5. **Invoice Service** – Maven builds JAR → Flyway migrates DB → Spring scheduler triggers `OverdueReminderJob` → mail gateway sends reminders.
6. **Shipment/Statement Processing** – External XML → validated by `shipment.xsd`; service receives `st:statement` → XSLT renders HTML.

#### Dependencies
- **Perl** – `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`, `Exporter`.
- **Shell** – `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`; environment vars `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`.
- **Java** – Maven, Flyway Core, Jackson Databind, PostgreSQL JDBC, PDFBox (profile), Spring Core/Context/Scheduler, JUnit Jupiter, Testcontainers PostgreSQL.

#### Cross-cutting
- Consistent error handling (`croak`/`die`, `set -euo pipefail`).
- Configuration via files (`/etc/log-digest.conf`, `hosts.*`, `reminder.properties`) and environment variables.
- Host normalization in `LogDigest`; heartbeat file timestamps for worker health.
- Scheduler pool size 2 for concurrent job execution; XML namespaces and XSLT parameters for styling.
