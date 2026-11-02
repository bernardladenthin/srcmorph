### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:42:11Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction from Oracle.

#### Purpose
- Deploy release tarballs to multiple hosts and verify service health.
- Control a queue worker process and expose its health status.
- Export settled payments nightly, produce CSV and control files, and compress the output.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball distribution, extraction, symlink updates, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` starts, stops, restarts, reports status, and monitors heartbeat of a queue worker.
- **Data Export**: `nightly_extract.ksh` queries Oracle for settled payments, writes CSV, creates a control file, and gzips the result with retry logic.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – POSIX wrapper exposing `start|stop|restart|status|health` commands for a worker binary.
- `nightly_extract.ksh` – KSH script that runs `sqlplus` to dump `settled_payment` rows, writes control metadata, and compresses the CSV.

#### Data flow
1. **Deployment**: script reads environment‑specific host file → rsync tarball → SSH to host → extract → symlink → `systemctl restart` → `curl` health poll → log success.
2. **Healthcheck**: command dispatch → start/stop worker via `nohup` → PID file → heartbeat file → health command reads heartbeat age → outputs status.
3. **Nightly Extract**: parse date and output dir → ensure dir → retry loop → `sqlplus` export → write control file → `gzip` CSV → log completion.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- System services: `orderd` (restarted by deployment), queue worker binary `/usr/local/bin/queue-worker`.
- Files: `build/<tag>.tar.gz`, `hosts.staging`, `hosts.production`, Oracle tables `settled_payment`.

#### Cross-cutting
- Logging with timestamps (`log` functions) across all scripts.
- Error handling via `die`/`fail` functions, consistent exit codes.
- Configuration via environment variables and defaults (e.g., `DEPLOY_USER`, `PID_FILE`, `ORACLE_SID`).
- Retry logic in nightly extract; dry‑run support in deployment.
