### ai
- H: 1.0
- C: CFF7E2AB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:13:34Z
- G: 
- A: 0.0.0
- X: package
- F: [other/](other/package.ai.md)
---
> A toolkit that aggregates web‑server logs, automates deployment and health monitoring for a payment system, and supplies XML schemas, XSLT transforms, and build configuration for invoicing and shipment services.

#### Purpose
- Reusable log‑digesting and reporting utilities.  
- Automated release deployment, worker health checks, and nightly data exports.  
- Build, scheduling, XML schema, and XSLT resources for invoicing and shipment workflows.

#### Responsibilities
- **Log aggregation & reporting** – parse access logs, normalize hosts, count requests, track slow requests, summarize status codes.  
- **Deployment & process control** – orchestrate tarball transfer, service restart, health verification, worker lifecycle management.  
- **Data extraction** – scheduled Oracle queries, CSV export, compression, control file generation.  
- **Build & configuration** – Maven project definition, dependency management, native‑PDF support.  
- **Scheduling & reminders** – Spring job wiring for overdue invoice notifications.  
- **XML validation & transformation** – XSD for shipment notices and XSLT for statement HTML rendering.

#### Key units
- **Perl**  
  - `LogDigest` module – OO class with `add`, `summarize`, `top_n`; exports `parse_line`.  
  - `log_digest.pl` – CLI entry point; handles options, host aliases, output formatting.  
- **Shell**  
  - `deploy_release.sh` – host‑by‑host deployment via `rsync`, `ssh`, `systemctl`.  
  - `healthcheck.sh` – POSIX wrapper for queue‑worker lifecycle and heartbeat‑based health probe.  
  - `nightly_extract.ksh` – KSH script that runs `sqlplus`, writes CSV, gzips, and creates a `.ctl` file.  
- **XML / Java**  
  - `invoice-service-pom.xml` – Maven POM with compiler, Jackson, Flyway, PostgreSQL, PDFBox.  
  - `scheduler-context.xml` – Spring beans for reminder data source, repository, mail gateway, job, and cron trigger.  
  - `shipment.xsd` – `ShipmentNoticeType`, `ParcelType`, `AddressType`, `CarrierCode`, `WeightGrams`.  
  - `statement-to-html.xsl` – XSLT stylesheet converting statement XML to styled HTML.

#### Data flow
1. CLI → `LogDigest` → aggregate → `summarize` → text/JSON output.  
2. Host list → `rsync` tarball → SSH → extraction → symlink → `systemctl restart` → health poll.  
3. Worker writes heartbeat → script checks PID/heartbeat age → status report.  
4. `sqlplus` query → CSV → row count → control file → `gzip`.  
5. Maven compiles → Flyway migrations → JAR packaging → optional PDFBox inclusion.  
6. Spring loads beans → scheduler triggers `overdueReminderJob` → mail sent.  
7. XML emitted → validated against `shipment.xsd` → consumed downstream.  
8. XML statement → `statement-to-html.xsl` → HTML with highlighted rows.

#### Dependencies
- **Internal** – `LogDigest` module, common shell library (`lib/common.sh`).  
- **External Perl** – `Getopt::Long`, `JSON::PP`, `POSIX`, `List::Util`, `Carp`.  
- **External Shell** – `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `nohup`, `kill`, `stat`.  
- **External XML/Java** – Jackson, Flyway, PostgreSQL, PDFBox, Spring task scheduler, HikariCP, JUnit, Testcontainers.

#### Cross‑cutting
- Consistent error handling (`set -euo pipefail`, `croak`/`die`).  
- Host normalization and logging level validation in Perl.  
- Timestamped logging and environment‑based configuration across shell scripts.  
- Property placeholders (`reminder.properties`) for database and SMTP settings in Spring.  
- Naming conventions (`reminder*`, `shipment*`, `statement*`) unify build, config, and data artifacts.
