### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:59Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates service release deployment and health monitoring, queue-worker lifecycle checks, and nightly Oracle payment exports for operational reporting.

#### Purpose
- Provides command-line operations for rolling out `orderd`, managing the queue worker, and producing settlement payment extracts.
- Handles deployment validation, process and heartbeat checks, and export retry/control-file generation.

#### Responsibilities
- **Release deployment (`deploy_release.sh`):** Select staging or production hosts, transfer and install a tagged archive, restart `orderd`, and halt rollout if host health checks fail.
- **Worker operations (`healthcheck.sh`):** Start, stop, restart, and report status for the queue worker; assess health using its heartbeat age.
- **Settlement reporting (`nightly_extract.ksh`):** Query Oracle `settled_payment` for a business day and produce a gzipped CSV with a row-count control file.

#### Key units
- `deploy_host` — Transfers and installs a release on one host, then restarts the service.
- `wait_healthy` — Polls a host’s `/health` endpoint until success or timeout.
- `load_hosts` — Loads the host list for the selected deployment environment.
- `is_running` — Determines whether the PID-file process is live.
- `heartbeat_age` — Computes the worker heartbeat’s age in seconds.
- `start_worker` / `stop_worker` — Launch or terminate the worker and maintain its PID file.
- `run_export` — Runs the Oracle query and writes CSV output.
- `write_control` — Writes the export row count and business day to the `.ctl` file.

#### Data flow
- **Deployment:** Environment and tag options → prerequisite and host-list checks → archive transfer and remote extraction → `current` release update and `orderd` restart → per-host HTTP health polling.
- **Worker:** Operation argument → PID/process check or lifecycle action; `health` additionally compares heartbeat-file age with the 120-second limit.
- **Payment export:** Business day and export directory → Oracle query → CSV line count and control file → gzip compression; failed exports are retried up to three times.

#### Dependencies
- Deployment uses `lib/common.sh`, staging/production host-list files, `rsync`, `ssh`, `curl`, and remote `orderd` service commands.
- Worker management uses `/usr/local/bin/queue-worker`, PID and heartbeat files, and standard process/file utilities.
- Payment extraction uses `sqlplus` with `/ AS SYSDBA`, the Oracle `settled_payment` table, and `gzip`.

#### Cross-cutting
- Each script is a directly invoked shell command; the package spans Bash, POSIX `sh`, and `ksh93`.
- Deployment and extraction emit timestamped logs; deployment uses Bash strict mode, while the other scripts specify no strict mode.
- Failure policies differ by task: deployment stops on failed host health checks, worker health returns distinct status codes, and extraction retries failed exports before returning an exhausted-retries code.
