### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:36:38Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of Perl, shell, and XML utilities that support log analysis, deployment, health monitoring, data export, and invoicing for a payment processing service.

#### Purpose
- Deliver end‑to‑end tooling for monitoring, deploying, and reporting on a payment processing platform.

#### Responsibilities
- **Log Analysis** – parse web‑server logs, aggregate host and status statistics, and produce text or JSON summaries.  
- **Deployment** – automate tarball distribution, extraction, symlink updates, service restarts, and health checks across environments.  
- **Process Management** – start, stop, and monitor a queue worker with heartbeat validation.  
- **Data Export** – nightly extraction of settled payments from Oracle into gzipped CSV with control files.  
- **Build & Packaging** – Maven project for the Invoice Service, including PDF export capability.  
- **Scheduling** – Spring XML configuration for overdue invoice reminders.  
- **XML Schema & Validation** – define shipment notice structure and enforce constraints.  
- **Presentation** – XSLT transformation of monthly account statements to HTML with highlighting.

#### Key units
- `LogDigest` (Perl module) – parses log lines, aggregates counts, tracks slow requests.  
- `log_digest.pl` (Perl script) – CLI driver that consumes `LogDigest` and formats output.  
- `deploy_release.sh` (shell script) – orchestrates host‑by‑host deployment via `rsync`, `ssh`, and `curl`.  
- `healthcheck.sh` (shell script) – POSIX init‑style interface for queue worker lifecycle and health.  
- `nightly_extract.ksh` (shell script) – Oracle `sqlplus` query, CSV/CTL generation, and gzip.  
- `invoice-service-pom.xml` (Maven POM) – defines project, dependencies, and PDF export profile.  
- `scheduler-context.xml` (Spring XML) – wires data source, repository, mail gateway, job, and cron trigger.  
- `shipment.xsd` (XML Schema) – `ShipmentNoticeType`, `ParcelType`, `AddressType`, and carrier constraints.  
- `statement-to-html.xsl` (XSLT) – templates for rendering statements with currency and highlight parameters.

#### Data flow
1. **Log Analysis** – CLI parses options → `LogDigest` instance → each log line added → summary generated → output as JSON or text.  
2. **Deployment** – host list → `rsync` tarball → `ssh` extraction → symlink update → `systemctl` restart → `curl` health check.  
3. **Health** – PID and heartbeat files checked → status reported.  
4. **Export** – `sqlplus` query → CSV & CTL files → gzip.  
5. **Build** – Maven compiles, tests, runs Flyway, packages JAR, optionally adds PDFBox.  
6. **Scheduling** – cron trigger → `overdueReminderJob.sendDueReminders()` → email via `mailGateway`.  
7. **Shipment** – XML emitted → validated against `shipment.xsd`.  
8. **Statement Rendering** – XSLT consumes `<st:statement>` XML → outputs HTML with highlighted rows.

#### Dependencies
- **Perl** – core modules (`POSIX`, `List::Util`, `Carp`), CPAN (`Getopt::Long`, `JSON::PP`, `FindBin`).  
- **Shell** – external utilities (`rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`).  
- **Oracle** – SID `PAYPRD` for nightly export.  
- **Maven** – compiler, Jackson, Flyway, PostgreSQL, PDFBox, HikariCP, JUnit, Testcontainers.  
- **Spring** – component scanning, data source, repository, mail gateway.  
- **XML** – namespaces `urn:example:shipment:1`, `urn:example:statement`.  
- **Environment** – SSH key `$SSH_KEY`, host list files, service name `orderd`, queue worker binary `/usr/local/bin/queue-worker`.

#### Cross-cutting
- **Error handling** – `Carp::croak` in Perl, `set -euo pipefail` in shell scripts, explicit checks in health scripts.  
- **Configuration** – host‑alias mapping file, environment variables (`$SSH_KEY`, `PAYPRD`), property placeholders (`${...}`).  
- **Constants** – `MAX_LINES = 50000`, `SLOW_REQUEST_MS = 1500`, default `top = 10`, default `level = 'WARN'`.  
- **Logging** – timestamped logs via `log` functions in shell scripts.  
- **Retry logic** – nightly export `MAX_RETRIES=3`.  
- **Health checks** – HTTP (`curl`) or heartbeat file age (`stat`).  
- **Process monitoring** – PID and heartbeat files standardize worker status.
