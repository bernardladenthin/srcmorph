### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:33:48Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates service release deployment, queue-worker lifecycle and health checks, and nightly export of settled payments.

#### Purpose
- Provides command-line operations for rolling tagged releases across staging or production hosts and managing queue-worker availability.
- Produces a dated, compressed CSV extract of settled Oracle payments with a row-count control file.

#### Responsibilities
- **Release rollout:** `deploy_release.sh` reads environment-specific host lists, deploys a tagged archive sequentially, restarts `orderd`, and verifies host health; supports dry-run mode.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status for `queue-worker`; its health check detects stopped workers and stale heartbeats.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a business day, retries failed exports, and writes a gzipped CSV plus control file.

#### Key units
- `deploy_release.sh` — coordinates host selection, remote release installation, service restart, and health polling.
- `healthcheck.sh` — implements `start`, `stop`, `restart`, `status`, and `health` operations for the queue worker.
- `is_running` — checks the worker PID file and process.
- `heartbeat_age` — calculates the age of the worker heartbeat.
- `start_worker` / `stop_worker` — launch or terminate the worker and maintain its PID file.
- `nightly_extract.ksh` — drives the business-day Oracle extract and retry loop.
- `run_export` — queries `settled_payment` for payment ID, account number, amount, and currency.
- `write_control` — writes the CSV row count and business day to the control file.

#### Data flow
- **Deployment:** environment and tag options select a host file and `build/$TAG.tar.gz`; each host receives and extracts the archive, switches its `current` link, restarts `orderd`, then undergoes health polling. Processing stops at the first failure.
- **Worker:** a command selects the lifecycle or health operation; worker state comes from the PID file, while `health` compares the heartbeat age against the configured limit.
- **Payment export:** date and output-directory options configure the Oracle query; successful output is counted, recorded in a control file, and compressed as `.csv.gz`. Failed queries are retried up to three times.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`; uses `hosts.staging` or `hosts.production`, `rsync`, `ssh`, `curl`, and remote `tar`, `ln`, and `systemctl`; checks `http://$host:8080/health`.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker` and uses the PID and heartbeat files, defaulting to `/run/queue-worker.pid` and `/var/lib/queue-worker/heartbeat`.
- `nightly_extract.ksh` uses `sqlplus` to connect as SYSDBA and query Oracle’s `settled_payment` table; uses `gzip` for compression.

#### Cross-cutting
- Scripts run directly as command-line tools; `deploy_release.sh` uses Bash strict mode, while the worker script uses POSIX `sh` and the extract uses `ksh93` without strict-mode settings.
- `deploy_release.sh` and `nightly_extract.ksh` emit timestamped logs to standard error; failures use script-specific exit statuses.
- Operational controls are configurable through command options and environment variables, including deployment credentials, worker file paths, Oracle SID, business date, and export directory.
