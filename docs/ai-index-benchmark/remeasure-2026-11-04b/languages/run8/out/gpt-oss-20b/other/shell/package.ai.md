### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:48Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and nightly data extraction for a payment processing service.

#### Purpose
- Deploy release archives to multiple hosts, restart services, and verify health.
- Control and report the lifecycle of a queue‑worker process.
- Export settled payments from Oracle to CSV, generate control files, and compress output.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles distribution of release tarballs, symlink updates, service restarts, and health polling.
- **Process Management**: `healthcheck.sh` provides start/stop/status/health commands for a queue worker, using PID and heartbeat files.
- **Data Export**: `nightly_extract.ksh` extracts settled payments, writes CSV and control files, gzips results, and retries on failure.

#### Key units
- `deploy_release.sh` – Bash script orchestrating host‑by‑host deployment.
- `healthcheck.sh` – POSIX shell wrapper for queue‑worker lifecycle and health checks.
- `nightly_extract.ksh` – Korn shell script exporting Oracle data to CSV and control files.

#### Data flow
1. **Deployment**:  
   `deploy_release.sh` reads a host list → rsyncs `build/$TAG.tar.gz` → SSH to host → extracts → updates `$RELEASE_ROOT/current` → restarts `orderd` → polls `http://$host:8080/health`.
2. **Health Check**:  
   `healthcheck.sh` checks PID file → verifies process liveness → reads heartbeat file → reports status or exits with error codes.
3. **Data Export**:  
   `nightly_extract.ksh` runs `sqlplus` to dump `settled_payment` rows → writes CSV → creates `.ctl` with row count → gzips CSV → logs progress.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Files: host lists (`hosts.staging`, `hosts.production`), release archives (`build/*.tar.gz`), Oracle tables (`settled_payment`).
- Environment variables: `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`, `PID_FILE`, `HEARTBEAT_FILE`.

#### Cross‑cutting
- **Logging**: All scripts use a `log` function that prefixes messages with timestamps.
- **Error handling**: `die`/`fail` functions exit with specific codes; scripts trap `EXIT` for cleanup.
- **Configuration**: Constants are defined at the top of each script (e.g., `RELEASE_ROOT`, `SERVICE_NAME`, `MAX_HEARTBEAT_AGE`, `MAX_RETRIES`).
- **Process control**: PID files and heartbeat files coordinate worker status across scripts.
