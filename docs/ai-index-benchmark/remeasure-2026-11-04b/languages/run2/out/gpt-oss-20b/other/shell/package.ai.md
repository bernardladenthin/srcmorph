### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:29Z
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
- Control and monitor a queue worker process.
- Export settled payments nightly to CSV for downstream processing.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health verification across environments.
- **Process Management**: `healthcheck.sh` starts, stops, restarts, reports status, and performs heartbeat‑based health checks for a queue worker.
- **Data Extraction**: `nightly_extract.ksh` pulls settled payments from Oracle, writes CSV and control files, and gzips the output.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`, and `systemctl`.
- `healthcheck.sh` – PID/heartbeat watchdog for `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – Oracle export driver, writes `payments_<day>.csv`, `payments_<day>.ctl`, and gzips the CSV.

#### Data flow
1. **Deployment**: script reads environment‑specific host list → rsync tarball → SSH remote extraction → symlink `current` → `systemctl restart` → `curl` health check.
2. **Healthcheck**: command dispatch → PID file checks → heartbeat file age → status output.
3. **Extraction**: parse date/dir → `sqlplus` query → spool to CSV → count rows → write control file → `gzip` CSV.

#### Dependencies
- External binaries: `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `nohup`, `kill`.
- External files: host lists (`hosts.staging`, `hosts.production`), worker binary (`/usr/local/bin/queue-worker`).
- Environment variables: `DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`.

#### Cross-cutting
- Logging via `log` functions with timestamps.
- Strict error handling: `set -euo pipefail` in deployment, explicit exit codes in healthcheck, retry logic in extraction.
- Configuration via environment variables and defaults.
- Use of `getopts` for option parsing across scripts.
