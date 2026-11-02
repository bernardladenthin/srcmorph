### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:42:30Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A unified toolkit that digests web‑server logs, automates deployment and worker health, and processes XML‑based invoices and shipment notices.

#### Purpose
- Reusable log‑digesting logic and CLI for web‑server metrics.  
- Automated release deployment, queue worker control, and nightly payment extraction.  
- Build, schedule, and validate XML contracts for an invoice service and shipment notices.

#### Responsibilities
- **Log Processing** – parse access logs, aggregate stats, identify slow requests, expose top hosts.  
- **Deployment & Process Control** – tarball distribution, service restarts, worker health monitoring, nightly payment export.  
- **Invoice & Shipment Management** – build invoice service, schedule overdue reminders, validate shipment XML, render statements to HTML.

#### Key units
- **Perl**  
  - `LogDigest` module (`new`, `add`, `summarize`, `top_n`; exports `parse_line`).  
  - `log_digest.pl` CLI orchestrator (option parsing, host‑alias loading, JSON/plain output).  
- **Shell**  
  - `deploy_release.sh` – host‑by‑host tarball deployment, symlink, restart, health check.  
  - `healthcheck.sh` – start/stop/restart/status/health for queue worker, heartbeat monitoring.  
  - `nightly_extract.ksh` – Oracle `sqlplus` export of `settled_payment`, control file creation, gzip with retry.  
- **XML**  
  - `invoice-service-pom.xml` – Maven build, Flyway migrations, PDFBox profile.  
  - `scheduler-context.xml` – Spring beans for reminder job, data source, mail gateway, cron schedule.  
  - `shipment.xsd` – XML schema for `shipmentNotice` (carrier, weight, address constraints).  
  - `statement-to-html.xsl` – XSLT to transform `<st:statement>` into styled HTML.

#### Data flow
1. **Log Analysis** – CLI → `LogDigest` → aggregated metrics → formatted output.  
2. **Deployment** – script reads host list → rsync tarball → SSH extraction → symlink → service restart → health poll.  
3. **Healthcheck** – command dispatch → worker start/stop → PID/heartbeat files → status report.  
4. **Nightly Extract** – date parsing → Oracle query → CSV + control file → gzip.  
5. **Invoice Service** – Maven build → JAR → Flyway migrations → optional PDF generation.  
6. **Reminder Scheduler** – cron triggers job → repository queries overdue records → mail gateway sends emails.  
7. **Shipment Notice** – XML emitted → validated against `shipment.xsd`.  
8. **Statement Rendering** – `<st:statement>` XML → XSLT → HTML with totals and highlights.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **XML**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox, Spring Task Scheduling.  
- **External Resources**: `/etc/log-digest.conf`, Oracle `settled_payment` table, SMTP server, PostgreSQL database, `build/<tag>.tar.gz`, host files (`hosts.staging`, `hosts.production`).

#### Cross‑cutting
- Consistent error handling (`croak`, `die`, `fail`) and exit codes.  
- Timestamped logging across scripts.  
- Configuration via files and environment variables (`LOG_DIGEST_CONFIG`, `DEPLOY_USER`, `ORACLE_SID`).  
- Retry logic in nightly extract; dry‑run support in deployment.  
- Property placeholders (`${...}`) in Maven/Spring configs.  
- XML schema uses `xs:positiveInteger` and enumerations for data integrity.  
- XSLT 1.0 functions for number formatting and conditional styling.
