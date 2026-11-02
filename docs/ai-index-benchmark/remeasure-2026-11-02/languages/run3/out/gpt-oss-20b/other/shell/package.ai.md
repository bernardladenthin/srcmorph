### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:56:35Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment exports.

#### Purpose
- Deploy release tarballs to multiple hosts with health verification.
- Control a queue worker process (start/stop/status/health).
- Export settled payments from Oracle to CSV/CTL and compress.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` provides a POSIX init‑style interface for a queue worker, tracking PID and heartbeat.
- **Data Export**: `nightly_extract.ksh` extracts settled payments, writes control files, and gzips the output.

#### Key units
- `deploy_release.sh` – orchestrates remote deployment via `rsync`, `ssh`, and `curl`.
- `healthcheck.sh` – exposes `start|stop|restart|status|health` commands for a worker binary.
- `nightly_extract.ksh` – runs `sqlplus` to dump payments, creates `.ctl`, and gzips the CSV.

#### Data flow
1. **Deployment**: script → host list → `rsync` tarball → SSH → extraction → symlink → `systemctl restart` → `curl` health poll.
2. **Worker**: `start` writes PID → worker writes heartbeat → `health` checks PID and heartbeat age.
3. **Export**: script → `sqlplus` query → CSV → control file → `gzip` → final artifacts.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- Files: host lists (`hosts.staging`, `hosts.production`), release tarballs (`build/*.tar.gz`), worker binary (`/usr/local/bin/queue-worker`).
- Environment variables: `DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`, `NLS_LANG`.

#### Cross-cutting
- Strict error handling in `deploy_release.sh` (`set -euo pipefail`); POSIX compliance in `healthcheck.sh`; retry logic in `nightly_extract.ksh`.
- Common logging pattern (`log` function) across scripts.
- Use of temporary lock files and cleanup hooks in deployment.
- Health checks rely on heartbeat file timestamps and PID verification.
