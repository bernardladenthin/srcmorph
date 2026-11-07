### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:12Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A collection of reusable utilities for log analysis, deployment automation, and invoicing infrastructure.

#### Purpose
- Provide a CLI for parsing and summarizing web‑server logs.
- Automate release deployment, health checks, and nightly payment extraction.
- Build, migrate, schedule, and validate XML for invoicing and shipment services.

#### Responsibilities
- **Log Processing** – `LogDigest` module and `log_digest.pl` CLI.
- **Deployment & Ops** – `deploy_release.sh`, `healthcheck.sh`, `nightly_extract.ksh`.
- **Build & Migration** – Maven `pom.xml` with Flyway, optional PDF support.
- **Scheduling & Messaging** – Spring scheduler (`scheduler-context.xml`) and email gateway.
- **XML Validation & Presentation** – `shipment.xsd` schema and `statement-to-html.xsl` XSLT.

#### Key units
- `LogDigest` (Perl) – parses log lines, aggregates counts, exposes `summarize` and `top_n`.
- `log_digest.pl` – CLI wrapper for `LogDigest`, handles aliases, exclusions, JSON output.
- `deploy_release.sh` – rsync tarball, remote extraction, symlink, `systemctl restart`, health poll.
- `healthcheck.sh` – PID/heartbeat management for queue‑worker, status reporting.
- `nightly_extract.ksh` – Oracle `sqlplus` query, CSV/CTL generation, gzip, retry logic.
- `invoice-service-pom.xml` – Maven project, Flyway migrations, PDF profile.
- `scheduler-context.xml` – Spring beans for overdue reminder job, data source, mail gateway, scheduler.
- `shipment.xsd` – XML Schema for `shipmentNotice`.
- `statement-to-html.xsl` – XSLT for transforming statement XML to HTML with amount highlighting.

#### Data flow
1. **Log Analysis** – CLI → `LogDigest->add` per line → `summarize` → output.
2. **Deployment** – Script → `rsync` → SSH → remote extraction → symlink → `systemctl restart` → `/health` poll.
3. **Healthcheck** – Start worker → PID/heartbeat files → health command reads heartbeat age.
4. **Nightly Extract** – Script parses dates → `sqlplus` query → CSV + control file → gzip → retry on failure.
5. **Build/Migration** – Maven compiles → Flyway runs migrations → JAR packaged.
6. **Scheduling** – Spring scheduler triggers `OverdueReminderJob` → queries repository → emails via `mailGateway`.
7. **XML Processing** – Shipment XML validated against `shipment.xsd`; statement XML transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.
- **Shell**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- **Oracle**: `settled_payment` table, SYSDBA access.
- **Maven**: Flyway, Jackson, JUnit, Testcontainers, PDFBox (optional), HikariCP, Spring task/context.
- **XML/XSLT**: Standard XSD namespace, XSLT parameters (`currencySymbol`, `highlightAbove`).

#### Cross-cutting
- **Logging**: Timestamped `log` function in shell scripts; Perl module validates logging level.
- **Error handling**: Perl `croak` on invalid input; shell scripts exit with codes 1–4 and `die`/`fail` helpers.
- **Configuration**: Central host alias files, exclusion sets, deployment host lists, heartbeat age, retry counts, schema defaults.
- **Concurrency**: Scheduler pool size 2; HikariCP pool size 8; heartbeat age threshold 120 s.
- **Retry logic**: `nightly_extract.ksh` retries up to 3 times with sleep intervals.
