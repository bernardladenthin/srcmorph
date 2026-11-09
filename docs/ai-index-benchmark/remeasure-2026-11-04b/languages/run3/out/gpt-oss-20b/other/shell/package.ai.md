### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:45Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue‑worker service, and nightly payment extraction from Oracle.

#### Purpose
- Automate deployment of application releases to multiple hosts.
- Provide a POSIX‑style control interface for a queue‑worker daemon.
- Export settled payments nightly into CSV/CTL files with retry logic.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health polling across environments.
- **Service Management**: `healthcheck.sh` offers start/stop/status/health commands for a queue worker, tracking PID and heartbeat.
- **Data Extraction**: `nightly_extract.ksh` queries Oracle for settled payments, writes CSV/CTL, gzips output, and retries on failure.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – wrapper around `/usr/local/bin/queue-worker`, manages `/run/queue-worker.pid` and `/var/lib/queue-worker/heartbeat`.
- `nightly_extract.ksh` – runs `sqlplus` against `PAYPRD`, produces `payments_<YYYYMMDD>.csv`, `.ctl`, and gzipped archive.

#### Data flow
1. **Deployment**: script reads environment flag → loads host list → for each host: rsync tarball → SSH to extract → symlink → systemctl restart → curl health check.
2. **Service**: `start` writes PID, `stop` kills process, `status` checks PID and heartbeat age, `health` reports OK/CRITICAL.
3. **Extraction**: parse date & dir → ensure dir → retry loop: `sqlplus` dumps to CSV → write control file → gzip CSV.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- External files: `hosts.staging`, `hosts.production`, `$SCRIPT_DIR/lib/common.sh`.
- Oracle connection via `/ AS SYSDBA` and `PAYPRD` SID.

#### Cross‑cutting
- Logging via timestamped `log` functions.
- Error handling with `die`/`fail` and exit codes (0 success, 1–4 failures).
- Configuration via environment variables and defaults (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`, `ORACLE_SID_DEFAULT`).
- Retry logic in `nightly_extract.ksh` (max 3 attempts, 60‑second backoff).
