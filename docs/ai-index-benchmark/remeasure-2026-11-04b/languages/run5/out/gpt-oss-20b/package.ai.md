### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:11Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A collection of utilities that aggregate web‑server logs, automate deployment and worker management, and support billing, logistics, and reporting workflows.

#### Purpose
- Aggregate and report HTTP access logs by host, status, and latency.  
- Deploy application releases, manage queue workers, and extract payment data.  
- Build, migrate, and configure a billing service; validate shipment XML; transform statements to HTML.

#### Responsibilities
- **Log aggregation** – parse, summarize, and flag slow requests (`LogDigest`).  
- **Deployment & process control** – host‑by‑host release rollout, worker health monitoring (`deploy_release.sh`, `healthcheck.sh`).  
- **Data extraction** – Oracle payment export to CSV (`nightly_extract.ksh`).  
- **Build & migration** – Maven packaging, Flyway schema updates, PDF generation.  
- **Scheduling** – Spring‑based reminder job for overdue invoices.  
- **XML handling** – Shipment notice schema (`shipment.xsd`) and statement‑to‑HTML XSLT (`statement-to-html.xsl`).

#### Key units
- `LogDigest` (Perl module) – parses log lines, aggregates counts, tracks slow requests.  
- `log_digest.pl` – CLI driver for log ingestion and report output.  
- `deploy_release.sh` – orchestrates tarball transfer, extraction, symlink, service restart, health check.  
- `healthcheck.sh` – PID/heartbeat‑based controller for queue workers.  
- `nightly_extract.ksh` – Oracle query driver, CSV spooler, control file generator, gzip compressor.  
- `invoice-service-pom.xml` – Maven project with Flyway, PDFbox profile.  
- `scheduler-context.xml` – Spring beans for reminder data source, repository, mail gateway, job, and cron trigger.  
- `shipment.xsd` – XML schema for shipment notices.  
- `statement-to-html.xsl` – XSLT stylesheet converting statement XML to styled HTML.

#### Data flow
1. **Log processing** – `log_digest.pl` reads files → `LogDigest->add` → `summarize` → formatted output.  
2. **Deployment** – `deploy_release.sh` → rsync tarball → SSH extraction → symlink → `systemctl restart` → `/health` poll.  
3. **Worker control** – `healthcheck.sh` starts `queue-worker`, writes PID, writes heartbeat; health checks read heartbeat age.  
4. **Payment extraction** – `nightly_extract.ksh` runs `sqlplus` query → spools CSV → counts rows → writes control file → gzips CSV.  
5. **Build & migration** – Maven compiles → Flyway migrates → packages JAR → optional PDF generation.  
6. **Scheduling** – Spring context loads beans → scheduled trigger fires → `OverdueReminderJob` sends emails via `SmtpMailGateway`.  
7. **XML validation** – Warehouse emits XML → consumers validate against `shipment.xsd`.  
8. **Statement rendering** – XML statement → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `List::Util`, `Carp`, `Exporter`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **Oracle**: `PAYPRD` database for payment extraction.  
- **Maven**: `org.example.billing:billing-parent`, Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFbox.  
- **Spring**: `task`, `context`, HikariCP, custom reminder components.  
- **XML**: W3C XSD, `urn:example:shipment:1`, `urn:example:statement`.

#### Cross-cutting
- **Logging**: Timestamped stderr logs in shell scripts; `LogDigest` normalizes host names.  
- **Error handling**: Perl uses `croak`/`die`; shell scripts exit with specific codes; Maven/Flyway rely on defaults.  
- **Configuration**: Host lists (`hosts.staging`, `hosts.production`), environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`), and property files (`reminder.properties`).  
- **Retry logic**: `nightly_extract.ksh` retries up to 3 times; deployment scripts log failures but do not retry.  
- **Testing**: Maven test scope includes JUnit and Testcontainers for PostgreSQL.
