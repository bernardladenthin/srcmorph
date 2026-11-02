### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:55:33Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Supports release rollout, queue-worker health management, and nightly settled-payment exports for an order-processing environment.

#### Purpose
- Provides operational shell commands for deploying `orderd`, managing a queue worker, and exporting settled Oracle payments.
- Runs each task directly, with task-specific inputs, status reporting, and failure handling.

#### Responsibilities
- **Release deployment (`deploy_release.sh`)** — selects staging or production hosts, transfers and installs a tagged release, restarts `orderd`, and verifies each host’s health endpoint; supports dry runs and deploys hosts sequentially.
- **Queue-worker lifecycle and monitoring (`healthcheck.sh`)** — starts, stops, restarts, or reports the worker process, and checks its heartbeat age.
- **Payment export (`nightly_extract.ksh`)** — queries Oracle `settled_payment` for a business date, writes a CSV and row-count control file, and compresses the CSV; retries failed exports.

#### Key units
- `deploy_host` — transfers, installs, and restarts a release on one host.
- `wait_healthy` — polls a host’s `/health` endpoint until healthy or timed out.
- `load_hosts` — loads host names from the environment-specific host file.
- `is_running` — checks the queue-worker PID file and process.
- `start_worker` / `stop_worker` — manage the worker process and PID file.
- `heartbeat_age` — calculates the heartbeat file’s age for health checks.
- `run_export` — runs the Oracle payment query and writes CSV output.
- `write_control` — counts CSV lines and writes the business-date control record.

#### Data flow
- **Deployment:** environment and tag → host list and release archive → per-host transfer, extraction, symlink update, and `orderd` restart → HTTP health verification.
- **Worker health:** action argument and PID/heartbeat files → process lifecycle operation or heartbeat-age check → status text and action-specific exit code.
- **Payment export:** business date and export directory → Oracle query of settled payments → CSV row count and `.ctl` file → gzipped CSV.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production` and `build/$TAG.tar.gz`, and uses `rsync`, `ssh`, and `curl`; the remote service is `orderd`.
- `healthcheck.sh` manages `/usr/local/bin/queue-worker` using configurable `PID_FILE` and `HEARTBEAT_FILE` paths.
- `nightly_extract.ksh` connects to local Oracle via `CONNECT / AS SYSDBA`, reads `settled_payment` fields `payment_id`, `account_no`, `amount`, and `currency`, and uses `sqlplus` and `gzip`.

#### Cross-cutting
- Scripts are standalone commands; only `deploy_release.sh` sources a shared file (`lib/common.sh`).
- Configuration is provided through command-line options and environment-variable defaults; the payment export defaults to `PAYPRD` and `/data/export/payments`.
- Failure behavior is script-specific: deployment stops on a host failure, worker checks return distinct monitoring exit codes, and payment exports retry up to three times with 60-second delays.
