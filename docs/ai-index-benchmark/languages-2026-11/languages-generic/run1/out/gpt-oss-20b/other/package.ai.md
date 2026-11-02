### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:32:10Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates deployment and health monitoring of a distributed order‑processing system, and supplies XML configuration, schema, and XSLT resources for billing services.

#### Purpose
- Log aggregation and reporting for web‑server access logs.
- Automated deployment, process management, and data export for an order‑processing platform.
- Centralized XML assets (config, schema, XSLT) for invoice, reminder, shipment, and statement workflows.

#### Responsibilities
- **Log Processing** – parse, aggregate, filter, and output statistics from raw access logs.
- **Deployment & Ops** – orchestrate multi‑host releases, restart services, monitor worker health, and export settled payments.
- **XML Asset Management** – provide Maven build configuration, Spring scheduling, XML validation, and XSLT transformation for billing domain.

#### Key units
- **Perl**  
  - `LogDigest` module: `add`, `summarize`, `top_n`, `parse_line`, `_normalize_host`.  
  - `log_digest.pl`: CLI entry point, alias handling, JSON output flag.  
  - Constants: `MAX_LINES`, `DEFAULT_LEVEL`, `SLOW_REQUEST_MS`, `%LEVEL_RANK`.
- **Shell**  
  - `deploy_release.sh`: rsync tarball, SSH extraction, `systemctl restart orderd`, health polling.  
  - `healthcheck.sh`: PID/heartbeat based lifecycle and status checks.  
  - `nightly_extract.ksh`: Oracle `sqlplus` query, CSV generation, gzip, control file with row count.
- **XML**  
  - `invoice-service-pom.xml`: Maven POM (Java 17, Flyway, PostgreSQL, optional PDFBox).  
  - `scheduler-context.xml`: Spring beans for reminder jobs, data source, mail gateway, cron scheduler.  
  - `shipment.xsd`: Schema for `<shipmentNotice>` documents.  
  - `statement-to-html.xsl`: XSLT to render `<st:statement>` as HTML.

#### Data flow
1. **Log Aggregation** – script reads log files → `LogDigest->add` updates counters → `summarize` produces host/status stats → output (text/JSON).  
2. **Deployment** – `deploy_release.sh` → `rsync` → SSH extraction → symlink `/opt/releases/current` → `systemctl restart orderd` → repeated `curl /health` until success.  
3. **Health Check** – `healthcheck.sh` reads `PID_FILE`; if running, verifies `HEARTBEAT_FILE` age; reports status or exits with code.  
4. **Export** – `nightly_extract.ksh` runs Oracle query → writes CSV → `wc -l` → `.ctl` file → `gzip` compresses CSV.  
5. **Billing XML** – Maven builds invoice service → Flyway applies DB migrations → Spring scheduler triggers `OverdueReminderJob` → XSLT transforms `<st:statement>` to HTML.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `FindBin`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `wc`, `stat`, `date`, `sed`, `grep`, `mapfile`.  
- **XML**: Spring framework, HikariCP, Flyway, PostgreSQL driver, Jackson databind, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox (optional), XSLT 1.0.

#### Cross‑cutting
- Shared constants and configuration across modules (e.g., host alias mapping, JSON flag, environment variables).  
- Unified error handling: `croak` in Perl, `die` in shell scripts, Spring bean creation failures, XML validation errors.  
- Logging conventions: timestamped stderr output in shell, human‑readable summaries in Perl.  
- Retry and timeout logic: health polling loops, nightly export retry counter, optional PDF generation profile.  
- Namespace and default value consistency in XML assets (`urn:example:shipment:1`, `urn:example:statement`, default country DE, currency EUR).
