### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:06:09Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction.

#### Purpose
- Deploy application releases to multiple hosts.
- Control and monitor a queue worker process.
- Export settled payments from Oracle to CSV.

#### Responsibilities
- **Deployment**: `deploy_release.sh` handles tarball transfer, extraction, service restart, and health verification across environments.
- **Process Management**: `healthcheck.sh` provides start/stop/status/health commands for a queue worker, tracking PID and heartbeat.
- **Data Export**: `nightly_extract.ksh` pulls settled payments for a business day, writes CSV and control files, and gzips the output.

#### Key units
- `deploy_release.sh` – orchestrates remote deployment and health checks.
- `healthcheck.sh` – PID/heartbeat‑based worker controller.
- `nightly_extract.ksh` – Oracle SQL export pipeline with retry logic.

#### Data flow
1. **Deployment**: script → rsync tarball → SSH → remote extraction → symlink update → service restart → health endpoint poll.
2. **Healthcheck**: start → writes PID → worker writes heartbeat → health command reads heartbeat age → reports status.
3. **Export**: script → `sqlplus` query → CSV spool → control file generation → gzip → completion.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `kill`, `nohup`, `date`, `stat`, `sqlplus`, `gzip`.
- **Files**: host lists (`hosts.staging`, `hosts.production`), release tarballs (`build/$TAG.tar.gz`), worker binary (`/usr/local/bin/queue-worker`).
- **Environment**: SSH key (`$SSH_KEY`), Oracle SID (`$ORACLE_SID`), NLS settings.

#### Cross-cutting
- Strict error handling (`set -euo pipefail` in deploy script; `set -e` in export).
- Timestamped logging via `log` functions.
- Configuration via environment variables and defaults.
- Retry logic in export script; health timeout in deploy script.
