### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:35:35Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and daily payment export for a distributed order‑processing system.

#### Purpose
- Distribute release tarballs to multiple hosts, restart services, and verify health.
- Control a queue‑worker daemon and expose a simple health probe.
- Export settled payments from Oracle to a gzipped CSV with a control file.

#### Responsibilities
- **Deployment** – orchestrate tarball transfer, extraction, symlink update, service restart, and health polling across environments.
- **Process Management** – start/stop/restart a queue worker, track its PID, and report health via heartbeat age.
- **Data Export** – run an Oracle query, spool results to CSV, gzip, and generate a control file with row count and business day.

#### Key units
- `deploy_release.sh` – main deployment driver; functions: `deploy_host`, `wait_healthy`, `check_prereqs`, `load_hosts`.
- `healthcheck.sh` – daemon controller; functions: `start_worker`, `stop_worker`, `is_running`, `heartbeat_age`.
- `nightly_extract.ksh` – payment exporter; functions: `run_export`, `write_control`, `require_tools`, `log`, `fail`.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads an environment‑specific host list → for each host, rsync tarball → SSH to extract → update `/opt/releases/current` → restart `orderd` → poll `http://host:8080/health` until success or timeout.
2. **Health**: `healthcheck.sh` starts `queue-worker` in background, writes PID → monitors heartbeat file age to report `OK`, `WARNING`, or `CRITICAL`.
3. **Export**: `nightly_extract.ksh` runs `sqlplus` to spool query → counts rows → writes control file → gzips CSV → exits with status.

#### Dependencies
- **External commands**: `rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `sqlplus`, `gzip`, `wc`, `date`, `stat`.
- **Environment**: `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`.
- **Files**: `hosts.staging`, `hosts.production`, `/opt/releases`, `/run/queue-worker.pid`, `/var/lib/queue-worker/heartbeat`, `/data/export/payments`.

#### Cross-cutting
- **Logging**: timestamped stderr output in all scripts.
- **Error handling**: `die`/`fail` functions exit with descriptive messages and non‑zero codes.
- **Configuration defaults**: paths, timeouts, retry counts, heartbeat age limits are defined as constants.
- **Process safety**: PID files guard against duplicate workers; health checks enforce service availability before proceeding.
