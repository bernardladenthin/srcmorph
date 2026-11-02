### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:31:59Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment, health monitoring, and daily payment export for a distributed order‑processing system.

#### Purpose
- Deploy release tarballs to multiple hosts, restart services, and verify health.
- Manage a queue worker process, exposing start/stop/status/health commands.
- Export settled payments from Oracle to gzipped CSV with a control file.

#### Responsibilities
- **Deployment**: orchestrate multi‑host release rollout, service restart, and health polling.
- **Process Management**: control a queue worker’s lifecycle and health via PID/heartbeat files.
- **Data Export**: query Oracle for settled payments, generate CSV, compress, and produce a row‑count control file.

#### Key units
- `deploy_release.sh` – Bash script that reads host lists, rsyncs tarballs, SSH‑executes extraction, restarts `orderd`, and polls `/health`.
- `healthcheck.sh` – POSIX shell wrapper exposing `start|stop|restart|status|health` for a queue worker; uses `PID_FILE`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`.
- `nightly_extract.ksh` – Korn shell script that runs an Oracle `sqlplus` query on `settled_payment`, writes CSV, gzips it, and creates a `.ctl` file with row count.

#### Data flow
1. **Deployment**: `deploy_release.sh` → `rsync` tarball → SSH → extract → symlink `/opt/releases/current` → `systemctl restart orderd` → `curl /health` loop.
2. **Health**: `healthcheck.sh` reads `PID_FILE`; if running, checks `HEARTBEAT_FILE` age; reports status or exits with appropriate code.
3. **Export**: `nightly_extract.ksh` → `sqlplus` query → CSV file → `wc -l` → `.ctl` file; then `gzip` compresses CSV.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `sqlplus`, `gzip`, `wc`, `stat`, `date`, `sed`, `grep`, `mapfile`.
- System services: `systemctl` for `orderd`.
- Oracle client environment (`ORACLE_SID`, `NLS_LANG`).
- Worker binary: `/usr/local/bin/queue-worker`.

#### Cross‑cutting
- **Configuration**: environment variables (`DEPLOY_USER`, `SSH_KEY`, `PID_FILE`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`, `ORACLE_SID`).
- **Error handling**: `die`/`fail` functions log and exit with codes; health checks use distinct exit codes (0,1,2,3,64).
- **Logging**: timestamped `log`/`printf` statements to stderr.
- **Retry logic**: `nightly_extract.ksh` retries up to `MAX_RETRIES` with sleep intervals.
