### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:56:20Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying services, managing a queue worker, and nightly data extraction from Oracle.

#### Purpose
- Deploy release tarballs to multiple hosts and verify service health.
- Control a queue worker process (start/stop/status/health).
- Export settled payments from Oracle to gzipped CSV with control file.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, symlink updates, service restart, and health polling.
- **Process Management**: `healthcheck.sh` provides init‑style control and heartbeat monitoring for a queue worker.
- **Data Export**: `nightly_extract.ksh` runs nightly SQL exports, retries on failure, and produces CSV/CTL artifacts.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `systemctl`, and `curl` for health checks.
- `healthcheck.sh` – exposes `start`, `stop`, `restart`, `status`, `health` commands; tracks PID and heartbeat files.
- `nightly_extract.ksh` – parses `-d`/`-o` options, runs `sqlplus` queries, writes CSV, creates `.ctl`, gzips output.

#### Data flow
1. **Deployment**: script reads environment‑specific host list → rsync tarball → SSH remote extraction → symlink update → `systemctl restart` → `curl` health poll → log status.
2. **Healthcheck**: command dispatch → worker binary launch (`nohup /usr/local/bin/queue-worker --heartbeat`) → PID file creation → heartbeat file updates → health command checks age against `MAX_HEARTBEAT_AGE`.
3. **Nightly Extract**: option parsing → tool checks → export directory creation → SQL query via `sqlplus` → CSV generation → row‑count control file → gzip → success log.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- Host list files: `hosts.staging`, `hosts.production`.
- SSH key: `$HOME/.ssh/deploy_ed25519`.
- Oracle connection: `sqlplus / AS SYSDBA` to `PAYPRD` SID.
- Queue worker binary: `/usr/local/bin/queue-worker`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deploy script; explicit exit codes in others).
- Timestamped logging via `log` functions.
- PID and heartbeat file conventions for process monitoring.
- Retry logic in nightly export (max 3 attempts with sleep).
- Configuration via environment variables and defaults (e.g., `DEPLOY_USER`, `HEALTH_TIMEOUT`, `MAX_HEARTBEAT_AGE`).
- Consistent exit code semantics: `0` success, non‑zero for specific failure types.
