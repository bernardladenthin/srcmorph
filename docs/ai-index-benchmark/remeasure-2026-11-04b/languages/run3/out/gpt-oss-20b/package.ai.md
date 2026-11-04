### ai
- H: 1.0
- C: 865350EF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:57Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that ingests web‑server logs, aggregates metrics, automates deployment and service control, and orchestrates invoicing workflows across Perl, shell, and XML components.

#### Purpose
- Parse and summarize access logs into host‑ and status‑level metrics.  
- Automate application release deployment and queue‑worker lifecycle.  
- Build and schedule invoicing reminders, validate shipment XML, and render statements to HTML.

#### Responsibilities
- **Log Processing (Perl)** – parsing, aggregation, slow‑request detection, reporting.  
- **Deployment & Service Control (Shell)** – host‑by‑host release rollout, queue‑worker PID/heartbeat management, nightly Oracle payment extraction.  
- **Build & Scheduling (XML/Maven/Spring)** – Maven build, Flyway migrations, Spring job scheduling for overdue reminders, XML schema validation, XSLT rendering.

#### Key units
- `LogDigest` (Perl module) – OO interface for adding log lines, summarizing, and retrieving top hosts.  
- `log_digest.pl` (Perl script) – CLI front‑end, option parsing, config loading, output formatting.  
- `deploy_release.sh` (Shell) – orchestrates tarball transfer, extraction, symlink, service restart, health polling.  
- `healthcheck.sh` (Shell) – wrapper for queue‑worker daemon, PID/heartbeat handling.  
- `nightly_extract.ksh` (Shell) – Oracle `sqlplus` extraction, CSV/CTL generation, gzip, retry loop.  
- `invoice-service-pom.xml` (Maven) – project definition, Flyway, optional PDF profile.  
- `scheduler-context.xml` (Spring) – data source, repository, mail gateway, overdue reminder job, cron trigger.  
- `shipment.xsd` (XML Schema) – validation for shipment notices.  
- `statement-to-html.xsl` (XSLT) – transforms statement XML to HTML with currency and threshold parameters.

#### Data flow
1. **Log ingestion** – CLI → `LogDigest` → counters → summary hash → formatted output.  
2. **Deployment** – script reads env → host list → rsync → SSH extraction → symlink → systemctl restart → curl health check.  
3. **Service lifecycle** – `healthcheck.sh` writes/reads PID and heartbeat files; status/health commands query these.  
4. **Payment extraction** – `nightly_extract.ksh` runs `sqlplus`, writes CSV/CTL, gzips, retries on failure.  
5. **Build & scheduling** – Maven compiles, runs Flyway, packages JAR; Spring context loads beans, triggers overdue reminder job via cron; mail gateway sends emails.  
6. **XML processing** – external system produces shipment XML → validated against `shipment.xsd`; statement XML → transformed by `statement-to-html.xsl` into HTML.

#### Dependencies
- **Perl**: core modules (`POSIX`, `List::Util`, `Carp`, `Exporter`), external (`Getopt::Long`, `JSON::PP`, `FindBin`), config file `/etc/log-digest.conf`.  
- **Shell**: external commands (`rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`), host lists (`hosts.staging`, `hosts.production`), common library `lib/common.sh`.  
- **Maven/Spring**: `org.flywaydb:flyway-core`, `com.fasterxml.jackson.core:jackson-databind`, `org.postgresql:postgresql`, optional `org.apache.pdfbox:pdfbox`, Spring context and scheduler, HikariDataSource.  
- **XML**: standard XSD namespace, XSLT 1.0 processor.

#### Cross‑cutting
- Logging via timestamped `log` functions (Shell) and verbosity levels (Perl).  
- Error handling: `die`/`fail` with exit codes (Shell), `croak` on invalid levels (Perl).  
- Configuration through environment variables, defaults, and property placeholders.  
- Retry logic in `nightly_extract.ksh` (max 3 attempts, 60‑second backoff).  
- Consistent namespace prefixes (`shp`, `st`) and parameterized XSLT for currency and thresholds.
