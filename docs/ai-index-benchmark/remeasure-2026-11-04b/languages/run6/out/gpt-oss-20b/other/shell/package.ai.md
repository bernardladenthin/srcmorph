### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:03Z
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
- Control a queue worker process with start/stop/status/health checks.
- Export settled payments from Oracle to CSV, generate control files, and compress output.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles distribution, extraction, service restart, and health polling.
- **Process Management**: `healthcheck.sh` manages a queue worker via PID/heartbeat files.
- **Data Extraction**: `nightly_extract.ksh` runs nightly Oracle exports, writes control metadata, and gzips results.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – PID/heartbeat‑based worker controller (`/usr/local/bin/queue-worker`).
- `nightly_extract.ksh` – Oracle export driver (`sqlplus`), gzip compressor, retry loop.

#### Data flow
1. **Deployment**: script receives environment and tag → loads host list → rsync tarball → SSH to host → extract → symlink `current` → systemctl restart → curl health endpoint.
2. **Healthcheck**: start → writes PID → worker writes heartbeat → health command reads heartbeat age → reports status.
3. **Extraction**: script parses date and output dir → runs SQL query via `sqlplus` → writes CSV → counts rows → writes control file → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `cat`, `sqlplus`, `gzip`.
- Remote hosts defined in `hosts.staging` / `hosts.production`.
- Oracle database (`CONNECT / AS SYSDBA`).
- Queue worker binary `/usr/local/bin/queue-worker`.

#### Cross‑cutting
- Strict error handling (`set -euo pipefail` in deploy script; explicit exit codes elsewhere).
- Timestamped logging via `log` functions.
- Configuration via environment variables and defaults (e.g., `DEPLOY_USER`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Retry logic in nightly extraction (max 3 attempts with sleep).
- Health checks use timeouts (`HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).
