### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:41:01Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates order-service releases, queue-worker health management, and nightly settled-payment exports for downstream processing.

#### Purpose
- Provides operational shell commands for deploying tagged service builds, controlling and checking the queue worker, and exporting daily settled payments from Oracle.
- Produces deployment health results and a compressed payment extract with a row-count control file.

#### Responsibilities
- **Release deployment:** Selects staging or production hosts, transfers and installs a tagged archive, restarts `orderd`, and checks host health sequentially; supports dry runs.
- **Worker lifecycle and monitoring:** Starts, stops, restarts, and reports status for `queue-worker`; checks process presence and heartbeat freshness.
- **Payment export:** Queries Oracle `settled_payment` for a business day, writes a CSV and control file, compresses the CSV, and retries failed exports.

#### Key units
- `deploy_release.sh` — orchestrates tagged deployments to hosts from `hosts.staging` or `hosts.production`.
- `check_prereqs`, `load_hosts`, `deploy_host`, `wait_healthy`, `cleanup` — deployment validation, host selection, per-host rollout and health polling, and exit cleanup.
- `healthcheck.sh` — command dispatcher for queue-worker lifecycle and health actions.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — process and heartbeat checks plus worker control.
- `nightly_extract.ksh` — exports settled payments to `payments_<business_day>.csv.gz` and writes the corresponding `.ctl` file.
- `run_export`, `write_control`, `require_tools` — Oracle extraction, CSV row-count control generation, and utility validation.

#### Data flow
- **Deployment:** CLI environment and tag → selected host list and `build/<tag>.tar.gz` → per-host transfer and remote installation → `orderd` restart → `/health` polling.
- **Worker health:** Action argument → PID-file process check; for `health`, heartbeat-file age check → status message and action-specific exit code.
- **Payment export:** Business day and export directory → Oracle query of `settled_payment` → CSV line count and control file → gzip-compressed CSV.

#### Dependencies
- `deploy_release.sh` sources `$SCRIPT_DIR/lib/common.sh`; uses `rsync`, `ssh`, and `curl`, with remote `bash`, `tar`, and `systemctl` operations. The deployed service is `orderd`, and health checks target port `8080`.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker` with `--heartbeat`; uses PID and heartbeat files, and `stat -c %Y` to read heartbeat modification time.
- `nightly_extract.ksh` uses `sqlplus` with `CONNECT / AS SYSDBA`, queries Oracle table `settled_payment`, and uses `gzip` and `wc`.

#### Cross-cutting
- All three are directly invoked shell commands; implementation languages are Bash, POSIX `sh`, and `ksh93`.
- Deployment and export scripts log timestamped messages to standard error; worker lifecycle and health results are printed to standard output.
- Failure handling is expressed through script-specific exit codes; deployment also uses an `EXIT` trap for cleanup, while the worker and export scripts define no traps.
