### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:56:45Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release deployment, queue-worker health management, and nightly export of settled payments.

#### Purpose
- Provides operational shell tools for deploying `orderd`, managing and checking the queue worker, and exporting Oracle payment data.
- Produces deployment status, worker health results, and compressed payment extracts with row-count controls.

#### Responsibilities
- **Release rollout:** `deploy_release.sh` deploys a tagged archive sequentially to staging or production hosts, restarts `orderd`, and stops at the first failed health check; supports dry-run.
- **Worker lifecycle and monitoring:** `healthcheck.sh` starts, stops, restarts, and reports status for `queue-worker`; health checks require a running process and a recent heartbeat.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a business day, writes a semicolon-separated CSV and control file, then gzips the CSV; retries failed exports up to three times.

#### Key units
- `deploy_release.sh` — parses deployment options, loads environment hosts, deploys each release, and polls host health endpoints.
- `healthcheck.sh` — dispatches `start`, `stop`, `restart`, `status`, and `health` operations using PID and heartbeat files.
- `nightly_extract.ksh` — runs the Oracle export, counts CSV lines, and writes the compressed extract and control file.
- `settled_payment` — Oracle source table queried for `payment_id`, `account_no`, `amount`, and `currency`.

#### Data flow
- **Deployment:** `build/$TAG.tar.gz` → selected host via `rsync` → extraction under `/opt/releases/$TAG` and `current` symlink update → `orderd` restart → `/health` polling.
- **Worker health:** PID file and process check determine whether the worker is running; heartbeat-file age determines whether a running worker is healthy.
- **Payment export:** business day and export directory options → Oracle query → CSV → row count and business-day control file → gzipped CSV.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, `ssh`, `curl`, `sudo`, `systemctl`, and `tar`; remote health endpoint is `http://$host:8080/health`.
- `healthcheck.sh` uses `/usr/local/bin/queue-worker`, PID and heartbeat files, and `nohup`, `kill`, and `stat`.
- `nightly_extract.ksh` uses `sqlplus` with `CONNECT / AS SYSDBA`, reads Oracle table `settled_payment`, and uses `gzip`, `wc`, and `sleep`.

#### Cross-cutting
- Standalone shell scripts with configurable environment variables and command-line options; `deploy_release.sh` uses Bash strict mode, while the worker and export scripts do not.
- Timestamped stderr logging is used by deployment and export operations.
- Failure handling is script-specific: deployment stops on the first host failure, worker health uses distinct exit codes for stopped and stale-heartbeat states, and payment export retries failed queries.
