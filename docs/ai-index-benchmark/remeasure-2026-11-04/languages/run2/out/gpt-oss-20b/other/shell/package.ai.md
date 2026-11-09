### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:16Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and nightly data extraction for a payment processing service.

#### Purpose
- Deploy release tarballs to multiple hosts, restart services, and verify health.
- Control a queue‑worker process with start/stop/status/health commands.
- Export settled payments from Oracle to CSV, gzip, and generate control files.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles distribution, extraction, symlink updates, service restarts, and health checks across environments.
- **Process Management**: `healthcheck.sh` provides a POSIX init‑style interface for a queue worker, tracking PID, heartbeat, and status.
- **Data Extraction**: `nightly_extract.ksh` runs nightly to pull settled payments, produce CSV/GZ, and create control metadata.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – PID/heartbeat manager for `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – Oracle export script, relies on `sqlplus` and `gzip`.

#### Data flow
1. **Deployment**: script parses `-e`/`-t`, loads host list, rsyncs tarball → SSH → extract → symlink `current` → restart `orderd` → poll `http://host:8080/health`.
2. **Healthcheck**: `start` writes PID, `stop` kills process, `status` checks PID and heartbeat age, `health` reports OK/WARNING/CRITICAL.
3. **Extraction**: parse `-d`/`-o`, run SQL via `sqlplus`, spool to CSV, count rows, write control file, gzip CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- External files: `hosts.staging`, `hosts.production`, `"$SCRIPT_DIR/lib/common.sh"`.
- Oracle DB via `sqlplus / AS SYSDBA`.
- Queue worker binary `/usr/local/bin/queue-worker`.

#### Cross-cutting
- Logging with timestamps (`log` functions).
- Strict error handling: `die`/`fail` exit with codes 1–4.
- Configuration via environment variables and defaults (`DEPLOY_USER`, `SSH_KEY`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`, `ORACLE_SID`).
- Retry logic in nightly extraction (max 3 attempts).
- Health checks use timeouts (`HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).
