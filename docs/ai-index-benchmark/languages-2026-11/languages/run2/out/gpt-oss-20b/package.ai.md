### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:35:44Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that aggregates web‑server logs, manages deployment and background workers, and builds and schedules an invoice‑and‑shipment service.

#### Purpose
- Log aggregation and reporting for HTTP access logs.  
- Deployment, worker lifecycle, and nightly payment export utilities.  
- Build, scheduling, validation, and presentation for an invoice‑and‑shipment service.

#### Responsibilities
- **Log processing** – parse access logs, aggregate per‑host and status statistics, expose CLI for reporting.  
- **Deployment & process control** – orchestrate release deployment, manage a queue worker, export settled payments.  
- **Service configuration & data handling** – Maven build, Spring scheduler, XML schema validation, XSLT rendering.

#### Key units
- `LogDigest` (Perl module) – parses lines, normalizes hosts, counts requests, tracks slow requests, tallies status codes.  
- `log_digest.pl` – CLI for top hosts, status distribution, JSON output, host exclusions.  
- `deploy_release.sh` – host‑by‑host tarball transfer, extraction, symlink, service restart, health check.  
- `healthcheck.sh` – start/stop/restart/status/health commands for `/usr/local/bin/queue-worker`.  
- `nightly_extract.ksh` – Oracle `sqlplus` export of settled payments, CSV/CTL generation, gzip with retry.  
- `invoice-service-pom.xml` – Maven project definition, dependencies, Flyway, PDF profile.  
- `scheduler-context.xml` – Spring data source, repository, mail gateway, overdue reminder job, scheduler.  
- `shipment.xsd` – validates `shipmentNotice` structure.  
- `statement-to-html.xsl` – transforms statement XML into styled HTML.

#### Data flow
1. **Log aggregation** – CLI → `LogDigest->add` per line → `summarize` → formatted output (text/JSON).  
2. **Deployment** – `deploy_release.sh` → rsync tarball → SSH extraction → symlink `current` → systemctl restart → curl health until success.  
3. **Worker control** – `healthcheck.sh` manipulates PID/heartbeat files; health command compares heartbeat age to `MAX_HEARTBEAT_AGE`.  
4. **Payment export** – `nightly_extract.ksh` → `sqlplus` spool → CSV/CTL → gzip; retry on failure.  
5. **Service build & run** – Maven → compile, Flyway, package → optional PDF export.  
   Spring context → scheduler triggers `OverdueReminderJob` via cron.  
   External XML → validated by `shipment.xsd`.  
   Statement XML → `statement-to-html.xsl` → HTML report.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `FindBin`, `POSIX`, `List::Util`, `Carp`.  
- **Shell**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`; shared `lib/common.sh`; host lists (`hosts.staging`, `hosts.production`).  
- **Maven**: `jackson-databind`, `flyway-core`, `postgresql`, `junit-jupiter`, `testcontainers:postgresql`, `pdfbox`.  
- **Spring**: HikariCP, custom `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`; scheduler pool size 2.  
- **XML**: XSD namespace `urn:example:shipment:1`; XSLT namespace `urn:example:statement`.

#### Cross‑cutting
- Strict mode (`set -euo pipefail`) in deployment; POSIX sh in healthcheck; consistent logging with timestamps.  
- Error handling via `Carp::croak` (Perl) and `die/fail` functions (Shell) with exit codes 1–4.  
- Host normalization in `LogDigest`; configurable log level and max lines (`MAX_LINES`).  
- JSON output uses canonical ordering; XSLT applies conditional classes for highlighting large bookings.  
- Retry logic in nightly export (max 3 attempts, 60 s sleep).
