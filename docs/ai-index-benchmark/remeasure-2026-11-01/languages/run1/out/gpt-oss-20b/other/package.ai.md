### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:19:16Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates deployment and worker management, and supplies XML schemas and XSLT for an invoice service.

#### Purpose
- Provide reusable log‑digesting and reporting utilities.
- Automate release deployment, queue‑worker control, and payment data export.
- Deliver configuration, validation, and presentation assets for an invoice‑and‑shipment service.

#### Responsibilities
- **Log Aggregation** – parse Apache‑style logs, aggregate per‑host and per‑status statistics, expose CLI reports.
- **Deployment & Process Control** – deploy tarballs to hosts, restart services, monitor health; start/stop/status/health of a queue worker.
- **Data Export** – run Oracle queries, generate gzipped CSVs with row‑count control files.
- **Configuration & Build** – Maven POM, Spring XML wiring, schema definitions, XSLT rendering for invoices.

#### Key units
- `LogDigest` (Perl module) – parses lines, tracks hosts, status codes, slow requests.
- `log_digest.pl` (Perl script) – CLI driver, host alias handling, JSON/text output.
- `deploy_release.sh` (Shell) – rsync tarball, SSH extraction, symlink, service restart, health poll.
- `healthcheck.sh` (Shell) – PID/heartbeat management for queue worker.
- `nightly_extract.ksh` (KornShell) – `sqlplus` query, CSV output, gzip, control file.
- `invoice-service-pom.xml` – Maven build, Flyway, PDF profile.
- `scheduler-context.xml` – Spring beans for reminder job, data source, mail gateway.
- `shipment.xsd` – XML schema for shipment notices.
- `statement-to-html.xsl` – XSLT for statement → HTML with highlighting.

#### Data flow
1. **Log Processing**: `log_digest.pl` streams files → `LogDigest::add` → aggregates → `summarize` → formatted output.
2. **Deployment**: `deploy_release.sh` reads host list → rsync tarball → SSH commands → restart `orderd` → health check via `curl`.
3. **Worker Control**: `healthcheck.sh` writes/reads PID and heartbeat → `health` checks process and heartbeat age.
4. **Export**: `nightly_extract.ksh` runs SQL via `sqlplus` → writes CSV → counts rows → writes `.ctl` → gzips CSV.
5. **Invoice Service**: Maven builds → Flyway runs migrations → JAR runs Spring scheduler → `OverdueReminderJob` queries JDBC → sends emails; XML data validated against `shipment.xsd`; statements transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.
- **Shell**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- **Java**: Maven, Flyway, Spring, HikariCP, PostgreSQL, PDFBox, Jackson, JUnit, Testcontainers.
- **External**: Oracle (via `sqlplus`), SMTP server, HTTP health endpoint.

#### Cross-cutting
- Consistent error handling (`croak`/`die`, exit codes).
- Logging via timestamped `stderr` in shell scripts.
- Configuration through environment variables or property placeholders.
- Retry logic in export script (`MAX_RETRIES`).
- Centralized constants (`MAX_LINES`, `SLOW_REQUEST_MS`, `DEFAULT_LEVEL`) in Perl module.
- Host normalization (`_normalize_host`) for accurate aggregation.
