### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:32Z
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
- Control a queue worker process and expose its health status.
- Export settled payments from Oracle to CSV, create control files, and compress output.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles packaging, distribution, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` starts, stops, restarts, and reports status/health of a queue worker.
- **Data Extraction**: `nightly_extract.ksh` queries Oracle for settled payments, writes CSV, creates control files, and gzips the result.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `systemctl`, and `curl` for health polling.
- `healthcheck.sh` – POSIX shell wrapper exposing `start|stop|restart|status|health`; manages PID and heartbeat files.
- `nightly_extract.ksh` – KSH script that runs a SQL query via `sqlplus`, writes CSV, counts rows, and gzips the file.

#### Data flow
1. **Deployment**: script reads environment‑specific host list → rsync archive → SSH to host → extract → symlink → restart `orderd` → poll `/health` endpoint.
2. **Health check**: worker binary (`/usr/local/bin/queue-worker`) writes heartbeat; script reads PID and heartbeat age to determine health.
3. **Extraction**: script builds SQL query → runs via `sqlplus` → writes CSV → counts rows → writes control file → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Host files: `hosts.staging`, `hosts.production`.
- Release archives: `build/<TAG>.tar.gz`.
- Oracle connection: `CONNECT / AS SYSDBA` to `settled_payment` table.
- Config files: `PID_FILE`, `HEARTBEAT_FILE`, `WORKER_BIN`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deployment, explicit exit codes in healthcheck).
- Logging via timestamped `log` functions.
- Use of environment variables for configuration (e.g., `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`).
- Retry logic in nightly extraction (max 3 attempts with sleep).
- Health thresholds: 120 s heartbeat age, 30 s health poll timeout.
