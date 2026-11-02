### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:18:50Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and exporting payment data from Oracle.

#### Purpose
- Deploy release tarballs to multiple hosts and verify service health.
- Control a queue worker process with start/stop/status/health commands.
- Export settled payments to a gzipped CSV with a row‑count control file.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health polling.
- **Process Management**: `healthcheck.sh` provides a POSIX‑style interface to start, stop, restart, and health‑check a queue worker.
- **Data Export**: `nightly_extract.ksh` runs an Oracle query, writes CSV output, gzips it, and creates a control file with row count.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment and health verification.
- `healthcheck.sh` – wrapper around `/usr/local/bin/queue-worker` with heartbeat monitoring.
- `nightly_extract.ksh` – script that spools `settled_payment` rows to CSV via `sqlplus`.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads a host list, rsyncs `build/$TAG.tar.gz` to each host, SSH‑executes extraction and symlink updates, restarts `orderd`, then polls `http://$host:8080/health`.
2. **Worker Control**: `healthcheck.sh` writes/reads a PID file and heartbeat file; `health` checks process existence and heartbeat age.
3. **Export**: `nightly_extract.ksh` runs a SQL query through `sqlplus`, writes results to a CSV, counts rows, writes a `.ctl` file, and gzips the CSV.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- **Files**: host list files (`hosts.staging`, `hosts.production`), release tarballs (`build/$TAG.tar.gz`), worker binary (`/usr/local/bin/queue-worker`).
- **Environment**: `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`, `PID_FILE`, `HEARTBEAT_FILE`.

#### Cross-cutting
- **Logging**: All scripts use timestamped `stderr` logs via a `log` function.
- **Error handling**: Each script exits with specific codes for missing tools, invalid options, or runtime failures.
- **Configuration**: Default values are set via environment variables or hard‑coded constants; options override defaults.
- **Retry logic**: `nightly_extract.ksh` retries the export up to `MAX_RETRIES` times before giving up.
