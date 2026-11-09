### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:46Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and nightly data extraction for a distributed payment processing system.

#### Purpose
- Deploy release tarballs to multiple hosts, restart services, and verify health.
- Manage a queue worker’s lifecycle and health status.
- Export settled payments from Oracle nightly, producing CSVs and control files.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles packaging, distribution, service restart, and health checks across environments.
- **Process Management**: `healthcheck.sh` provides start/stop/status/health for a queue worker, using PID and heartbeat files.
- **Data Extraction**: `nightly_extract.ksh` queries Oracle for settled payments, writes CSV, control file, and gzips output.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, rsync, extraction, symlink, systemctl restart, and curl health polling.
- `healthcheck.sh` – PID/heartbeat‑based worker controller (`start_worker`, `stop_worker`, `is_running`, `heartbeat_age`).
- `nightly_extract.ksh` – Oracle export driver (`run_export`, `write_control`, retry loop, gzip).

#### Data flow
1. **Deployment**: script parses `-e`/`-t`, loads host list, rsyncs `build/$TAG.tar.gz` to each host, SSH‑executes extraction, updates `$RELEASE_ROOT/current`, restarts `orderd`, polls `http://$host:8080/health`.
2. **Health Check**: `healthcheck.sh` reads `PID_FILE`, verifies process, checks `HEARTBEAT_FILE` age, reports status or health.
3. **Nightly Extract**: script parses `-d`/`-o`, ensures export dir, runs `sqlplus` query, writes CSV, creates control file with row count, gzips CSV.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- **Files**: `hosts.staging`, `hosts.production`, `build/$TAG.tar.gz`, `/usr/local/bin/queue-worker`.
- **Environment**: `DEPLOY_USER`, `SSH_KEY`, `ORACLE_SID`, `NLS_LANG`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deploy, `set -e` in nightly).
- Logging via timestamped `log` functions.
- Configuration via environment variables and associative arrays.
- Health checks use heartbeat file timestamps and HTTP polling.
