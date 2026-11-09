### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:52Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Supports release operations by deploying `orderd`, managing queue-worker health, and exporting settled Oracle payments for downstream processing.

#### Purpose
- Provides standalone shell utilities for service rollout and worker lifecycle/health checks.
- Produces a nightly, business-day-filtered settled-payments export with a row-count control file.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, installs a tagged build, restarts `orderd`, and gates progress on host health checks; supports dry-run mode.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status or heartbeat health for `queue-worker`.
- **Payment export:** `nightly_extract.ksh` queries Oracle `settled_payment`, writes a CSV and `.ctl` row-count file, then gzips the export; retries failed exports.

#### Key units
- `deploy_host` — uploads and installs a release on one host, updates the `current` symlink, and restarts `orderd`.
- `wait_healthy` — polls a host’s `/health` endpoint after deployment.
- `load_hosts` — loads the selected staging or production host list.
- `is_running` — checks whether the PID in the queue worker’s PID file identifies a live process.
- `heartbeat_age` — calculates the age of the worker heartbeat file.
- `start_worker` / `stop_worker` — launch `queue-worker` and record its PID, or signal it and remove the PID file.
- `run_export` — runs the SQL*Plus query for the selected business day.
- `write_control` — records the export’s row count and business day in the companion control file.

#### Data flow
- **Deployment:** CLI environment and tag → host list and `build/<tag>.tar.gz` → per-host upload, extraction, symlink update, and service restart → HTTP health polling.
- **Worker health:** action argument → PID/process check; for `health`, heartbeat age check → status message and action-specific exit code.
- **Payment export:** business-day and output-directory options → Oracle query → semicolon-separated CSV → row count and business-day `.ctl` file → gzipped CSV.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, `ssh`, and `curl`; deployment targets `/opt/releases` and `orderd`.
- `healthcheck.sh` manages `/usr/local/bin/queue-worker` using PID and heartbeat files; relies on `nohup`, `kill`, and `stat`.
- `nightly_extract.ksh` uses `sqlplus` to connect as `/ AS SYSDBA` and query Oracle table `settled_payment`; requires `gzip`.

#### Cross-cutting
- Scripts are directly executable shell jobs: Bash for deployment, POSIX `sh` for worker management, and ksh93 for payment export.
- Configuration is supplied through command-line options and environment variables, with defaults for deployment credentials, worker files, export directory, and Oracle SID.
- Operational failures are surfaced through stderr logging or status messages and distinct exit codes; deployment health polling and export retries are bounded, while worker checks have no retry or timeout.
