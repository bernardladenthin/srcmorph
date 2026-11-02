### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:58:24Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A unified Perl‑based toolkit that aggregates web‑server logs, automates deployment and data export, and supplies XML artifacts for an invoicing service.

#### Purpose
- Aggregate and report web‑server access logs by host and status.
- Automate release deployment, worker lifecycle, and nightly Oracle data export.
- Provide XML schemas, XSLT, and Spring configuration for an invoicing service’s build, scheduling, validation, and presentation layers.

#### Responsibilities
- **Log Processing** – parse, aggregate, and report access‑log metrics.
- **Deployment & Ops** – automate release rollout, worker lifecycle, and data export.
- **Invoice Service Artifacts** – build configuration, scheduled reminder jobs, XML validation, and HTML rendering.

#### Key units
- `LogDigest` (Perl module): `new`, `add`, `summarize`, `top_n`.
- `log_digest.pl` (CLI): option parsing, host alias handling, JSON/text output.
- `deploy_release.sh`: host‑by‑host tarball transfer, extraction, symlink, service restart, health check.
- `healthcheck.sh`: start/stop/restart/status/health for `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh`: Oracle `sqlplus` query → CSV → gzip → control file.
- `invoice-service-pom.xml`: Maven build, Flyway, PDF profile.
- `scheduler-context.xml`: Spring beans for reminder scheduler, data source, repository, mail gateway, job trigger.
- `shipment.xsd`: XML schema for `shipmentNotice` with constraints.
- `statement-to-html.xsl`: XSLT to transform `<st:statement>` into styled HTML.

#### Data flow
1. **Log Analysis** – CLI → `LogDigest` → aggregated summary → formatted output or JSON.  
2. **Deployment** – `deploy_release.sh` → rsync tarball → SSH extraction → symlink → `systemctl restart` → health poll.  
3. **Worker Management** – `healthcheck.sh` → PID/heartbeat files → status/health checks.  
4. **Data Export** – `nightly_extract.ksh` → `sqlplus` query → CSV → gzip → `.ctl` file.  
5. **Invoice Service Runtime** – Spring context loads `scheduler-context.xml` → scheduled `overdueReminderJob` queries `reminderRepository` → emails via `mailGateway`.  
6. **Validation & Presentation** – Incoming shipment XML → validated against `shipment.xsd`; Statement XML → transformed by `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`, `FindBin`.  
- **Shell**: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`; external binary `/usr/local/bin/queue-worker`; Oracle `settled_payment` table.  
- **Java**: Maven (Java 17), HikariCP, Jackson, Flyway, PostgreSQL driver, PDFBox, JUnit 5, Testcontainers.  
- **XML**: XSD (`shipment.xsd`), XSLT (`statement-to-html.xsl`), Spring XML (`scheduler-context.xml`).

#### Cross-cutting
- **Configuration**: Host aliases/exclusions via `/etc/log-digest.conf`; environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`); `reminder.properties` for database/SMTP.  
- **Logging & Error Handling**: Consistent timestamped stderr logs; `croak`/`die` with specific exit codes; retry loops in `nightly_extract.ksh`.  
- **Normalization & Constraints**: Host normalization in `LogDigest`; XSD defaults and limits (e.g., `WeightGrams ≤ 31500`, `country` default “DE”).  
- **Utility Functions**: `top_n` ranking, heartbeat age checks, XSLT parameters (`currencySymbol`, `highlightAbove`).  
- **Security**: SSH key usage, controlled file permissions, service restarts via `systemctl`.
