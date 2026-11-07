### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:01Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and nightly data extraction for a payment processing service.

#### Purpose
- Deploy release tarballs to multiple hosts, restart services, and verify health.
- Control a queue‑worker process with start/stop/status/health commands.
- Export settled payments from Oracle to CSV, create control files, and compress them.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles distribution, extraction, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` provides a POSIX init‑style interface for a queue worker, managing PID, heartbeat, and status.
- **Data Export**: `nightly_extract.ksh` extracts settled payments, writes CSV/CTL files, gzips output, and retries on failure.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – exposes `start|stop|restart|status|health`; uses `nohup`, `kill`, `stat`.
- `nightly_extract.ksh` – runs `sqlplus` to dump `settled_payment`, writes control file, gzips CSV, retries up to 3 times.

#### Data flow
1. **Deployment**: script parses `-e`/`-t`, loads host list, `rsync` tarball → SSH → extract → symlink → `systemctl restart` → `curl` health check.
2. **Healthcheck**: `start` writes PID, worker writes heartbeat; `health` reads heartbeat age, reports status.
3. **Export**: parse `-d`/`-o`, ensure dir, run `sqlplus` query → CSV → control file → `gzip`.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `stat`, `sqlplus`, `gzip`.
- Host list files: `hosts.staging`, `hosts.production`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat`).
- Oracle connection via `sqlplus` (SYSDBA, `/`).

#### Cross‑cutting
- Common logging pattern: timestamped messages to stderr (`log` function).
- Error handling via `die`/`fail` functions with exit codes.
- Configuration via environment variables and defaults (`DEPLOY_USER`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Retry logic in `nightly_extract.ksh` (max 3 attempts with sleep).
- Health check timeout constants (`HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).
- Use of `set -euo pipefail` in deployment script for strict error handling.
