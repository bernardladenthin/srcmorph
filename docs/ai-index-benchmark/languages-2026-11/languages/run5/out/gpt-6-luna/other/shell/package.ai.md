### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:34:57Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Supports payment operations by deploying tagged service releases, monitoring and controlling a queue worker, and exporting settled payments for a business day.

#### Purpose
- Provides shell utilities for release rollout, worker lifecycle and health checks, and nightly Oracle payment exports.
- Operates directly against configured hosts, local worker files, and Oracle data.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, deploys a tagged build, restarts `orderd`, and checks host health before proceeding.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status or heartbeat health for `queue-worker`.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a selected business day and creates a compressed CSV with a row-count control file.

#### Key units
- `deploy_release.sh` — host-by-host deployment, health polling, dry-run support, and cleanup.
- `healthcheck.sh` — init-style worker controls and heartbeat freshness check.
- `nightly_extract.ksh` — retried Oracle export and CSV/control-file generation.
- `is_running`, `heartbeat_age`, `start_worker`, `stop_worker` — worker process and heartbeat helpers.
- `check_prereqs`, `load_hosts`, `deploy_host`, `wait_healthy`, `cleanup` — deployment validation, host selection, rollout, health checks, and exit cleanup.
- `require_tools`, `run_export`, `write_control` — export prerequisites, query execution, and control-file creation.

#### Data flow
- **Deployment:** options and environment select a host list and `build/$TAG.tar.gz`; each host receives the release over SSH, updates its `current` symlink, restarts `orderd`, and is polled at `/health` before the next host.
- **Worker health:** the PID file determines whether the worker is running; health operations compare heartbeat-file age with the 120-second limit.
- **Payment export:** the business-day filter selects rows from Oracle `settled_payment`; results are written as CSV, counted into a `.ctl` file, and compressed to `.csv.gz`, with up to three export attempts.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses SSH/`rsync`, `curl`, and remote systemd commands.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker` and uses PID and heartbeat files, configurable through `PID_FILE` and `HEARTBEAT_FILE`.
- `nightly_extract.ksh` uses `sqlplus` with `ORACLE_SID` and queries `settled_payment` columns `payment_id`, `account_no`, `amount`, and `currency`.

#### Cross-cutting
- Scripts are standalone shell utilities: Bash for deployment, POSIX `sh` for worker control, and `ksh93` for export.
- Configuration is supplied through command-line options and environment variables, with defaults for deployment credentials, worker paths, Oracle SID, and export directory.
- Failure behavior is script-specific: deployment stops on detected errors, worker operations use distinct health/status exit codes, and exports retry failed queries before reporting failure.
