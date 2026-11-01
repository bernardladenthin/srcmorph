### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:13:09Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> A collection of shell utilities that automate deployment, health monitoring, and nightly data export for a payment processing service.

#### Purpose
- Automate release deployment across multiple hosts.
- Provide a lightweight health‑check and control interface for a queue worker.
- Export daily settled payments from Oracle to a compressed CSV with a control file.

#### Responsibilities
- **Deployment** – `deploy_release.sh` handles tarball transfer, extraction, service restart, and health verification.
- **Process control** – `healthcheck.sh` manages a queue worker’s lifecycle and exposes a heartbeat‑based health probe.
- **Data extraction** – `nightly_extract.ksh` runs a scheduled Oracle query, writes CSV output, gzips it, and creates a control file.

#### Key units
- `deploy_release.sh` – orchestrates host‑by‑host deployment, uses `rsync`, `ssh`, and `systemctl`.
- `healthcheck.sh` – POSIX wrapper exposing `start|stop|restart|status|health` commands; relies on `/usr/local/bin/queue-worker`.
- `nightly_extract.ksh` – KSH script that invokes `sqlplus` to export `settled_payment` rows, gzips the result, and writes a `.ctl` file.

#### Data flow
1. **Deployment**: script reads environment‑specific host list → rsync tarball → SSH to host → extract → symlink update → restart service → poll `/health` endpoint.
2. **Health check**: worker binary writes heartbeat file; script checks PID file and heartbeat age to report status.
3. **Export**: script spawns `sqlplus` to query Oracle → writes CSV → counts rows → writes control file → gzips CSV.

#### Dependencies
- External utilities: `rsync`, `ssh`, `curl`, `systemctl`, `sqlplus`, `gzip`, `nohup`, `kill`, `stat`.
- External files: host lists (`hosts.staging`, `hosts.production`), common library (`lib/common.sh`).
- External binaries: `/usr/local/bin/queue-worker`, Oracle client.

#### Cross‑cutting
- Strict error handling (`set -euo pipefail` in deployment, explicit exit codes in others).
- Timestamped logging via `log` functions.
- Configuration via environment variables and command‑line options.
- Health checks based on heartbeat file age or service endpoint polling.
