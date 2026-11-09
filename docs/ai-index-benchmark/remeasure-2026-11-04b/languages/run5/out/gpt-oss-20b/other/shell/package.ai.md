### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:57Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction.

#### Purpose
- Deploy and verify application releases across hosts.
- Control a queue worker process with health monitoring.
- Export settled payments from Oracle to CSV for downstream processing.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health checks.
- **Process Management**: `healthcheck.sh` provides start/stop/status/health commands for a queue worker, tracking PID and heartbeat.
- **Data Extraction**: `nightly_extract.ksh` queries Oracle for settled payments, writes CSV, creates control files, and gzips output.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment and health polling.
- `healthcheck.sh` – PID/heartbeat‑based worker controller.
- `nightly_extract.ksh` – Oracle export driver, retry logic, control file generator.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads host list → rsync tarball → SSH → extract → symlink → `systemctl restart` → poll `/health` → log status.
2. **Worker**: `healthcheck.sh` starts `queue-worker` with `nohup`, writes PID, writes heartbeat via worker binary; health checks read heartbeat age.
3. **Extraction**: `nightly_extract.ksh` parses date → runs `sqlplus` query → spools CSV → counts rows → writes control file → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Remote resources: `/opt/releases`, `/run/queue-worker.pid`, `/var/lib/queue-worker/heartbeat`, Oracle `PAYPRD` database.
- Host configuration files: `hosts.staging`, `hosts.production`.

#### Cross-cutting
- **Logging**: All scripts use timestamped `stderr` logs via a `log` function.
- **Error handling**: Each script exits with specific codes for missing tools, invalid options, or runtime failures.
- **Configuration**: Default paths and environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`) are centralized per script.
- **Retry logic**: `nightly_extract.ksh` implements up to 3 retries with sleep; `deploy_release.sh` does not retry but logs failures.
