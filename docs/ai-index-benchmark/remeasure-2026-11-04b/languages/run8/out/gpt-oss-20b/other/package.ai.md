### other
- H: 1.0
- C: 8B47BE8A
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:57Z
- G: 
- A: 0.0.0
- X: package
- F: [perl/](perl/package.ai.md)
- F: [shell/](shell/package.ai.md)
- F: [xml/](xml/package.ai.md)
---
> A multi‑layered toolkit for processing web logs, automating deployment and data extraction, and configuring an invoice‑reminder service with XML schemas and XSLT rendering.

#### Purpose
- Aggregate HTTP access logs into host‑ and status‑level summaries, flagging slow requests.
- Automate release deployment, queue‑worker lifecycle, and nightly Oracle data export for a payment service.
- Build, schedule, validate, and render invoice‑related XML data for a Spring‑based reminder system.

#### Responsibilities
- **Log Aggregation** – parsing, normalizing, counting, and reporting HTTP logs (Perl).
- **Deployment & Monitoring** – host‑by‑host release rollout, service restarts, health polling, and worker process control (Shell).
- **Data Extraction** – Oracle `settled_payment` export to CSV/CTL, compression, and retry logic (Shell).
- **Build & Migration** – Maven project setup with Flyway and optional PDFBox profile (XML).
- **Scheduling & Email** – Spring job that emails overdue invoices (XML).
- **Schema Validation** – XSD for shipment notices (XML).
- **Presentation** – XSLT that transforms statement XML to styled HTML (XML).

#### Key units
- **Perl**  
  - `LogDigest` – OO class for log ingestion, aggregation, and top‑N host reporting.  
  - `log_digest.pl` – CLI driver handling options, host exclusions, and output formatting.

- **Shell**  
  - `deploy_release.sh` – orchestrates tarball distribution, symlink updates, service restarts, and health checks.  
  - `healthcheck.sh` – PID/heartbeat‑based lifecycle and health status for the queue worker.  
  - `nightly_extract.ksh` – Oracle `sqlplus` dump of `settled_payment`, CSV/CTL generation, gzip, and retry loop.

- **XML**  
  - `invoice-service-pom.xml` – Maven POM with Flyway migration path and PDFBox profile.  
  - `scheduler-context.xml` – Spring beans for reminder data source, repository, mail gateway, job, and scheduler.  
  - `shipment.xsd` – XML schema for `<shipmentNotice>` with carrier, parcel, and address types.  
  - `statement-to-html.xsl` – XSLT stylesheet converting `<st:statement>` to `<html>` with currency and highlight parameters.

#### Data flow
1. **Log Aggregation** – CLI → `LogDigest` → summary hash → plain text/JSON output.  
2. **Deployment** – `deploy_release.sh` → rsync → SSH → extraction → symlink → service restart → health poll.  
3. **Health Check** – `healthcheck.sh` reads PID/heartbeat → reports status.  
4. **Data Export** – `nightly_extract.ksh` runs `sqlplus` → CSV/CTL → gzip → log.  
5. **Invoice Service** – Maven build → Flyway migrations → optional PDFBox PDF generation.  
6. **Reminder Scheduler** – Spring context → repository → overdue invoices → email via `SmtpMailGateway`.  
7. **Shipment Notice** – Warehouse emits XML → validated against `shipment.xsd`.  
8. **Statement Rendering** – XML statement → `statement-to-html.xsl` → HTML output.

#### Dependencies
- **Perl** – `Getopt::Long`, `JSON::PP`, `List::Util`, `Carp`, `Exporter`.  
- **Shell** – `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.  
- **XML** – Jackson Databind, Flyway Core, PostgreSQL JDBC, JUnit Jupiter, Testcontainers PostgreSQL, PDFBox, HikariCP, Spring Task Scheduler.  
- **External tables** – Oracle `settled_payment`; PostgreSQL for Flyway migrations.  
- **Configuration files** – `LOG_DIGEST_CONFIG`, host lists (`hosts.staging`, `hosts.production`), `reminder.properties`.

#### Cross‑cutting
- Consistent logging with timestamps across all scripts.  
- Error handling via `croak`/`die` in Perl; `die`/`fail` with exit codes in Shell.  
- Host normalization (lower‑case, strip trailing dot) in log parsing.  
- Constants for limits (`MAX_LINES`, `SLOW_REQUEST_MS`) and retry counts in Shell scripts.  
- XML namespaces (`xs`, `shp`, `st`) uniformly applied in schemas and XSLT.  
- Parameterization in XSLT (`currencySymbol`, `highlightAbove`) for flexible rendering.
