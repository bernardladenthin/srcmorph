### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:40Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release deployment, queue-worker health and lifecycle checks, and nightly export of settled payments for operational systems.

#### Purpose
- Provides command-line operations for deploying `orderd`, managing and monitoring the queue worker, and exporting Oracle payment data.
- Produces environment-specific rollouts, worker health signals, and compressed daily payment files with control counts.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, transfers and activates a tagged release, restarts `orderd`, and verifies each host before proceeding.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status or heartbeat health for `queue-worker`.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a business day, retries failed exports, and creates a gzipped CSV plus a control file.

#### Key units
- `deploy_host` — deploys a release archive to one host and restarts the service.
- `wait_healthy` — polls a host’s `/health` endpoint until success or timeout.
- `load_hosts` — loads the selected staging or production host list.
- `is_running` — checks the queue worker PID file and process liveness.
- `heartbeat_age` — calculates the age of the worker heartbeat.
- `start_worker` / `stop_worker` — launch or stop `queue-worker` and manage its PID file.
- `run_export` — queries Oracle’s `settled_payment` table for the selected business day.
- `write_control` — records the export row count and business day in the `.ctl` file.

#### Data flow
- Deployment: environment and tag select a host list and `build/$TAG.tar.gz`; hosts are processed sequentially through transfer, extraction, symlink update, service restart, and health verification. Dry-run mode logs actions instead.
- Worker: lifecycle commands use `PID_FILE` to track the process; health checks combine process liveness with heartbeat age.
- Export: business day and export directory configure the Oracle query; results become a semicolon-separated CSV, a row-count control file, and a compressed `.csv.gz` output.

#### Dependencies
- Deployment sources `lib/common.sh`; uses `hosts.staging` or `hosts.production`, SSH/rsync, `systemctl`, and `http://$host:8080/health`.
- Worker management invokes `/usr/local/bin/queue-worker` and uses configurable PID and heartbeat files.
- Payment export uses `sqlplus` with `CONNECT / AS SYSDBA` and reads Oracle table `settled_payment`.
- All scripts rely on external shell utilities; deployment and export also check required tools.

#### Cross-cutting
- Each script is directly executable; the package spans Bash, POSIX `sh`, and `ksh93`.
- Configuration is provided through command-line options and environment variables, with defaults for deployment credentials, worker file paths, Oracle SID, and export directory.
- Operational failures use script-specific exit statuses; deployment stops on the first failed host, while payment export retries up to three times.
