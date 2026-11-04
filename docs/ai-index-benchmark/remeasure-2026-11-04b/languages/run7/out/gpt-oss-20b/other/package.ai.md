### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:08Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A collection of utilities for log analysis, deployment automation, and invoicing infrastructure.

#### Purpose
- Provide reusable log‑digesting logic and a CLI for web‑server logs.
- Automate release deployment, service health checks, and nightly payment extraction for a payment system.
- Build, migrate, schedule, and validate XML for an invoicing and shipment service.

#### Responsibilities
- **Log Processing** – parsing, aggregating, and summarizing access logs (`LogDigest` module + CLI).
- **Deployment & Ops** – remote tarball deployment, service restart, health polling, queue‑worker lifecycle, and nightly Oracle data export.
- **Build & Data Migration** – Maven build, Flyway migrations, optional PDF support.
- **Scheduling & Messaging** – Spring scheduler for overdue reminders, email gateway integration.
- **XML Validation & Presentation** – shipment notice schema, XSLT for statement rendering.

#### Key units
- `LogDigest` (Perl module) – parses log lines, aggregates counts, provides `summarize` and `top_n`.
- `log_digest.pl` (Perl script) – CLI wrapper for `LogDigest`, handles aliases, exclusions, JSON output.
- `deploy_release.sh` (Bash) – rsync tarball, remote extraction, symlink, `systemctl restart`, health poll.
- `healthcheck.sh` (Bash) – PID/heartbeat management for queue‑worker, status/health reporting.
- `nightly_extract.ksh` (KornShell) – Oracle `sqlplus` query, CSV/CTL generation, gzip, retry logic.
- `invoice-service-pom.xml` (Maven) – project definition, Flyway, PDF profile.
- `scheduler-context.xml` (Spring) – beans for reminder job, data source, mail gateway, scheduler.
- `shipment.xsd` (XML Schema) – defines `shipmentNotice` structure.
- `statement-to-html.xsl` (XSLT) – transforms statement XML to HTML with amount highlighting.

#### Data flow
1. **Log Analysis** – CLI → alias/load → `LogDigest->add` per line → `summarize` → text/JSON output.
2. **Deployment** – script → `rsync` tarball → SSH → remote extraction → symlink `current` → `systemctl restart` → `/health` poll.
3. **Healthcheck** – start → `nohup queue-worker` → PID/heartbeat files → health command reads heartbeat age.
4. **Nightly Extract** – script parses dates → `sqlplus` query → CSV + control file → gzip → retry on failure.
5. **Build/Migration** – Maven compiles → Flyway runs migrations → JAR packaged.
6. **Scheduling** – Spring scheduler triggers `OverdueReminderJob` → queries repository → emails via `mailGateway`.
7. **XML Processing** – shipment XML validated against `shipment.xsd`; statement XML transformed by `statement-to-html.xsl`.

#### Dependencies
- **Perl**: `POSIX`, `List::Util`, `Carp`, `Exporter`, `Getopt::Long`, `JSON::PP`.
- **Shell**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- **Oracle**: `settled_payment` table, SYSDBA access.
- **Maven**: Flyway, Jackson, JUnit, Testcontainers, PDFBox (optional), HikariCP, Spring task/context.
- **XML/XSLT**: Standard XSD namespace, XSLT parameters (`currencySymbol`, `highlightAbove`).

#### Cross-cutting
- **Logging**: All shell scripts use a timestamped `log` function; Perl module validates logging level.
- **Error handling**: Perl `croak` on invalid level or line count; shell scripts exit with codes 1–4 and `die`/`fail` helpers.
- **Configuration**: Host alias files, exclusion sets, deployment host lists, heartbeat age, retry counts, and schema defaults are centrally defined.
- **Concurrency**: Scheduler pool size 2; HikariCP pool size 8; heartbeat age threshold 120 s.
- **Retry logic**: `nightly_extract.ksh` retries up to 3 times with sleep intervals.
