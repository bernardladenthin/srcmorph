### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:33:11Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and nightly data export for a payment processing service.

#### Purpose
- Deploy release tarballs to multiple hosts, restart services, and verify health.
- Control a queue‑worker process and expose its health status.
- Export settled payments from Oracle to gzipped CSVs with control files.

#### Responsibilities
- **Deployment**: orchestrate file transfer, extraction, symlink updates, service restarts, and health checks across environments.
- **Process Management**: start/stop/restart a queue worker, track its PID, and monitor heartbeat freshness.
- **Data Export**: run nightly Oracle exports, retry on failure, gzip output, and generate row‑count control files.

#### Key units
- `deploy_release.sh` – script that parses `-e`, `-t`, `-n`, `-p` options, copies tarballs via `rsync`, SSH‑executes extraction and service restart, and polls `http://<host>:8080/health`.
- `healthcheck.sh` – utility exposing `start|stop|restart|status|health` commands, writes/reads `/run/queue-worker.pid`, checks `/var/lib/queue-worker/heartbeat`, and exits with codes 0,1,2,3,64.
- `nightly_extract.ksh` – KSH script that parses `-d` and `-o`, ensures `sqlplus`/`gzip`, runs `sqlplus` export, gzips CSV, writes `.ctl` control file, and retries up to 3 times.

#### Data flow
1. **Deployment**: `deploy_release.sh` → `rsync` tarball → SSH → extraction → symlink → `systemctl restart orderd` → `curl` health poll.
2. **Health**: `healthcheck.sh` reads PID file → verifies process → checks heartbeat file age → reports status.
3. **Export**: `nightly_extract.ksh` → `sqlplus` query → CSV file → `gzip` → control file → logs.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- External scripts: `$SCRIPT_DIR/lib/common.sh` (used by `deploy_release.sh`).
- External binaries: `/usr/local/bin/queue-worker`, `/usr/local/bin/queue-worker` (worker binary).
- Oracle connection via `sqlplus / AS SYSDBA`.

#### Cross-cutting
- Strict error handling in `deploy_release.sh` (`set -euo pipefail`); `healthcheck.sh` uses simple exit codes; `nightly_extract.ksh` implements retry logic.
- Common logging pattern: timestamped messages to stderr.
- Configuration via environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `ORACLE_SID`).
- Health checks: HTTP endpoint for service, heartbeat file age for worker, retry limits for export.
