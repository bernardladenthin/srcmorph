### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:46Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A unified toolkit for web‑log analytics, deployment automation, nightly Oracle data extraction, and invoice‑XML workflow management.

#### Purpose
- Aggregate and report web‑access‑log metrics.
- Automate deployment, worker lifecycle, and health checks.
- Export nightly Oracle data to CSV/CTL and archive it.
- Build, validate, and transform invoice‑service XML artifacts.

#### Responsibilities
- **Log analytics** – parse Apache/Nginx logs, compute host‑level and status‑code statistics, expose CLI reporting.
- **Deployment & ops** – orchestrate tarball deployment, service restarts, health checks, and queue‑worker lifecycle.
- **Oracle data export** – run nightly SQL exports, generate CSV/CTL, gzip, and retry on failure.
- **Invoice XML handling** – Maven build, Spring scheduler, shipment‑notice validation, statement HTML rendering.

#### Key units
- `LogDigest` (Perl module) – core log aggregation and summarization.
- `log_digest.pl` – CLI wrapper for `LogDigest`.
- `deploy_release.sh` – host‑by‑host tarball deployment and `systemctl` restart.
- `healthcheck.sh` – worker lifecycle and health‑check utilities.
- `nightly_extract.ksh` – Oracle export, CSV/CTL generation, gzip, retry logic.
- `invoice-service-pom.xml` – Maven project definition for the invoice service.
- `scheduler-context.xml` – Spring context wiring `OverdueReminderJob`, `JdbcReminderRepository`, `SmtpMailGateway`.
- `shipment.xsd` – XML Schema for shipment notices.
- `statement-to-html.xsl` – XSLT stylesheet for statement XML → HTML.

#### Data flow
1. **Log processing**: CLI → `LogDigest` → `summarize` → formatted output.
2. **Deployment**: script → `rsync`/`ssh` → remote extraction → `systemctl` restart → `curl` health check.
3. **Healthcheck**: command → worker launch (`nohup`) → PID/heartbeat → `health` command checks age.
4. **Nightly export**: options → `sqlplus` query → CSV/CTL → gzip → success log.
5. **Invoice XML**: Maven build → JAR → Spring scheduler triggers job → email via `SmtpMailGateway`; XML documents validated against `shipment.xsd`; statements transformed by `statement-to-html.xsl`.

#### Dependencies
- Perl: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.
- Shell utilities: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `kill`, `sqlplus`, `gzip`.
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
