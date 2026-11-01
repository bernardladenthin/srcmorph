### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:41:09Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates release deployment and worker health checks, and supplies XML schemas and XSLT for invoice and shipment processing.

#### Purpose
- Log aggregation and reporting for HTTP access logs.
- Automated deployment, worker lifecycle, and data export for production systems.
- Configuration, schema, and transformation support for invoice, reminder, and shipment services.

#### Responsibilities
- **Log Processing** – parse, normalize, aggregate, and report HTTP access logs (Perl).
- **Deployment & Ops** – orchestrate tarball deployment, service restart, health polling, and queue‑worker management (Shell).
- **Data Export** – query Oracle, generate gzipped CSVs, and produce control files (Shell).
- **Configuration & Build** – Maven POM, Spring context, and dependency wiring for the invoice service (XML).
- **Schema & Transformation** – define XML contracts for shipment notices and account statements, and transform statements to HTML (XML).

#### Key units
- **Perl**  
  - `LogDigest` module – parsing, host normalization, slow‑request detection, aggregation.  
  - `log_digest.pl` – CLI for reading files, applying exclusions, and outputting JSON or formatted text.
- **Shell**  
  - `deploy_release.sh` – remote tarball transfer, extraction, symlink, service restart, health check.  
  - `healthcheck.sh` – PID/heartbeat management and health status reporting.  
  - `nightly_extract.ksh` – Oracle query, CSV generation, gzip, control file creation.
- **XML**  
  - `invoice-service-pom.xml` – Maven build, dependencies, Flyway, PDF export profile.  
  - `scheduler-context.xml` – Spring beans for reminder scheduler, data source, mail gateway, cron trigger.  
  - `shipment.xsd` – XML Schema for `shipmentNotice` documents.  
  - `statement-to-html.xsl` – XSLT stylesheet converting `st:statement` to HTML.

#### Data flow
1. **Log Aggregation** – CLI → `LogDigest` → summary → JSON/text output.  
2. **Deployment** – `deploy_release.sh` → rsync → SSH → service restart → `curl` health check.  
3. **Worker Management** – `healthcheck.sh` writes/reads PID and heartbeat; `health` command compares age to threshold.  
4. **Data Export** – `nightly_extract.ksh` → `sqlplus` query → CSV → gzip → control file.  
5. **Invoice Service** – Maven builds → Flyway migrations → optional PDF export.  
6. **Scheduler** – Spring loads `scheduler-context.xml` → schedules `OverdueReminderJob`.  
7. **Shipment/Statement** – External systems produce XML → validated against `shipment.xsd`; statements transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl** – `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.  
- **Shell** – `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **XML** – Maven (`flyway-core`, `jackson-databind`, `postgresql`, `pdfbox`), Spring (`JdbcTemplate`, `HikariCP`), XSLT 1.0 processor.  
- **External** – Oracle database, PostgreSQL, SMTP server, remote hosts running Bash.

#### Cross‑cutting
- Consistent error handling: `croak`/`die` in Perl, `set -euo pipefail` in Shell, schema validation in XML.  
- Configuration via files (`/etc/log-digest.conf`, host lists) and environment variables (`DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`).  
- Logging and timestamping across scripts.  
- Retry logic in `nightly_extract.ksh` (max 3 attempts).  
- Namespace consistency (`urn:example:shipment:1`, `urn:example:statement`).
