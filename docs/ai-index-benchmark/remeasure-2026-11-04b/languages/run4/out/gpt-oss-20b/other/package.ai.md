### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:08Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of tooling and infrastructure for processing web‑server logs, deploying and monitoring a payment service, and managing invoicing, reminders, and statement rendering in a billing system.

#### Purpose
- Log analytics and reporting for web‑server access logs.
- Automated deployment, health monitoring, and nightly data extraction for a payment processing service.
- Build, schedule, validate, and present billing documents (invoices, reminders, shipments, statements).

#### Responsibilities
- **Log processing** – parse, aggregate, and report access‑log statistics.
- **Deployment & monitoring** – distribute releases, manage worker processes, and export settled payments.
- **Billing infrastructure** – build the invoice service, schedule overdue reminders, validate shipment notices, and transform statements to HTML.

#### Key units
- `LogDigest` (Perl module) – parses log lines, aggregates host/status counts, tracks slow requests.
- `log_digest.pl` (Perl script) – CLI driver for log aggregation, host filtering, and JSON/text output.
- `deploy_release.sh` (Shell script) – orchestrates tarball distribution, extraction, service restart, and health checks.
- `healthcheck.sh` (Shell script) – POSIX init‑style interface for a queue‑worker, managing PID, heartbeat, and status.
- `nightly_extract.ksh` (Korn shell script) – extracts settled payments from Oracle, writes CSV/CTL, gzips output, retries on failure.
- `invoice-service-pom.xml` (Maven POM) – defines the Invoice Service build, dependencies, Flyway migrations, and optional PDF support.
- `scheduler-context.xml` (Spring XML) – configures the overdue‑reminder scheduler, JDBC repository, and mail gateway.
- `shipment.xsd` (XSD) – schema for `<shipmentNotice>` documents.
- `statement-to-html.xsl` (XSLT) – transforms statement XML into styled HTML.

#### Data flow
1. **Log analytics**  
   `log_digest.pl` → reads log files → feeds lines to `LogDigest` → `summarize` → textual/JSON report.
2. **Deployment**  
   `deploy_release.sh` → `rsync` tarball → SSH → extract → symlink → `systemctl restart` → `curl` health check.
3. **Health monitoring**  
   `healthcheck.sh` `start` → writes PID → worker writes heartbeat → `health` reads heartbeat age → status report.
4. **Data export**  
   `nightly_extract.ksh` → `sqlplus` query → CSV + control file → `gzip` → retry loop.
5. **Billing**  
   Maven build → Flyway migrations → JAR package.  
   Spring scheduler (`reminderScheduler`) triggers `overdueReminderJob` → `JdbcReminderRepository` queries overdue invoices → `SmtpMailGateway` sends emails.  
   Shipment XMLs validated against `shipment.xsd`.  
   Statement XML → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`, `FindBin`.
- **Shell utilities**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `stat`, `sqlplus`, `gzip`.
- **Oracle**: `sqlplus` (SYSDBA, `/`), `settled_payment` table.
- **Maven**: Flyway Core, Surefire, PDFBox (profile), Jackson Databind, HikariCP, Spring Task & Context.
- **Java**: PostgreSQL JDBC, JUnit Jupiter, Testcontainers.
- **XML**: XSD schema (`shipment.xsd`), XSLT stylesheet (`statement-to-html.xsl`).
- **Configuration files**: `/etc/log-digest.conf`, `hosts.staging`, `hosts.production`, `reminder.properties`.

#### Cross‑cutting
- **Logging**: timestamped stderr messages (`log` function in shell scripts), Perl `croak`/`die` with clear messages.
- **Error handling**: strict `set -euo pipefail` in deployment, retry logic in nightly extract, Perl `croak` for malformed lines.
- **Configuration**: environment variables (`DEPLOY_USER`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`), property placeholders in Spring (`reminder.properties`), Maven properties.
- **Performance limits**: Perl `MAX_LINES = 50000`, slow request threshold `SLOW_REQUEST_MS = 1500`.
- **Threading/Concurrency**: Scheduler pool size 2, HikariCP pool size 8, worker heartbeat mechanism.
- **Naming conventions**: XML namespaces (`urn:example:shipment:1`, `urn:example:statement`), bean names follow Spring conventions, Perl module exports.
