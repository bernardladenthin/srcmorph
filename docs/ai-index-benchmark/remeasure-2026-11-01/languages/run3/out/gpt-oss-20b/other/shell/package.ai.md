### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:58:07Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and exporting daily payment data from Oracle.

#### Purpose
- Deploy application releases to multiple hosts.
- Control and monitor a queue worker process.
- Export settled payments to a compressed CSV with a control file.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink creation, service restart, and health verification across environments.
- **Worker Management**: `healthcheck.sh` provides start/stop/restart/status/health commands for a queue worker, using PID and heartbeat files.
- **Data Export**: `nightly_extract.ksh` generates daily payment exports from Oracle, gzips the CSV, and writes a row‑count control file.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`, and `systemctl`.
- `healthcheck.sh` – exposes `start`, `stop`, `restart`, `status`, `health` commands; relies on `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – runs a SQL query against `settled_payment`, writes CSV, gzips, and creates a `.ctl` file.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads host lists → rsync tarball → SSH → extract → symlink → restart `orderd` → poll `/health`.
2. **Worker**: `healthcheck.sh` starts `queue-worker` with `--heartbeat`; health checks read heartbeat file age.
3. **Export**: `nightly_extract.ksh` runs `sqlplus` query → writes CSV → counts rows → writes control file → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.
- External files: `deploy_release.sh` sources `lib/common.sh`; host lists `hosts.staging`/`hosts.production`.
- External binaries: `/usr/local/bin/queue-worker`.
- Oracle database: `settled_payment` table accessed via `sqlplus`.

#### Cross-cutting
- **Logging**: All scripts use timestamped `stderr` logs via a `log` function.
- **Error handling**: `die`/`exit` patterns with specific exit codes for usage, missing tools, and health failures.
- **Configuration**: Environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`) provide defaults and override points.
- **Retry logic**: `nightly_extract.ksh` implements a retry loop with exponential back‑off for transient failures.
