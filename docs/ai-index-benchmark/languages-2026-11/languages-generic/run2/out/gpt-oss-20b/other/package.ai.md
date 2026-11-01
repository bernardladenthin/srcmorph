### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:35:55Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A suite of utilities that aggregates web‑server logs, automates deployment and health monitoring, exports payments, and builds and schedules invoicing services with XML validation and transformation.

#### Purpose
- Provide a command‑line tool for parsing and summarizing access logs.
- Automate release distribution, service restarts, and health checks for a distributed order‑processing system.
- Export settled payments from Oracle to compressed CSV with control metadata.
- Build the Invoice Service JAR, schedule overdue reminder emails, and validate/transform shipment and statement XML.

#### Responsibilities
- **Log aggregation** – Perl module `LogDigest` and script `log_digest.pl`.
- **Deployment orchestration** – Shell script `deploy_release.sh`.
- **Health monitoring & worker control** – Shell script `healthcheck.sh`.
- **Payment export** – Shell script `nightly_extract.ksh`.
- **Build & dependency management** – Maven POM `invoice-service-pom.xml`.
- **Scheduling & email** – Spring context `scheduler-context.xml`.
- **XML schema validation** – `shipment.xsd`.
- **XML transformation** – `statement-to-html.xsl`.

#### Key units
- `LogDigest` (Perl) – aggregates request counts, status codes, slow requests, and host statistics.
- `log_digest.pl` – CLI entry point that formats plain‑text or JSON reports.
- `deploy_release.sh` – drives tarball transfer, extraction, symlink update, service restart, and health polling.
- `healthcheck.sh` – starts/stops a queue‑worker, tracks PID, and reports heartbeat age.
- `nightly_extract.ksh` – runs an Oracle query, spools CSV, gzips, and writes a control file.
- `invoice-service-pom.xml` – Maven build configuration for the Invoice Service.
- `scheduler-context.xml` – Spring beans for data source, repository, SMTP gateway, and overdue reminder job.
- `shipment.xsd` – XML schema for `<shipmentNotice>` with carrier, weight, and address constraints.
- `statement-to-html.xsl` – XSLT stylesheet that renders `<st:statement>` to HTML, highlighting large bookings.

#### Data flow
1. **Log analysis** – CLI parses options → loads host aliases → feeds lines to `LogDigest` → `summarize` → `top_n` → output.
2. **Deployment** – `deploy_release.sh` reads host list → rsync tarball → SSH extraction → symlink update → restart `orderd` → poll `/health` until success.
3. **Health** – `healthcheck.sh` starts `queue-worker` → writes PID → monitors heartbeat file age → reports `OK`, `WARNING`, or `CRITICAL`.
4. **Export** – `nightly_extract.ksh` runs `sqlplus` query → spools CSV → counts rows → writes control file → gzips CSV.
5. **Build** – Maven compiles, runs Flyway migrations, packages JAR.
6. **Scheduling** – Spring initializes beans → `overdueReminderJob.sendDueReminders()` runs daily → queries repository → sends emails via SMTP gateway.
7. **XML** – External system produces XML → `shipment.xsd` validates → `statement-to-html.xsl` transforms statement XML to HTML.

#### Dependencies
- **Perl** – core modules (`POSIX`, `List::Util`, `Carp`, `FindBin`, `Getopt::Long`), CPAN `JSON::PP`.
- **Shell** – external commands (`rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `sqlplus`, `gzip`, `wc`, `date`, `stat`); environment variables (`DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`).
- **XML** – Maven dependencies (`Jackson Databind`, `Flyway Core`, `PostgreSQL Driver`, `JUnit Jupiter`, `Testcontainers PostgreSQL`, `PDFBox`), Spring (`HikariCP`, scheduling, context), XML libs (XSD, XSLT 1.0).

#### Cross-cutting
- **Logging** – timestamped stderr output across all scripts.
- **Error handling** – `croak`/`die`/`fail` with descriptive messages; non‑zero exit codes.
- **Configuration** – constants for host aliases, timeouts, retry counts, heartbeat limits; property placeholders in Spring.
- **Process safety** – PID files guard duplicate workers; health checks enforce service availability before proceeding.
