### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:06:31Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑language toolkit that aggregates web‑server logs, automates deployment and worker management, and supports invoicing workflows through XML validation and XSLT rendering.

#### Purpose
- Consolidate and report access‑log metrics.
- Automate release deployment, worker lifecycle, and nightly payment extraction.
- Build, schedule, and validate invoicing and reminder processes.

#### Responsibilities
- **Log Analysis (Perl)** – parse, aggregate, and report host, status, and latency statistics.
- **Deployment & Process Control (Shell)** – deploy releases, manage queue workers, and export payments.
- **Invoicing & Scheduling (XML/Maven)** – build service JAR, run migrations, schedule reminder jobs, validate shipment notices, and render statements.

#### Key units
- **Perl**: `LogDigest` module (parsing, counting, ranking), `log_digest.pl` CLI driver.
- **Shell**: `deploy_release.sh` (remote deployment & health checks), `healthcheck.sh` (worker PID/heartbeat), `nightly_extract.ksh` (Oracle SQL export).
- **XML/Maven**: `invoice-service-pom.xml` (build & Flyway), `scheduler-context.xml` (Spring scheduler & mail gateway), `shipment.xsd` (shipment notice schema), `statement-to-html.xsl` (HTML rendering).

#### Data flow
1. **Log Processing**: CLI → `LogDigest->add` per line → `summarize` → formatted output.
2. **Deployment**: script → rsync → SSH → remote extraction → service restart → health poll.
3. **Worker Management**: start → PID file; worker → heartbeat; healthcheck reads heartbeat age.
4. **Payment Export**: `sqlplus` query → CSV → control file → gzip.
5. **Invoicing**: Maven build → JAR → Flyway → database; scheduler triggers reminder job → mail gateway; shipment XML → XSD validation; statement XML → XSLT → HTML.

#### Dependencies
- **Perl**: `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`, `Exporter`, `FindBin`.
- **Shell**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- **XML/Maven**: Jackson‑databind, Flyway‑core, PostgreSQL driver, JUnit‑Jupiter, Testcontainers‑PostgreSQL, PDFBox (profile), Spring scheduling, HikariCP, standard XSD/XSLT namespaces.

#### Cross-cutting
- Robust error handling (`set -euo pipefail`, `croak`, `die`).
- Configuration via environment variables, host alias files, and property placeholders.
- Constants: `MAX_LINES=50000`, `SLOW_REQUEST_MS=1500`, default log level `WARN`.
- Ranking logic (`top_n`) and host normalization (`_normalize_host`).
- XML schemas use default values (`country="DE"`), XSLT parameters (`currencySymbol='EUR'`, `highlightAbove=1000`).
