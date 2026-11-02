### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:41:57Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of utilities and configurations for processing web‑server logs, automating deployment and monitoring, and managing an invoice‑handling service’s build, scheduling, and XML resources.  

#### Purpose
- Log‑analysis tool for web‑server access data.  
- Shell scripts for release deployment, worker lifecycle, and payment export.  
- XML/Maven configuration for an invoice service’s build, scheduling, and report generation.

#### Responsibilities
- **Log processing** – parse Apache‑style logs, aggregate host/status stats, flag slow requests.  
- **Deployment & monitoring** – automate tarball deployment, service restarts, health checks, and queue‑worker control.  
- **Payment export** – run Oracle queries, produce CSVs, compress, and generate control files.  
- **Build & scheduling** – Maven build, Flyway migrations, Spring scheduler for overdue reminders.  
- **XML handling** – validate shipment notices, transform account statements to HTML.

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
