### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:56:43Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates release deployment and worker management, and supplies XML‑based configuration for an invoicing service and related utilities.

#### Purpose
- Provide log‑analysis tooling for web servers.
- Automate deployment, worker lifecycle, and nightly data exports.
- Supply build, scheduling, validation, and presentation artifacts for an invoicing system.

#### Responsibilities
- **Log Aggregation** – Perl module `LogDigest` parses access logs, tallies hosts, status codes, and slow requests; CLI script `log_digest.pl` orchestrates file input and output.
- **Deployment & Process Control** – Shell scripts `deploy_release.sh`, `healthcheck.sh`, and `nightly_extract.ksh` manage tarball distribution, queue‑worker lifecycle, and Oracle payment exports.
- **Invoice Service Build & Scheduling** – Maven POM, Spring XML context, and XSLT/XSD files configure the Java service, schedule overdue‑invoice reminders, validate shipment notices, and render statements.

#### Key units
- **Perl**  
  - `LogDigest` (module) – `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `DEFAULT_LEVEL`, `SLOW_REQUEST_MS`.  
  - `log_digest.pl` – CLI entry point, option parsing, host‑alias handling, JSON/plain‑text output.

- **Shell**  
  - `deploy_release.sh` – `rsync`/`ssh` tarball transfer, extraction, symlink, `systemctl restart`, health poll.  
  - `healthcheck.sh` – POSIX init interface (`start|stop|restart|status|health`) for queue worker, PID/heartbeat tracking.  
  - `nightly_extract.ksh` – `sqlplus` query, CSV/CTL generation, `gzip` compression.

- **XML / Maven**  
  - `invoice-service-pom.xml` – Maven project, Flyway migrations, PDF profile.  
  - `scheduler-context.xml` – Spring beans (`reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`), cron `0 0 7 * * MON-FRI`.  
  - `shipment.xsd` – Schema for `<shipmentNotice>` (carrier, parcel, address).  
  - `statement-to-html.xsl` – XSLT rendering `<st:statement>` to HTML, highlighting amounts > 1000.

#### Data flow
1. **Log Analysis** – CLI → options → host‑alias → `LogDigest` → summary → plain/JSON output.  
2. **Deployment** – Script → host list → `rsync` tarball → SSH extraction → symlink → `systemctl restart` → health check via `curl`.  
3. **Worker Management** – `healthcheck.sh` writes PID, worker writes heartbeat; `health` verifies PID and heartbeat age.  
4. **Export** – `nightly_extract.ksh` → `sqlplus` → CSV → `.ctl` → `gzip`.  
5. **Invoice Service** – Maven build → Flyway migrations → optional PDF generation.  
6. **Reminder Scheduler** – Spring context loads beans → job runs at 7 AM weekdays → queries DB → sends emails.  
7. **Shipment Validation** – Warehouse emits XML → validated against `shipment.xsd`.  
8. **Statement Rendering** – XML → XSLT → HTML.

#### Dependencies
- **Perl** – Core (`POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`, `Getopt::Long`), CPAN (`JSON::PP`).  
- **Shell** – External commands: `rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.  
- **Java/Maven** – HikariCP, Flyway 11.4.0, PostgreSQL driver, JUnit, Testcontainers, PDFBox 3.0.5, Spring beans/task schemas, Jackson 2.19.2.  
- **XML** – Namespaces `urn:example:shipment:1`, `urn:example:statement`.  
- **Data** – Host lists (`hosts.staging`, `hosts.production`), release tarballs (`build/*.tar.gz`), Oracle SID, SMTP settings (`reminder.properties`).

#### Cross‑cutting
- Consistent error handling: Perl uses `croak`/`die`; shell scripts use `set -euo pipefail` and explicit checks.  
- Configuration via environment variables (`LOG_DIGEST_CONFIG`, `DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`, `NLS_LANG`).  
- Logging pattern (`log` function) shared across shell utilities.  
- Host normalization in Perl (`_normalize_host`) ensures uniform counting.  
- Scheduler pool size set to 2; job execution concurrent with Spring task executor.  
- JSON output uses canonical ordering for reproducibility.
