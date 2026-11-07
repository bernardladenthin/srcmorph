### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:52Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release rollout, queue-worker health management, and nightly settled-payment exports for operational services.

#### Purpose
- Provides shell entry points for sequential application deployment and service health checks.
- Produces daily Oracle payment extracts with compressed CSV output and row-count controls.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, transfers a tagged build, updates the release symlink, restarts `orderd`, and halts rollout on failed health checks; supports dry-run mode.
- **Worker lifecycle and monitoring:** `healthcheck.sh` starts, stops, restarts, and reports status for `queue-worker`; its health probe checks process presence and heartbeat freshness.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a business day, retries failed exports, and writes a gzipped CSV with a companion control file.

#### Key units
- `deploy_host` — deploys one release archive to a host and verifies its health.
- `wait_healthy` — polls a host’s `/health` endpoint up to the configured timeout.
- `load_hosts` — reads the selected staging or production host list.
- `start_worker` / `stop_worker` — launch or signal the worker and maintain its PID file.
- `is_running` / `heartbeat_age` — determine worker process state and heartbeat age.
- `run_export` — runs the Oracle query for settled payments on the requested business day.
- `write_control` — records the export row count and business day in the control file.

#### Data flow
- Deployment: environment and tag options → host list and `build/$TAG.tar.gz` → per-host transfer and remote extraction → `orderd` restart → HTTP health verification.
- Worker health: PID and heartbeat files → process and age checks → status message and command-specific exit code.
- Payment export: business day and export directory → Oracle `settled_payment` query → CSV row count and `.ctl` file → gzip-compressed CSV.

#### Dependencies
- Deployment uses `lib/common.sh`, staging/production host files, and `rsync`, `ssh`, `curl`, and remote `sudo systemctl`; target service is `orderd`.
- Worker management uses `/usr/local/bin/queue-worker`, its PID file, heartbeat file, and standard process/file utilities.
- Payment export uses Oracle `sqlplus` with `/ AS SYSDBA`, `settled_payment`, and `gzip`; defaults to `ORACLE_SID=PAYPRD`.

#### Cross-cutting
- Each script is a directly invoked operational command; `deploy_release.sh` additionally sources `lib/common.sh`.
- Configuration is supplied through command-line options and environment variables, with defaults for deployment identity, worker files, Oracle SID, and export directory.
- Failure is signaled through script-specific exit codes; deployment stops on a host health-check failure, while payment export retries up to three times.
