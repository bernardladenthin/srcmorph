### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:59Z
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
- Export settled payments from Oracle to CSV, create control files, and compress output.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles environment‑specific host lists, tarball transfer, extraction, symlink updates, service restart, and health polling.
- **Process Management**: `healthcheck.sh` provides a POSIX‑style service wrapper for a queue worker, managing PID files, heartbeats, and status reporting.
- **Data Extraction**: `nightly_extract.ksh` runs nightly to query Oracle, write CSVs, generate control files, and gzip the results with retry logic.

#### Key units
- `deploy_release.sh` – orchestrates remote deployment via `rsync`/`ssh`, updates `/opt/releases`, restarts `orderd`, and checks `/health`.
- `healthcheck.sh` – manages `/usr/local/bin/queue-worker`, uses `/run/queue-worker.pid` and `/var/lib/queue-worker/heartbeat`, enforces `MAX_HEARTBEAT_AGE` (120 s).
- `nightly_extract.ksh` – executes `sqlplus` against `settled_payment`, writes `payments_<day>.csv`, creates `payments_<day>.ctl`, gzips the CSV, with up to 3 retries.

#### Data flow
1. **Deployment**: script → `rsync` tarball → SSH → remote `/tmp/` → extraction → `/opt/releases/<tag>` → symlink `current` → `systemctl restart orderd` → `curl /health` poll.
2. **Healthcheck**: start → `nohup queue-worker --heartbeat` → PID file → heartbeat file updated by worker → health command reads heartbeat age → status output.
3. **Nightly Extract**: parse `-d`/`-o` → `sqlplus` query → CSV output → `write_control` counts rows → gzip → control file records row count and day.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- **Remote paths**: `/opt/releases`, `/run/queue-worker.pid`, `/var/lib/queue-worker/heartbeat`.
- **Oracle**: `settled_payment` table, SYSDBA connection.
- **Environment files**: `hosts.staging`, `hosts.production`.

#### Cross‑cutting
- **Logging**: All scripts use a `log` function that prefixes timestamps to stderr.
- **Error handling**: `die`/`fail` functions exit with specific codes (1–4) and print messages.
- **Configuration**: Default values for users, SSH keys, release roots, service names, heartbeat age, and export directories are defined as constants.
- **Retry logic**: `nightly_extract.ksh` retries failed exports up to `MAX_RETRIES` with sleep intervals.
- **Health checks**: `deploy_release.sh` waits for `/health` after restart; `healthcheck.sh` verifies heartbeat age against `MAX_HEARTBEAT_AGE`.
