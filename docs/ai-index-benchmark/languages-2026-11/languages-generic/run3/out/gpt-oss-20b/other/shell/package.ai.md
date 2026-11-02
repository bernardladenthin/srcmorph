### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:41:23Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release deployment, monitors queue worker health, and exports daily settled payments from Oracle.

#### Purpose
- Deploy built releases to multiple servers, restart services, and verify health.
- Control and monitor a queue worker’s lifecycle and heartbeat.
- Generate daily CSV exports of settled payments from Oracle.

#### Responsibilities
- **Release Deployment** – `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health polling.
- **Worker Lifecycle & Health** – `healthcheck.sh` starts/stops the queue worker, tracks its PID, and evaluates heartbeat age for status reporting.
- **Payment Export** – `nightly_extract.ksh` runs an Oracle query, writes a CSV, gzips it, and creates a control file with row count and business day.

#### Key units
- `deploy_release.sh` – script; functions: `deploy_host`, `wait_healthy`, `load_hosts`.
- `healthcheck.sh` – script; functions: `start_worker`, `stop_worker`, `is_running`, `heartbeat_age`.
- `nightly_extract.ksh` – script; functions: `run_export`, `write_control`, `require_tools`.

#### Data flow
- **Deployment**: tag & env → host list → rsync tarball → SSH extraction → symlink → systemctl restart → curl health check.
- **Health**: command → start/stop worker → PID file → heartbeat file → age calculation → status code.
- **Export**: date & dir → sqlplus query → CSV → gzip → control file → log.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `sqlplus`, `gzip`, `date`, `stat`, `cat`.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`, `NLS_LANG`.
- Files: host lists (`hosts.staging`, `hosts.production`), worker binary (`/usr/local/bin/queue-worker`).

#### Cross-cutting
- Consistent error handling via `die()`/`fail()` and exit codes.
- Logging to stderr with timestamps.
- Use of constants for paths, timeouts, and retry limits.
- Configuration driven by environment variables and default values.
