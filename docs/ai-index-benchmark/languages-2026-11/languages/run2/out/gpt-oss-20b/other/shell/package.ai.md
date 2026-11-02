### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:35:26Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment exports in a production environment.

#### Purpose
- Deploy application releases to multiple hosts.
- Control a background queue worker process.
- Export settled payments from Oracle to CSV.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink management, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` starts, stops, restarts, reports status, and health‑checks a queue worker via PID and heartbeat files.
- **Data Export**: `nightly_extract.ksh` pulls settled payments from Oracle, writes CSV and control files, and gzips the output with retry logic.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – exposes `start`, `stop`, `restart`, `status`, `health` commands for the queue worker binary `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – runs `sqlplus` to spool payments, writes `payments_YYYYMMDD.csv`, `payments_YYYYMMDD.ctl`, and gzips to `payments_YYYYMMDD.csv.gz`.

#### Data flow
1. **Deployment**: script reads environment flag → loads host list → for each host: rsync tarball → SSH to extract → symlink `current` → systemctl restart → curl health endpoint until success.
2. **Worker control**: commands manipulate PID file `/run/queue-worker.pid` and heartbeat file `/var/lib/queue-worker/heartbeat`; health checks compare heartbeat age to `MAX_HEARTBEAT_AGE`.
3. **Export**: script parses date and output dir → ensures dir exists → runs `sqlplus` to spool data → writes control file with row count → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- External scripts: `lib/common.sh` (shared utilities for deployment).
- Configuration files: `hosts.staging`, `hosts.production`.
- Oracle connection via `/ AS SYSDBA`.

#### Cross-cutting
- Strict mode (`set -euo pipefail`) in deployment script; POSIX sh in healthcheck; no strict mode in nightly export.
- Common logging pattern: timestamped `log` function writing to stderr.
- Error handling via `die`/`fail` functions with exit codes 1–4.
- Use of environment variables for defaults (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Retry logic in nightly export (max 3 attempts with 60s sleep).
