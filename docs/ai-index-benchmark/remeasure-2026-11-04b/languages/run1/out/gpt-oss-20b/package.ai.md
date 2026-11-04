### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:48Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that parses web logs, automates deployment and health monitoring for a payment service, and supplies build, scheduling, validation, and rendering assets for an invoicing system.

#### Purpose
- Provide utilities for log analysis, deployment automation, and invoicing configuration.  
- Enable quick reporting, reliable service management, and consistent data validation/rendering.

#### Responsibilities
- **Log Processing** – parse access logs, aggregate host and status statistics, flag slow requests, and output summaries.  
- **Deployment & Ops** – deploy releases, manage queue worker lifecycle, monitor health, and extract nightly payment data from Oracle.  
- **Build & Configuration** – define Maven build, Spring scheduling, XML schema validation, and XSLT rendering for invoices.

#### Key units
- **Perl**  
  - `LogDigest` (module): parses lines, aggregates counts, tracks slow requests.  
  - `log_digest.pl` (script): CLI driver, handles exclusions, outputs text/JSON.  
  - Helper functions: `top_n`, `parse_line`.  
- **Shell**  
  - `deploy_release.sh`: packages, rsyncs, SSH‑deploys, restarts services, polls health.  
  - `healthcheck.sh`: POSIX wrapper for start/stop/restart/status/health of queue worker.  
  - `nightly_extract.ksh`: runs Oracle SQL, writes CSV, creates control file, gzips output.  
- **XML**  
  - `invoice-service-pom.xml`: Maven project, Flyway migrations, PDF profile.  
  - `scheduler-context.xml`: Spring beans for reminder jobs, cron triggers.  
  - `shipment.xsd`: XML Schema for shipment notices.  
  - `statement-to-html.xsl`: XSLT stylesheet converting statements to HTML with highlighting.

#### Data flow
1. **Log Analysis** – `log_digest.pl` → `LogDigest->add` → aggregates → `summarize` → output.  
2. **Deployment** – `deploy_release.sh` → rsync → SSH → extract → symlink → restart `orderd` → curl `/health`.  
3. **Health Check** – `healthcheck.sh` reads PID/heartbeat → reports status/health.  
4. **Extraction** – `nightly_extract.ksh` builds SQL → `sqlplus` → CSV → control file → gzip.  
5. **Build & Scheduling** – Maven compiles → Flyway runs → Spring loads context → reminder job triggers via cron.  
6. **Validation & Rendering** – External XML → validated by `shipment.xsd`; statements → transformed by `statement-to-html.xsl` to HTML.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `List::Util`, `Carp`, `Exporter`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **XML**: Maven (`org.example.billing:billing-parent`, Flyway, Jackson, PostgreSQL, PDFBox), Spring (`task`, `context`, `beans`, HikariCP), XSD/XSLT 1.0.

#### Cross-cutting
- Robust error handling (`set -euo pipefail`, `croak`, explicit exit codes).  
- Configuration via environment variables and property placeholders.  
- Consistent logging with timestamps.  
- Retry logic in nightly extraction; heartbeat age and health poll timeouts in health checks.  
- Parameterized XSLT and default values for XML namespaces and presentation.
