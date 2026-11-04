### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:08Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of utilities for processing web logs, automating deployment and data extraction, and managing invoicing, shipment, and statement workflows.

#### Purpose
- Summarize HTTP access logs and expose a reusable digest API.
- Automate release deployment, worker health checks, and nightly Oracle data exports.
- Build, schedule, and validate invoicing and shipment XML, and transform statements to HTML.

#### Responsibilities
- **Log Analysis** – parsing, counting, and reporting host/status statistics.
- **Deployment & Ops** – tarball distribution, service restart, health polling, and worker lifecycle control.
- **Data Extraction** – Oracle export to CSV, control file generation, and compression.
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
1. **Log Processing**  
   CLI → `LogDigest` → parse lines → aggregate → `summarize` → output JSON/text.
2. **Deployment**  
   Script → rsync tarball → SSH → extract → symlink → systemctl restart → curl health.
3. **Healthcheck**  
   Start → PID file → worker heartbeat → health command reads age → status report.
4. **Extraction**  
   Script → `sqlplus` query → CSV → row count → control file → gzip.
5. **Scheduling**  
   Spring context → data source → repository → job → scheduler → cron → email reminders.
6. **XML Workflow**  
   External system → shipment XML → validate against `shipment.xsd`.  
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
