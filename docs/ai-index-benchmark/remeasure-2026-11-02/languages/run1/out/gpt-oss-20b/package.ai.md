### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:26:10Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that aggregates web‑server logs, automates deployment and background jobs, and validates and renders invoice‑related XML data.

#### Purpose
- Aggregate and report HTTP access logs.
- Automate release deployment, worker lifecycle, and nightly payment extraction.
- Build, schedule, and validate invoice‑generation and statement‑rendering workflows.

#### Responsibilities
- **Log analytics** – parsing, counting, and summarizing access logs with a CLI.
- **Deployment & process control** – shell scripts for rolling releases, queue‑worker health, and Oracle payment exports.
- **Invoice infrastructure** – Maven build, Spring scheduling, XML schema validation, and XSLT presentation.

#### Key units
- `LogDigest` (Perl module) – parses log lines, tracks host/status counts, flags slow requests.  
- `log_digest.pl` – CLI driver for log reporting.  
- `deploy_release.sh` – host‑by‑host tarball deployment, service restart, health check.  
- `healthcheck.sh` – PID/heartbeat‑based worker controller.  
- `nightly_extract.ksh` – Oracle export to CSV/CTL, gzip.  
- `invoice-service-pom.xml` – Maven project with Flyway migrations and PDF profile.  
- `scheduler-context.xml` – Spring beans for overdue‑invoice reminders.  
- `shipment.xsd` – XML schema for shipment notices.  
- `statement-to-html.xsl` – XSLT for rendering statements to HTML.

#### Data flow
1. **Log processing** – CLI → `LogDigest::add` (parse & count) → `summarize` → formatted output.  
2. **Deployment** – local tarball → `rsync` → remote extraction → symlink update → `systemctl restart` → `curl` health check.  
3. **Worker control** – `healthcheck.sh` writes/reads PID and heartbeat files to start/stop/status the queue worker.  
4. **Payment export** – `nightly_extract.ksh` runs SQL*Plus query → CSV + control file → gzip.  
5. **Invoice build** – Maven compiles, runs Flyway, packages JAR; optional PDFBox added via profile.  
6. **Reminder scheduling** – Spring context loads data source → repository → job → scheduler triggers email job.  
7. **Shipment validation** – external XML → `shipment.xsd` validation before processing.  
8. **Statement rendering** – `<st:statement>` XML → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.  
- **Shell**: `rsync`, `ssh`, `curl`, `sqlplus`, `gzip`, `systemctl`, `nohup`, `kill`, `date`, `stat`.  
- **Java/Maven**: Flyway, PostgreSQL JDBC, Jackson, PDFBox (profile), JUnit, Testcontainers.  
- **Spring**: HikariCP, Task Scheduler, custom reminder beans.  
- **XML**: XSD for shipment notices, XSLT for statements.

#### Cross-cutting
- Consistent error handling (`croak` in Perl, `set -euo pipefail` in shell).  
- Host normalization (lowercase, strip trailing dot) across log parsing and CLI.  
- Configuration via environment variables (`DEPLOY_USER`, `PID_FILE`, `ORACLE_SID`, `LOG_DIGEST_CONFIG`).  
- Retry and timeout logic in deployment and nightly export scripts.  
- Property placeholders (`${...}`) in Maven and Spring configurations.  
- Default values in XSD (`country="DE"`) and XSLT (`currencySymbol="EUR"`, `highlightAbove=1000`).
