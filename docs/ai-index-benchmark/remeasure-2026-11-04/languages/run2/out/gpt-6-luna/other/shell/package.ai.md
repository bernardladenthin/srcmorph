### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:57Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates order-service releases, queue-worker health and lifecycle checks, and daily settled-payment exports.

#### Purpose
- Provides operational shell commands for deploying tagged releases, managing worker availability, and extracting payment data.
- Supports staging/production rollout, heartbeat-based health reporting, and Oracle-backed daily CSV delivery.

#### Responsibilities
- **Release deployment — `deploy_release.sh`:** Selects staging or production hosts, installs a tagged build, restarts `orderd`, and verifies each host before continuing.
- **Worker operations — `healthcheck.sh`:** Starts, stops, restarts, and reports queue-worker status; checks heartbeat freshness for health.
- **Payment exports — `nightly_extract.ksh`:** Exports settled payments for a business day to gzipped CSV and writes a row-count control file; retries failed exports.

#### Key units
- `deploy_host` — transfers and installs a release on one host, restarts `orderd`, and waits for health.
- `wait_healthy` — polls `http://<host>:8080/health` with a 30-second limit.
- `start_worker` / `stop_worker` — launch the queue worker and manage its PID file.
- `is_running` / `heartbeat_age` — determine worker liveness and heartbeat age.
- `run_export` — queries Oracle for settled payments matching the business day.
- `write_control` — writes the export row count and business day to the `.ctl` file.
- `settled_payment` — Oracle source table; exported columns are `payment_id`, `account_no`, `amount`, and `currency`.

#### Data flow
- `deploy_release.sh`: parses environment and tag → loads host list and `build/<tag>.tar.gz` → copies, extracts, switches the `current` symlink, and restarts `orderd` host by host → polls each health endpoint.
- `healthcheck.sh`: reads PID and heartbeat files → performs the requested lifecycle or status action → reports worker state and, for `health`, checks heartbeat age against 120 seconds.
- `nightly_extract.ksh`: selects a business day and output directory → queries Oracle → writes CSV and row-count control file → gzips the CSV; retries failed exports up to three times.

#### Dependencies
- **Deployment:** `lib/common.sh`, `hosts.staging` or `hosts.production`, `build/<tag>.tar.gz`, and external `rsync`, `ssh`, and `curl`; remote host service `orderd`.
- **Worker operations:** `/usr/local/bin/queue-worker`, `/run/queue-worker.pid`, and `/var/lib/queue-worker/heartbeat`.
- **Payment export:** Oracle via `sqlplus` (`CONNECT / AS SYSDBA`), `settled_payment`, and external `gzip`, `wc`, and `mkdir`.

#### Cross-cutting
- Scripts are directly executable shell commands: Bash for deployment, POSIX `sh` for worker management, and `ksh93` for payment export.
- Configurable environment values include deployment user and SSH key, worker PID/heartbeat paths, and Oracle SID; defaults are provided per script.
- Each command reports failures through nonzero exit statuses; deployment stops at the first unhealthy host, while payment export retries up to three times.
