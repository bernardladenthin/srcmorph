### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:41Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates deployment and queue‑worker management, and supports invoicing workflows through build, scheduling, and XML transformations.  

#### Purpose  
- Consolidate and report access‑log metrics.  
- Automate release deployment, worker health checks, and nightly payment extraction.  
- Build, schedule, and transform invoicing and shipment data.  

#### Responsibilities  
- **Log Analysis (Perl)** – parse, aggregate, and report host/activity statistics.  
- **Deployment & Process Control (Shell)** – orchestrate tarball deployment, worker lifecycle, and Oracle data export.  
- **Build & Scheduling (XML/Maven)** – compile the Invoice Service, run migrations, schedule overdue reminders, and validate/transform XML contracts.  

#### Key units  
- **Perl**  
  - `LogDigest` module – OO log aggregator (`new`, `add`, `summarize`, `top_n`).  
  - `parse_line` – regex parser for host, status, latency.  
  - `log_digest.pl` – CLI driver for file ingestion, alias handling, and output.  
- **Shell**  
  - `deploy_release.sh` – host‑by‑host deployment via `rsync/ssh`, symlink, `systemctl`, health check.  
  - `healthcheck.sh` – PID/heartbeat watchdog for `/usr/local/bin/queue-worker`.  
  - `nightly_extract.ksh` – Oracle `sqlplus` export to CSV/CTL, gzip.  
- **XML/Maven**  
  - `invoice-service-pom.xml` – Maven build, Flyway migrations, PDFBox profile.  
  - `scheduler-context.xml` – Spring beans for overdue‑invoice reminders.  
  - `shipment.xsd` – XML schema for `shipmentNotice`.  
  - `statement-to-html.xsl` – XSLT for rendering account statements.  

#### Data flow  
1. **Log Processing** – CLI reads log files → `LogDigest` aggregates → summary or JSON output.  
2. **Deployment** – Script copies tarball → remote extraction → symlink update → service restart → health check.  
3. **Health Check** – PID/heartbeat files → status report.  
4. **Extraction** – `sqlplus` query → CSV/CTL → gzip.  
5. **Build** – Maven compiles, migrates, packages JAR.  
6. **Scheduling** – Cron triggers `OverdueReminderJob` → email dispatch.  
7. **XML** – Shipment notices validated against `shipment.xsd`; statements transformed by `statement-to-html.xsl`.  

#### Dependencies  
- **Perl**: core modules (`POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`), `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `nohup`, `kill`.  
- **XML/Maven**: Jackson, Flyway, PostgreSQL, JUnit, Testcontainers, PDFBox, HikariCP, Spring task/context.  

#### Cross‑cutting  
- Centralized error handling (`croak`, `die`, strict `set -euo pipefail`).  
- Configurable logging levels and host normalization.  
- Environment‑driven configuration (env vars, config files).  
- Consistent use of XML namespaces and XSLT parameters.
