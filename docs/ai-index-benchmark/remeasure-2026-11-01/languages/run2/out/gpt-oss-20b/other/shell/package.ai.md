### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:41:03Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and exporting daily payment data from Oracle.

#### Purpose
- Deploy release tarballs to multiple hosts and verify service health.
- Control, monitor, and health‑check a queue worker process.
- Export settled payments to a gzipped CSV with a control file.

#### Responsibilities
- **Deployment**: orchestrate tarball transfer, extraction, symlink update, service restart, and health polling.
- **Process Management**: start/stop/restart a worker, track PID, and evaluate heartbeat freshness.
- **Data Export**: query Oracle for settled payments, write CSV, gzip, and generate a control file with row count.

#### Key units
- `deploy_release.sh` – orchestrates remote deployment and health verification.
- `healthcheck.sh` – POSIX wrapper for queue worker lifecycle and health status.
- `nightly_extract.ksh` – KSH script that queries Oracle, writes CSV, gzips, and creates a control file.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads host list → rsync tarball → SSH remote commands → service restart → health check via `curl`.
2. **Worker Management**: `healthcheck.sh` writes/reads PID and heartbeat files; `health` command compares heartbeat age to `MAX_HEARTBEAT_AGE`.
3. **Export**: `nightly_extract.ksh` builds query → runs `sqlplus` → writes CSV → gzips → writes control file.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- **Remote environment**: remote hosts run `bash`, `mkdir`, `tar`, `ln`, `sudo systemctl`.
- **Files**: host lists (`hosts.staging`, `hosts.production`), worker binary (`/usr/local/bin/queue-worker`).

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deployment, exit codes in all scripts).
- Timestamped logging via `log` functions.
- Configuration via environment variables and defaults (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Retry logic in `nightly_extract.ksh` (max 3 attempts with sleep).
