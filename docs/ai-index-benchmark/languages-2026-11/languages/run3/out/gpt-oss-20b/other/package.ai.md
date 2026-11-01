### other
- H: 1.0
- C: 522CF165
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:29Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A toolkit for processing web‑server logs, deploying services, and managing invoice‑related XML workflows.

#### Purpose
- Aggregate and report web‑access‑log metrics.
- Automate deployment, worker management, and nightly data extraction.
- Build, validate, and transform invoice‑service XML artifacts.

#### Responsibilities
- **Log analytics** – parse Apache/Nginx logs, compute host‑level and status‑code statistics, expose CLI reporting.
- **Deployment & ops** – orchestrate tarball deployment, service restarts, health checks, and queue‑worker lifecycle.
- **Oracle data export** – run nightly SQL exports, generate CSV/CTL, gzip, and retry on failure.
- **Invoice XML handling** – Maven build, Spring scheduler, shipment‑notice validation, statement HTML rendering.

#### Key units
- `LogDigest` (Perl module) – `new`, `add`, `summarize`, `top_n`; constants `MAX_LINES`, `SLOW_REQUEST_MS`.
- `log_digest.pl` (Perl script) – CLI entry point, option parsing, host‑alias loading, output formatting.
- `deploy_release.sh` (Shell) – host‑by‑host tarball transfer, extraction, symlink, `systemctl` restart, health polling.
- `healthcheck.sh` (Shell) – `start/stop/restart/status/health` commands, PID/heartbeat file handling.
- `nightly_extract.ksh` (Korn shell) – option parsing, `sqlplus` export, CSV/CTL creation, gzip, retry logic.
- `invoice-service-pom.xml` – Maven project definition, dependencies, PDFBox profile.
- `scheduler-context.xml` – Spring context for `OverdueReminderJob`, `JdbcReminderRepository`, `SmtpMailGateway`.
- `shipment.xsd` – XML Schema for shipment notices (carrier, parcel, address).
- `statement-to-html.xsl` – XSLT stylesheet for converting statement XML to HTML.

#### Data flow
1. **Log processing**: CLI → `LogDigest` → `summarize` → text/JSON output.
2. **Deployment**: script → `rsync`/`ssh` → remote extraction → `systemctl` restart → `curl` health check.
3. **Healthcheck**: command → worker launch (`nohup`) → PID/heartbeat → `health` command checks age.
4. **Nightly export**: options → `sqlplus` query → CSV/CTL → gzip → success log.
5. **Invoice XML**: Maven build → compiled JAR → Spring scheduler triggers job → email via `SmtpMailGateway`; XML documents validated against `shipment.xsd`; statements transformed by `statement-to-html.xsl`.

#### Dependencies
- Perl: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.
- Shell: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Oracle: `PAYPRD` SID, `sqlplus / AS SYSDBA`.
- Maven: `jackson-databind`, `flyway-core`, `postgresql`, `pdfbox`.
- Spring: `HikariCP`, custom `JdbcReminderRepository`, `SmtpMailGateway`, `OverdueReminderJob`.
- XML: XSD core, XSLT 1.0 processor.

#### Cross‑cutting
- Consistent error handling (`set -euo pipefail`, `Carp::croak`).
- Host normalization and alias mapping in log digest.
- PID/heartbeat conventions for worker monitoring.
- Retry logic (max 3 attempts) in nightly export.
- Property placeholders (`${...}`) across Maven and Spring configurations.
- Namespace consistency (`urn:example:...`) in XML schemas and XSLT.
