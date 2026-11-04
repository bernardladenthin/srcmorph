### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:12Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that processes web logs, automates deployment, extracts Oracle data, and validates/transforms invoicing and shipment XML.

#### Purpose
- Provide a reusable log‑digest API and CLI for HTTP access logs.
- Automate release deployment, worker health checks, and nightly Oracle exports.
- Build, schedule, and validate invoicing/shipment XML, converting statements to HTML.

#### Responsibilities
- **Log Analysis** – parsing, counting, summarizing host/status stats.
- **Deployment & Ops** – tarball distribution, service restart, health polling, worker lifecycle.
- **Data Extraction** – Oracle export to CSV, control file creation, compression.
- **Build & Scheduling** – Maven packaging, Flyway migrations, Spring job wiring.
- **XML Validation & Transformation** – shipment notice schema, statement XSLT rendering.

#### Key units
- `LogDigest` (Perl module) – `new`, `add`, `summarize`, `top_n`.
- `log_digest.pl` – CLI orchestrator, option parsing, alias handling.
- `deploy_release.sh` – host‑by‑host deployment, rsync/ssh, systemctl restart, health check.
- `healthcheck.sh` – PID/heartbeat worker controller.
- `nightly_extract.ksh` – Oracle `sqlplus` export, CSV generation, control file, gzip.
- `invoice-service-pom.xml` – Maven project, dependencies, Flyway, PDFBox profile.
- `scheduler-context.xml` – Spring beans for reminder job, data source, mail gateway, cron trigger.
- `shipment.xsd` – XML schema for shipment notices.
- `statement-to-html.xsl` – XSLT stylesheet for statement HTML rendering.

#### Data flow
1. **Log Processing** – CLI → `LogDigest` → parse → aggregate → `summarize` → JSON/text output.  
2. **Deployment** – Script → rsync tarball → SSH → extract → symlink → `systemctl restart` → `curl` health.  
3. **Healthcheck** – Start → PID file → worker heartbeat → health command reads age → status report.  
4. **Extraction** – Script → `sqlplus` query → CSV → row count → control file → gzip.  
5. **Scheduling** – Spring context → data source → repository → job → scheduler → cron → email reminders.  
6. **XML Workflow** – External system → shipment XML → validate against `shipment.xsd`.  
   Statement XML → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `cat`, `sqlplus`, `gzip`.  
- **Oracle**: `CONNECT / AS SYSDBA` for nightly export.  
- **Maven**: Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox, HikariCP, Spring `task`/`context`.  
- **XML**: Standard XML Schema namespace; no external imports.

#### Cross-cutting
- Consistent error handling (`set -euo pipefail`, `croak`/`die`).  
- Host normalization (lowercase, strip trailing dot) across log parsing and exclusions.  
- Configuration via environment variables, property placeholders, and Maven properties.  
- Retry logic in nightly extraction (max 3 attempts).  
- Health checks use timeouts (`HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).  
- Scheduler pool size 2; HikariCP max 8 connections.
