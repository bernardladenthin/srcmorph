### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:54:47Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates application release deployment, queue-worker health management, and nightly Oracle payment exports.

#### Purpose
- Deploy tagged application builds to staging or production hosts and verify service health.
- Operate the queue worker and export settled payments as compressed daily CSV files with row-count controls.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects environment hosts, deploys `build/<tag>.tar.gz` sequentially, restarts `orderd`, and halts on deployment or health-check failure.
- **Worker lifecycle and monitoring:** `healthcheck.sh` starts, stops, restarts, or reports the status and heartbeat health of the queue worker.
- **Payment extraction:** `nightly_extract.ksh` queries Oracle for a business day’s settled payments, retries failures, and produces a gzipped CSV plus control file.

#### Key units
- `deploy_host` — transfers and installs a release on one host, restarts `orderd`, and waits for its health endpoint.
- `wait_healthy` — polls `http://<host>:8080/health` up to `HEALTH_TIMEOUT`.
- `load_hosts` — loads non-comment host entries from the selected environment’s host file.
- `is_running` — determines whether the worker PID file references a live process.
- `start_worker` / `stop_worker` — launch the queue worker with its heartbeat path or stop it and remove its PID file.
- `heartbeat_age` — calculates the age of the worker heartbeat.
- `run_export` — invokes Oracle SQL*Plus to write settled payment fields for the requested business day.
- `write_control` — records the CSV row count and business day in the neighboring `.ctl` file.

#### Data flow
- `deploy_release.sh`: environment and tag → host list and build archive → per-host transfer, extraction, symlink update, and service restart → HTTP health checks.
- `healthcheck.sh`: action and PID/heartbeat files → worker process lifecycle or health evaluation → status message and action-specific exit code.
- `nightly_extract.ksh`: business day and output directory → Oracle `settled_payment` query → CSV row count and control file → gzip-compressed CSV.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses SSH/rsync plus remote shell and systemd commands.
- Deployment relies on `build/<tag>.tar.gz`, a readable SSH key, and the `orderd` service; its health probe uses `curl`.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker` with `--heartbeat`; PID and heartbeat paths are configurable through `PID_FILE` and `HEARTBEAT_FILE`.
- `nightly_extract.ksh` uses `sqlplus` with `/ AS SYSDBA`, `ORACLE_SID`, and `NLS_LANG`; it reads `settled_payment` fields `payment_id`, `account_no`, `amount`, and `currency`.

#### Cross-cutting
- The scripts are directly executable operational entry points; deployment uses Bash strict mode, while the worker and export scripts do not.
- Deployment and export log timestamped messages to stderr; the worker script reports action and health status directly.
- Environment variables and command-line options configure deployment environment/tag, worker file paths, and export date/output directory.
- Failure signaling uses explicit, script-specific exit statuses; deployment stops at the first host failure, while payment extraction retries up to three times with 60-second sleeps.
