### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:48:02Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that aggregates web‑server logs, automates release deployment, and provides XML/XSLT utilities for a billing system.

#### Purpose
- Log aggregation & reporting for HTTP access logs.  
- Deployment of release tarballs and queue‑worker lifecycle/health management.  
- Build, scheduling, and XML/XSLT utilities for invoicing and shipment processing.

#### Responsibilities
- **Log processing** – parse, aggregate, and report host and status statistics.  
- **Deployment orchestration** – copy, extract, symlink releases, restart services, and verify health.  
- **Service control** – start/stop/restart queue workers, track PID and heartbeat.  
- **Build & packaging** – Maven build for invoice service with optional PDF export.  
- **Scheduling** – Spring job to send overdue reminders on weekdays.  
- **XML validation & presentation** – XSD for shipment notices; XSLT to render statements.

#### Key units
- `LogDigest` (Perl module): parses log lines, normalizes hosts, counts requests, tracks slow requests, tallies status codes, exposes `top_n`.  
- `log_digest.pl` (Perl script): CLI entry point, option parsing, host‑alias handling, output formatting (text/JSON).  
- `deploy_release.sh` (Bash): deploys tarball, restarts `orderd`, polls `/health`.  
- `healthcheck.sh` (Bash): manages queue‑worker PID, launches with `nohup`, checks heartbeat age.  
- `invoice-service-pom.xml` (Maven): dependencies, compiler, Flyway, PDF export profile.  
- `scheduler-context.xml` (Spring): beans for reminder job, data source, mail gateway.  
- `shipment.xsd` (XML Schema): defines `shipmentNotice` structure and constraints.  
- `statement-to-html.xsl` (XSLT): transforms `<st:statement>` into HTML with totals and highlights.

#### Data flow
1. **Log aggregation** – CLI → `LogDigest` → per‑host/status counts → summary output (text/JSON).  
2. **Deployment** – `deploy_release.sh` → rsync tarball → SSH extraction → symlink update → restart `orderd` → health poll.  
3. **Service control** – `healthcheck.sh` → PID file manipulation → `queue-worker` launch → heartbeat check → health status.  
4. **Build & scheduling** – Maven → compile, Flyway, package → Spring context → scheduler triggers reminder job.  
5. **XML/XSLT** – External system → `shipmentNotice` XML → validates against `shipment.xsd`; `<st:statement>` → `statement-to-html.xsl` → HTML report.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, `JSON::PP`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `kill`, `date`, `stat`.  
- **Maven**: Jackson, Flyway, PostgreSQL driver, JUnit, Testcontainers, PDFBox (profile).  
- **Spring**: context, task scheduler, JDBC.  
- **Database**: PostgreSQL via HikariDataSource.  
- **Mail**: SMTP via `SmtpMailGateway`.  
- **XML**: XSD, XSLT 1.0.

#### Cross-cutting
- Host normalization (lowercase, strip trailing dot) in log parsing.  
- Constants: `MAX_LINES`, `SLOW_REQUEST_MS`, default log level.  
- Error handling: `croak` in `LogDigest`, `die` with usage in scripts.  
- Configuration via environment variables (`DEPLOY_USER`, `SSH_KEY`, `RELEASE_ROOT`, etc.) and optional host‑alias file.  
- Logging: timestamped stderr messages in deployment scripts.  
- Idempotent operations: symlink updates, health polling, and repeatable deployments.
