### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:28:53Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release rollout, queue-worker health management, and daily settled-payment exports for production operations.

#### Purpose
- Provides operational shell tools for deploying `orderd`, managing `queue-worker`, and exporting settled payments from Oracle.
- Supports release health checks and payment-export retry and row-count controls.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, deploys a tagged archive sequentially, restarts `orderd`, and stops on deployment or health-check failure; supports dry-run mode.
- **Worker lifecycle and health:** `healthcheck.sh` starts, stops, restarts, and reports on `queue-worker`, checking PID-file liveness and heartbeat freshness.
- **Payment export:** `nightly_extract.ksh` queries Oracle `settled_payment` for a business day, produces a gzipped CSV and row-count control file, and retries failed exports.

#### Key units
- `deploy_release.sh` — parses deployment options, loads environment host lists, deploys releases, and polls each host’s `/health` endpoint.
- `healthcheck.sh` — dispatches `start`, `stop`, `restart`, `status`, and `health` commands for `queue-worker`.
- `nightly_extract.ksh` — runs the Oracle payment export and creates its compressed CSV and control file.
- `wait_healthy(host)` — polls a deployment host until healthy or the timeout is reached.
- `is_running()` / `heartbeat_age()` — determine worker process liveness and heartbeat age.
- `run_export(outfile)` / `write_control(csv)` — execute the payment query and record its row count and business day.

#### Data flow
- **Deployment:** environment and release tag → host list and `build/<tag>.tar.gz` → per-host transfer, extraction, `current` update, and `orderd` restart → HTTP health checks.
- **Worker health:** command → PID-file process check and, for `health`, heartbeat-age check → status message and command-specific exit code.
- **Payment export:** business day and export directory → Oracle `settled_payment` query → CSV line count and control file → gzipped CSV; failed exports are retried.

#### Dependencies
- **Deployment:** `lib/common.sh`, staging or production host files, SSH/rsync, `tar`, `systemctl`, and `curl`.
- **Worker management:** `/usr/local/bin/queue-worker` and POSIX utilities including `kill`, `nohup`, and `stat`.
- **Payment export:** Oracle `sqlplus` and the `settled_payment` table; `gzip`, `wc`, and other system utilities.

#### Cross-cutting
- Scripts log operational messages to standard error; deployment and export logs are timestamped.
- Behavior is configured through options and environment overrides, including deployment credentials, worker PID and heartbeat paths, Oracle SID, business day, and export directory.
- Failure handling uses nonzero exit statuses; deployment stops at the first failed host, worker health distinguishes process failure from stale heartbeat, and payment export limits retries.
