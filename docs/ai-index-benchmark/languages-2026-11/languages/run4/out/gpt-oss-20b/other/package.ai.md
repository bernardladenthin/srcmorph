### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:33:24Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of utilities and assets that aggregate web‑server logs, automate deployment and health monitoring for a payment service, and supply configuration, validation, and presentation artifacts for an invoicing/shipment system.

#### Purpose
- Log aggregation and reporting for web servers.
- Deployment, process management, and data export for a payment processing service.
- Build, validation, scheduling, and presentation support for an invoicing/shipment application.

#### Responsibilities
- **Log Processing** – parse, aggregate, and report HTTP access logs (`LogDigest` module, `log_digest.pl` script).
- **Deployment & Health** – orchestrate tarball deployment, service restart, queue‑worker lifecycle, and health checks (`deploy_release.sh`, `healthcheck.sh`).
- **Data Export** – nightly Oracle export to gzipped CSV with control files (`nightly_extract.ksh`).
- **Build & Runtime** – Maven project setup, Flyway migrations, PDFBox profile (`invoice-service-pom.xml`).
- **Scheduling & Email** – Spring beans for overdue reminder jobs (`scheduler-context.xml`).
- **Validation** – XML schema for shipment notices (`shipment.xsd`).
- **Presentation** – XSLT stylesheet for statement HTML rendering (`statement-to-html.xsl`).

#### Key units
- `LogDigest` (Perl module) – aggregates host, status, and slow‑request counts.
- `log_digest.pl` – CLI for generating summaries.
- `deploy_release.sh` – deploys releases across hosts.
- `healthcheck.sh` – manages queue‑worker process and health status.
- `nightly_extract.ksh` – Oracle export, gzip, control file generation.
- `invoice-service-pom.xml` – Maven build definition.
- `scheduler-context.xml` – Spring context for reminder jobs.
- `shipment.xsd` – XML schema for shipment notices.
- `statement-to-html.xsl` – XSLT for statement to HTML conversion.

#### Data flow
1. **Log Aggregation**: CLI → `LogDigest` → summary hash → formatted output.
2. **Deployment**: `deploy_release.sh` → `rsync` → SSH extraction → `systemctl restart` → health poll.
3. **Health**: `healthcheck.sh` reads PID/heartbeat → status codes.
4. **Export**: `nightly_extract.ksh` → `sqlplus` → CSV → `gzip` → control file.
5. **Runtime**: Spring loads `scheduler-context.xml` → `overdueReminderJob` queries DB → emails via `mailGateway`.
6. **Validation**: Shipment XML validated against `shipment.xsd`.
7. **Presentation**: XSLT transforms statement XML to HTML.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Getopt::Long`, `JSON::PP`.
- **Shell**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`; `$SCRIPT_DIR/lib/common.sh`.
- **Oracle**: `sqlplus / AS SYSDBA`.
- **Java/Maven**: Jackson, Flyway, PostgreSQL JDBC, JUnit Jupiter, Testcontainers, PDFBox, HikariCP, Spring Framework.
- **XML**: XSD namespaces `urn:example:shipment:1`, `urn:example:statement`.

#### Cross-cutting
- Consistent error handling (`croak`, `die`, exit codes 0‑64).
- Logging: timestamped stderr messages.
- Configuration via files (`/etc/log-digest.conf`) and environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Validation constraints in XSD (e.g., `WeightGrams` ≤ 31500, default country `DE`).
- Scheduling with Spring Task Scheduler (cron, pool size).
- Retry logic in `nightly_extract.ksh` (up to 3 attempts).
