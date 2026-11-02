### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:26:00Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction from Oracle.

#### Purpose
- Deploy application releases to multiple hosts.
- Control a long‑running queue worker process.
- Export settled payments nightly into CSV/CTL files.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball distribution, extraction, service restart, and health verification across environments.
- **Process Management**: `healthcheck.sh` provides start/stop/status/health commands for a queue worker, using PID and heartbeat files.
- **Data Export**: `nightly_extract.ksh` extracts settled payments for a given business day, writes CSV and control files, and gzips the output.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – PID/heartbeat‑based worker controller, interacts with `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – Oracle export driver, relies on `sqlplus` and `gzip`, produces `payments_<date>.csv.gz` and `<csv>.ctl`.

#### Data flow
1. **Deployment**: local tarball → `rsync` to `/tmp/` on host → remote extraction to `$RELEASE_ROOT/$TAG` → symlink update → `systemctl restart orderd` → `curl` health check.
2. **Worker**: `start` writes PID, worker writes heartbeat; `health` reads heartbeat age; `stop` kills PID.
3. **Export**: `run_export` runs SQL*Plus query → CSV → control file → gzip → completion.

#### Dependencies
- External binaries: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- Remote services: `systemctl` on target hosts, `http://$host:8080/health`.
- Files: host list files (`hosts.staging`, `hosts.production`), release tarballs (`build/$TAG.tar.gz`), worker binary (`/usr/local/bin/queue-worker`).

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deploy script, `set -e` in ksh).
- Timestamped logging via `log` functions.
- Configuration via environment variables and defaults (e.g., `DEPLOY_USER`, `PID_FILE`, `ORACLE_SID`).
- Retry logic in `nightly_extract.ksh` (max 3 attempts).
- Health checks with timeouts (`HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).
