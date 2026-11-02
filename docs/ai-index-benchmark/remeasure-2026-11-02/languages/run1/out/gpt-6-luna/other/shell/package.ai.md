### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:24:40Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release rollout, queue-worker supervision, and nightly settled-payment export for production operations.

#### Purpose
- Provides standalone shell jobs for deploying application releases, checking worker health, and exporting payment data.
- Covers staging/production deployment, queue-worker lifecycle management, and Oracle-backed daily payment extracts.

#### Responsibilities
- **Release deployment — `deploy_release.sh`:** Selects hosts by environment, installs a tagged archive sequentially, restarts `orderd`, and gates progress on HTTP health checks; supports dry runs.
- **Worker operations — `healthcheck.sh`:** Starts, stops, restarts, and reports queue-worker status; checks heartbeat freshness against a 120-second limit.
- **Payment export — `nightly_extract.ksh`:** Queries settled payments for a business day, writes a semicolon-separated CSV and row-count control file, then compresses the CSV; retries failed exports.

#### Key units
- `deploy_release.sh` — deploys `build/$TAG.tar.gz` to hosts listed in `hosts.staging` or `hosts.production`, under `/opt/releases`.
- `healthcheck.sh` — init-style command interface for `/usr/local/bin/queue-worker`, using a PID file and heartbeat file.
- `nightly_extract.ksh` — Oracle export job for `settled_payment`, producing `payments_<business_day>.csv.gz` and its `.ctl` file.
- `run_export` — runs the Oracle query that writes payment ID, account number, amount, and currency to CSV.
- `write_control` — records CSV row count and business day in the control file.
- `wait_healthy` — polls a deployed host’s `/health` endpoint, with a 30-second timeout.

#### Data flow
- `deploy_release.sh`: CLI environment and tag → environment host list and release archive → remote extraction, `current` symlink update, and `orderd` restart → per-host health check.
- `healthcheck.sh`: action argument and PID/heartbeat files → worker lifecycle or health evaluation → status output and action-specific exit status.
- `nightly_extract.ksh`: business-day and export-directory options → Oracle query to CSV → row count to `.ctl` file → gzip compression.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`; uses host lists, `build/$TAG.tar.gz`, SSH/rsync, and remote `systemctl` operations.
- `healthcheck.sh` depends on the queue-worker executable and the configured PID and heartbeat paths.
- `nightly_extract.ksh` uses `sqlplus` with `CONNECT / AS SYSDBA`, queries `settled_payment`, and uses `gzip` for output compression.

#### Cross-cutting
- All three are directly runnable operational scripts rather than application libraries.
- `deploy_release.sh` uses Bash strict mode; `healthcheck.sh` uses POSIX `sh` and `nightly_extract.ksh` uses `ksh93`, both without strict-mode settings.
- Scripts log operational messages to stderr; deployment and export logs include timestamps.
- Failure handling is script-specific: deployment stops on host health-check failure, worker health uses distinct exit codes, and export retries up to three times with 60-second sleeps.
