### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:38:28Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates service release rollout and worker health operations, alongside the nightly export of settled payments for business-day processing.

#### Purpose
- Deploy tagged `orderd` releases across staging or production hosts with per-host health verification.
- Manage the queue worker lifecycle and heartbeat checks, and generate daily settled-payment CSV exports with row-count control files.

#### Responsibilities
- **Release deployment:** Parse environment and tag options, load host lists, deploy releases sequentially, restart `orderd`, and stop on the first unhealthy host; supports dry runs.
- **Queue worker operations:** Start, stop, restart, and report status for `queue-worker`; check worker liveness and heartbeat freshness.
- **Payment exports:** Query Oracle `settled_payment` for a business day, create a semicolon-delimited CSV and row-count control file, and retry failed exports.

#### Key units
- `deploy_release.sh` — Deploy a tagged release to staging or production hosts and poll each host’s `/health` endpoint.
- `healthcheck.sh` — Provide init-style commands and heartbeat-based health status for the queue worker.
- `nightly_extract.ksh` — Export daily settled payments to compressed CSV with a `.ctl` row-count file.

#### Data flow
- Deployment: options and host files → local `build/$TAG.tar.gz` → remote release directory and `current` symlink → `orderd` restart → health probe.
- Worker health: PID file and process state → liveness result; heartbeat-file timestamp → freshness result.
- Payment export: business day and Oracle `settled_payment` query → CSV → row count in `.ctl` → gzip-compressed CSV; failed queries are retried.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`; uses `hosts.staging` or `hosts.production`, the build tarball, and remote SSH/`rsync` operations.
- Deployment targets `/opt/releases` and the `orderd` systemd service; health probes use `curl`.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker` and uses configurable PID and heartbeat files.
- `nightly_extract.ksh` connects to Oracle as `/ AS SYSDBA`, reads `settled_payment`, and requires `sqlplus` and `gzip`.

#### Cross-cutting
- Scripts are directly executed, with shell-specific implementations: Bash, POSIX `sh`, and `ksh93`.
- Logging is timestamped to standard error in the deployment and export scripts; the worker script prints command and health status messages.
- Failure handling is script-specific: deployment stops at the first host failure, worker health reports distinct stopped and stale-heartbeat statuses, and payment export retries up to three times.
