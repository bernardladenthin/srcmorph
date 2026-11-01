### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:39:37Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Supports release delivery, service-worker operations, and daily settled-payment export across production environments.

#### Purpose
- Automates sequential deployment of tagged releases, queue-worker lifecycle and health checks, and Oracle payment extracts with count controls.
- Provides operational scripts for deployment, service monitoring, and business-day data delivery.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, transfers and activates a release, restarts `orderd`, and halts rollout if health checks fail.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports `queue-worker`, including heartbeat freshness.
- **Payment extraction:** `nightly_extract.ksh` queries settled payments for a business day, emits a gzipped semicolon-delimited CSV, and writes a row-count control file; retries failed exports.

#### Key units
- `deploy_release.sh` — sequentially deploys `build/<tag>.tar.gz` to environment hosts and verifies each host at `/health`.
- `deploy_host(host)` — transfers and activates a release, restarts `orderd`, and checks host health.
- `wait_healthy(host)` — polls the host health endpoint up to `HEALTH_TIMEOUT` (30 seconds).
- `healthcheck.sh` — dispatches `start`, `stop`, `restart`, `status`, and `health` commands for `queue-worker`.
- `is_running()` / `heartbeat_age()` — check worker PID liveness and heartbeat age.
- `nightly_extract.ksh` — exports rows from Oracle `settled_payment` and produces CSV, gzip, and control-file outputs.
- `run_export(outfile)` / `write_control(csv)` — run the Oracle export and record its CSV row count with the business day.

#### Data flow
- **Deployment:** CLI environment and tag → release archive and host list → per-host transfer, extraction, `current` symlink update, and `orderd` restart → health polling; dry-run logs plans without deploying.
- **Worker health:** command and PID/heartbeat files → process and heartbeat checks → status output and command-specific exit code.
- **Payment export:** business day and output directory → Oracle query → semicolon-delimited CSV → row-count control file → gzip-compressed CSV; export failures are retried up to three times.

#### Dependencies
- Deployment uses `lib/common.sh`, `hosts.staging` or `hosts.production`, SSH/`rsync`, and remote service-management commands.
- Worker control uses `/usr/local/bin/queue-worker`, PID and heartbeat files, and POSIX shell utilities.
- Payment export uses Oracle `sqlplus` and the `settled_payment` table (`payment_id`, `account_no`, `amount`, `currency`, `business_day`), plus `gzip`.

#### Cross-cutting
- Scripts are command-line operational tools with configurable environment variables and timestamped logging where specified.
- Failure is surfaced through nonzero exit statuses; deployment stops on a host health timeout, worker health distinguishes stopped from stale heartbeat, and extraction uses bounded retries.
- Key defaults: deployment user `deploy`, SSH key `~/.ssh/deploy_ed25519`; worker heartbeat limit 120 seconds; Oracle SID `PAYPRD`, export directory `/data/export/payments`, and retry delay 60 seconds.
