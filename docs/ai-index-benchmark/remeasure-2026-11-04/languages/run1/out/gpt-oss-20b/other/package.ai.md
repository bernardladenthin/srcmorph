### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:21Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A toolkit that aggregates web‑server logs, deploys application releases, and supports invoicing workflows through build, scheduling, and XML processing.

#### Purpose
- Log aggregation and reporting for web servers.
- Automated deployment and process management for a queue worker.
- Build, scheduling, and XML‑based invoicing and reminder services.

#### Responsibilities
- **Log Processing** – parse access logs, aggregate host and status statistics, identify slow requests, and output summaries or JSON.
- **Deployment & Process Control** – orchestrate tarball deployment, service restarts, health checks, and manage a long‑running queue worker.
- **Invoicing Infrastructure** – Maven build, scheduled reminder jobs, XML schema validation, and XSLT rendering of statements.

#### Key units
- **Perl**  
  - `LogDigest` (class): `add`, `summarize`, `top_n`, `parse_line`.  
  - `log_digest.pl` (script): `usage`, `read_aliases`, `main`.
- **Shell**  
  - `deploy_release.sh` – host‑by‑host deployment, health verification, service restart.  
  - `healthcheck.sh` – PID/heartbeat management for queue worker.  
  - `nightly_extract.ksh` – Oracle export to CSV/CTL, gzip, retry logic.
- **XML**  
  - `invoice-service-pom.xml` – Maven project, Flyway, PDF profile.  
  - `scheduler-context.xml` – Spring beans for overdue‑reminder job.  
  - `shipment.xsd` – XML Schema for shipment notices.  
  - `statement-to-html.xsl` – XSLT for statement HTML rendering.

#### Data flow
1. **Log Toolkit** – CLI supplies log files → `log_digest.pl` reads config → `LogDigest` aggregates → summary/JSON output.  
2. **Deployment** – `deploy_release.sh` rsyncs tarball → SSH extraction → symlink update → service restart → health check.  
3. **Worker** – `healthcheck.sh` starts `/usr/local/bin/queue-worker`, writes PID/heartbeat → healthcheck monitors age.  
4. **Export** – `nightly_extract.ksh` runs Oracle query → CSV/CTL → gzip → archive.  
5. **Invoicing** – Maven builds JAR → Spring loads `scheduler-context.xml` → `overdueReminderJob` queries DB → emails sent.  
6. **XML** – Shipment notices validated against `shipment.xsd`; monthly statements transformed by `statement-to-html.xsl` into HTML.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **XML**: Maven (`jackson-databind`, `flyway-core`, `postgresql`, `pdfbox`), Spring (`context`, `task`), HikariCP, XSLT processor.  
- **External**: Oracle database (`/ AS SYSDBA`), host lists (`hosts.staging`, `hosts.production`).

#### Cross-cutting
- Configuration via environment variables and external files (`/etc/log-digest.conf`, `reminder.properties`).  
- Consistent error handling: `set -euo pipefail` in shell, `croak` in Perl.  
- Logging helper (`log`) used across scripts.  
- Retry logic with exponential back‑off in `nightly_extract.ksh`.  
- Host normalization (lowercase, strip trailing dots) in `LogDigest`.  
- Scheduler cron: `0 0 7 * * MON-FRI`; pool size 2.  
- XML schema defaults: country `DE`, carrier enumeration.  
- XSLT parameters (`currencySymbol`, `highlightAbove`) allow runtime overrides.
