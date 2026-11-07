### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:10Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities that deploy releases, manage a queue worker, and export nightly payment data.

#### Purpose
- Deploy application releases to multiple hosts.
- Control a long‑running queue worker process.
- Export settled payments from Oracle to CSV.

#### Responsibilities
- **Deployment**: transfer tarballs, update symlinks, restart services, health‑check hosts.
- **Process Management**: start/stop/restart a worker, track PID, monitor heartbeat.
- **Data Extraction**: run Oracle queries, generate CSV/CTL, gzip output, retry on failure.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, health verification, and service restart.
- `healthcheck.sh` – init‑style wrapper for a queue worker, handling PID/heartbeat files and status reporting.
- `nightly_extract.ksh` – nightly Oracle export script, producing CSV, control file, and gzipped archive.

#### Data flow
1. **Deploy**: `deploy_release.sh` reads an environment‑specific host list, rsyncs a tarball, SSH‑executes extraction, updates `$RELEASE_ROOT/current`, restarts `orderd`, and polls `/health`.
2. **Worker**: `healthcheck.sh` starts `/usr/local/bin/queue-worker` with `nohup`, writes its PID, and writes a heartbeat file; health checks compare heartbeat age to `MAX_HEARTBEAT_AGE`.
3. **Export**: `nightly_extract.ksh` runs a SQL query via `sqlplus`, writes results to a CSV, counts rows for a control file, gzips the CSV, and retries on transient failures.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Host list files: `hosts.staging`, `hosts.production`.
- Oracle connection: `/ AS SYSDBA` with `ORACLE_SID`.
- Worker binary: `/usr/local/bin/queue-worker`.

#### Cross‑cutting
- Strict error handling in `deploy_release.sh` (`set -euo pipefail`).
- Common logging function `log` in all scripts.
- Use of environment variables for configuration (e.g., `DEPLOY_USER`, `PID_FILE`, `ORACLE_SID`).
- Retry logic in `nightly_extract.ksh` with exponential back‑off via `sleep 60`.
