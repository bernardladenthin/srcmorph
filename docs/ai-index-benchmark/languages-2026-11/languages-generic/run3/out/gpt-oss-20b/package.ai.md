### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:42:08Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that parses web‑server logs, automates deployment and monitoring, and supports an invoice‑handling service’s build, scheduling, and XML reporting.

#### Purpose
- Log‑analysis utility for Apache‑style access logs.  
- Shell‑based deployment, health‑check, and payment‑export automation.  
- Maven‑based build, migration, and scheduling for an invoice service, plus XML validation and transformation.

#### Responsibilities
- **Log processing** – parse, aggregate, and report host/status statistics, flag slow requests.  
- **Deployment & monitoring** – automate tarball deployment, service restarts, health checks, and queue‑worker lifecycle.  
- **Payment export** – run Oracle queries, generate CSVs, compress, and create control files.  
- **Build & scheduling** – Maven build, Flyway migrations, Spring scheduler for overdue reminders.  
- **XML handling** – validate shipment notices against XSD, transform account statements to HTML.

#### Key units
- `LogDigest` (Perl class) – line parsing, counters, `top_n` logic.  
- `log_digest.pl` – CLI orchestrator, alias loading, output formatting.  
- `deploy_release.sh` – host deployment, symlink, service restart, health polling.  
- `healthcheck.sh` – queue‑worker start/stop, heartbeat age calculation.  
- `nightly_extract.ksh` – Oracle export, CSV/gzip, control file creation.  
- `invoice-service-pom.xml` – Maven project, dependencies, Flyway config.  
- `scheduler-context.xml` – Spring beans for reminder job and scheduler.  
- `shipment.xsd` – XML schema for shipment notices.  
- `statement-to-html.xsl` – XSLT for account statement HTML.

#### Data flow
1. **Log tool**: CLI → `LogDigest` → aggregated stats → text/JSON report.  
2. **Deployment**: tag/env → host list → rsync → SSH extraction → symlink → systemctl restart → curl health check.  
3. **Health**: script command → worker PID/heartbeat → age → status code.  
4. **Export**: date/dir → `sqlplus` query → CSV → gzip → control file.  
5. **Invoice service**: Maven build → Flyway migrations → JAR.  
6. **Scheduler**: cron trigger → `overdueReminderJob` → DB query → `mailGateway` send.  
7. **XML**: incoming XML → `shipment.xsd` validation → processing.  
8. **Reporting**: statement XML → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `sqlplus`, `gzip`, `date`, `stat`, `cat`.  
- **Java**: Jackson, Flyway, PostgreSQL driver, PDFBox, HikariCP, Spring Framework, JUnit, Testcontainers.  
- **Configuration files**: `/etc/log-digest.conf`, host lists (`hosts.staging`, `hosts.production`), `reminder.properties`, XSD/XSLT resources.

#### Cross-cutting
- Consistent error handling (`croak`, `die`, exit codes).  
- Logging to stderr with timestamps.  
- Use of constants for limits, timeouts, defaults (e.g., `MAX_LINES`, `SLOW_REQUEST_MS`, `graceDays`).  
- Environment‑driven configuration for deployment scripts.  
- XML/XSD defaults and XSLT parameters for reusable templates.
