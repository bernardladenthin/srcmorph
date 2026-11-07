### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:31Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that parses web logs, automates deployment and monitoring of a payment system, and builds an invoice‑processing service with XML contracts and scheduled reminders.

#### Purpose
- Aggregate web‑server access‑log metrics.
- Deploy, monitor, and nightly‑extract data for a payment service.
- Build, configure, and run an invoice‑processing Java service with XML exchange and reminder scheduling.

#### Responsibilities
- **Log Analysis (Perl)** – parse access logs, aggregate statistics, detect slow requests, output summaries or JSON.
- **Deployment & Monitoring (Shell)** – distribute releases, restart services, health‑check queue workers, export settled payments from Oracle.
- **Build & Runtime (XML/Java)** – Maven build with Flyway migrations, optional PDF generation, Spring scheduler for overdue reminders, XML schemas for shipment notices and statements, XSLT for HTML reports.

#### Key units
- **Perl**  
  - `LogDigest` (OO module): `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.  
  - `log_digest.pl`: CLI entry point, option parsing, alias handling, output formatting.
- **Shell**  
  - `deploy_release.sh`: host‑by‑host tarball deployment, symlink update, service restart, health polling.  
  - `healthcheck.sh`: PID/heartbeat manager for `/usr/local/bin/queue-worker`.  
  - `nightly_extract.ksh`: Oracle `sqlplus` export, CSV/GZ creation, control file generation.
- **XML/Java**  
  - `invoice-service-pom.xml`: Maven project, Flyway, PDFBox profile.  
  - `scheduler-context.xml`: Spring beans (`reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`).  
  - `shipment.xsd`: `shipmentNotice` schema with `ParcelType`, `AddressType`.  
  - `statement-to-html.xsl`: XSLT for `st:statement` → styled HTML.

#### Data flow
1. **Log Analysis** – `log_digest.pl` reads log files → `LogDigest::add` → `summarize` → output.  
2. **Deployment** – `deploy_release.sh` rsyncs → SSH → extraction → symlink → restart → health check.  
3. **Healthcheck** – `healthcheck.sh` writes PID, monitors heartbeat, reports status.  
4. **Extraction** – `nightly_extract.ksh` runs SQL, spools CSV, counts rows, writes control file, gzips.  
5. **Invoice Service** – Maven builds JAR → Flyway migrates DB → Spring loads `scheduler-context.xml` → `overdueReminderJob` queries DB, formats emails, sends via `SmtpMailGateway`.  
6. **XML Exchange** – External systems send `shipmentNotice` XML validated against `shipment.xsd`; accounting systems produce `st:statement` XML transformed by `statement-to-html.xsl` into HTML reports.

#### Dependencies
- **Perl** – `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, `Exporter`.  
- **Shell** – `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`; host lists (`hosts.staging`, `hosts.production`); common shell library (`lib/common.sh`).  
- **Java** – HikariCP, PostgreSQL JDBC, Flyway, Jackson Databind, JUnit/Testcontainers, PDFBox (profile), Spring `task`/`context`.  
- **XML** – Standard XML Schema namespace; XSLT processor.

#### Cross‑cutting
- Consistent error handling (`Carp::croak`, `die` with exit codes).  
- Configuration via environment variables or property placeholders (`LOG_DIGEST_CONFIG`, `DEPLOY_USER`, `HEARTBEAT_FILE`, `reminder.*`).  
- Logging with timestamps across scripts.  
- Retry and timeout logic in deployment and extraction.  
- Canonical JSON output for deterministic ordering.  
- XML schemas enforce strict contracts; XSLT parameters allow runtime customization.
