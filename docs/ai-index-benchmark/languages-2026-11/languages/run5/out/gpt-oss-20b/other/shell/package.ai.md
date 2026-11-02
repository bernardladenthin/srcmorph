### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:36:33Z
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
- Control a queue worker process (start/stop/status/health).
- Export settled payments from Oracle to gzipped CSV with a control file.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball distribution, extraction, symlink updates, service restarts, and health checks across environments.
- **Process Management**: `healthcheck.sh` provides a POSIX init‑style interface to start, stop, and monitor a queue worker, including heartbeat validation.
- **Data Export**: `nightly_extract.ksh` runs nightly to pull settled payments from Oracle, writes CSV and control files, and gzips the output.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, `curl`.
- `healthcheck.sh` – exposes `start|stop|restart|status|health` commands for the queue worker binary `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – executes an Oracle `sqlplus` query, writes `payments_YYYYMMDD.csv`, `payments_YYYYMMDD.ctl`, and gzips the CSV.

#### Data flow
1. **Deployment**: `deploy_release.sh` reads host list → rsync tarball → SSH → extract → symlink → systemctl restart → curl health endpoint.
2. **Health**: `healthcheck.sh` checks PID file → verifies heartbeat file age → reports status.
3. **Export**: `nightly_extract.ksh` runs `sqlplus` query → writes CSV → counts rows → writes `.ctl` → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `nohup`, `kill`, `sqlplus`, `gzip`.
- SSH key: `$SSH_KEY` (default `~/.ssh/deploy_ed25519`).
- Host list files: `hosts.staging`, `hosts.production`.
- Oracle SID: `PAYPRD` (default).
- Service name: `orderd` (restarted by deployment).
- Queue worker binary: `/usr/local/bin/queue-worker`.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deployment, explicit checks in others).
- Timestamped logging via `log` functions.
- Configuration via environment variables and defaults.
- Retry logic in nightly export (`MAX_RETRIES=3`).
- Health checks use HTTP (`curl`) or heartbeat file age (`stat`).
- PID and heartbeat files standardize process monitoring.
