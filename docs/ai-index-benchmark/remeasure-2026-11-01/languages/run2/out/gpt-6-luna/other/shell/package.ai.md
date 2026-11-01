### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:39:37Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Provides operational scripts for releasing services, managing a queue worker, and exporting settled payments for business-day processing.

#### Purpose
- Automates sequential staging or production deployment of tagged releases, with service restart and health verification.
- Supports queue-worker lifecycle and health checks, plus a retrying nightly export of settled Oracle payments.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` loads environment-specific host lists, uploads and activates a release, restarts `orderd`, and checks host health; supports dry-run mode.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status or heartbeat freshness for `queue-worker`.
- **Payment export:** `nightly_extract.ksh` queries settled payments for a business day, writes a CSV and row-count control file, then compresses the CSV.

#### Key units
- `deploy_release.sh` — coordinates per-host release deployment and health polling.
- `healthcheck.sh` — provides init-style queue-worker commands and heartbeat-based health status.
- `nightly_extract.ksh` — produces the daily settled-payment export and control file.
- `settled_payment` — Oracle source table for exported payment ID, account number, amount, and currency.

#### Data flow
- Deployment reads a tag, environment host list, and release archive; transfers and extracts the archive on each host, updates the `current` symlink, restarts `orderd`, then polls `/health`.
- Worker health checks use the PID file to determine whether the process is running, then compare heartbeat-file age against a 120-second limit.
- Payment export selects rows for the business day through SQL*Plus, writes semicolon-separated CSV, records its line count and business day in a `.ctl` file, and gzips the CSV; failed exports are retried up to three times.

#### Dependencies
- Deployment sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, `ssh`, `systemctl`, and `curl`.
- Worker management invokes `/usr/local/bin/queue-worker` and uses PID and heartbeat files.
- Payment export uses SQL*Plus with `CONNECT / AS SYSDBA` and the `settled_payment` table; requires `sqlplus` and `gzip`.

#### Cross-cutting
- The scripts are standalone operational commands in Bash, POSIX `sh`, and ksh93; their option and exit-status conventions differ.
- Deployment and payment export emit timestamped logs to stderr; worker lifecycle and health messages go to stdout.
- Deployment processes hosts sequentially and stops on unhealthy hosts; payment export retries failed queries, while worker checks have no retry or timeout behavior.
