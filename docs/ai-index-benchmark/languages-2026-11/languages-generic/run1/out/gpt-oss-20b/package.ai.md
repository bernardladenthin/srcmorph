### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:32:18Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that aggregates web‑server logs, automates deployment and health monitoring of a distributed order‑processing system, and supplies XML assets for billing workflows.

#### Purpose
- Aggregate, filter, and report web‑server access logs.
- Orchestrate multi‑host releases, restart services, monitor worker health, and export settled payments.
- Provide centralized XML configuration, schema, and XSLT for invoice, reminder, shipment, and statement processes.

#### Responsibilities
- **Log Processing** – parse, aggregate, filter, and output statistics from raw access logs.
- **Deployment & Ops** – orchestrate releases, restart services, monitor worker health, and export settled payments.
- **XML Asset Management** – supply Maven build configuration, Spring scheduling, XML validation, and XSLT transformation for billing domain.

#### Key units
- **Perl**  
  - `LogDigest` module: `add`, `summarize`, `top_n`, `parse_line`, `_normalize_host`.  
  - `log_digest.pl`: CLI entry point, alias handling, JSON output flag.  
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
1. **Log Aggregation** – scripts read log files → `LogDigest->add` updates counters → `summarize` produces host/status stats → output (text/JSON).  
2. **Deployment** – `deploy_release.sh` → `rsync` → SSH extraction → symlink `/opt/releases/current` → `systemctl restart orderd` → repeated `curl /health` until success.  
3. **Health Check** – `healthcheck.sh` reads `PID_FILE`; if running, verifies `HEARTBEAT_FILE` age; reports status or exits with code.  
4. **Export** – `nightly_extract.ksh` runs Oracle query → writes CSV → `wc -l` → `.ctl` file → `gzip` compresses CSV.  
5. **Billing XML** – Maven builds invoice service → Flyway applies DB migrations → Spring scheduler triggers `OverdueReminderJob` → XSLT transforms `<st:statement>` to HTML.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `FindBin`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `wc`, `stat`, `date`, `sed`, `grep`, `mapfile`.  
- **XML**: Spring framework, HikariCP, Flyway, PostgreSQL driver, Jackson databind, JUnit Jupiter, Testcontainers PostgreSQL, Apache PDFBox (optional), XSLT 1.0.

#### Cross‑cutting
- Shared constants and configuration (host alias mapping, JSON flag, environment variables).  
- Unified error handling (`croak` in Perl, `die` in shell, Spring bean failures, XML validation errors).  
- Logging conventions: timestamped stderr in shell, human‑readable summaries in Perl.  
- Retry and timeout logic: health polling loops, nightly export retry counter, optional PDF generation profile.  
- Namespace and default value consistency in XML assets (`urn:example:shipment:1`, `urn:example:statement`, default country DE, currency EUR).
