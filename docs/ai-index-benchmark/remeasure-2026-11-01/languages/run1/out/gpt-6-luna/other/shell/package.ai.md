### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:16:40Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Provides operational scripts for releasing services, monitoring a queue worker, and exporting settled payments from Oracle.

#### Purpose
- Automates host-by-host application releases and queue-worker lifecycle and health checks.
- Produces a daily gzipped CSV export of settled payments with a row-count control file.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, copies and activates a tagged release, restarts `orderd`, and verifies host health; supports dry runs.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports the status or heartbeat freshness of `queue-worker`.
- **Payment export:** `nightly_extract.ksh` queries Oracle for a business day’s settled payments, writes and compresses a CSV, and records its row count in a `.ctl` file.

#### Key units
- `deploy_release.sh`: Bash deployment entry point with host loading, prerequisite checks, per-host deployment, health polling, and cleanup.
- `healthcheck.sh`: POSIX shell command wrapper; `is_running`, `start_worker`, `stop_worker`, and `heartbeat_age` implement worker checks and lifecycle actions.
- `nightly_extract.ksh`: Ksh nightly export entry point; `run_export` queries Oracle and `write_control` records the CSV row count.

#### Data flow
- **Deployment:** options and environment select a host list and `build/$TAG.tar.gz`; the archive is copied to each host, extracted under `/opt/releases/$TAG`, linked as `/opt/releases/current`, and followed by a service restart and HTTP health polling.
- **Worker health:** command input selects lifecycle or status behavior; health checks inspect the PID file and compare the heartbeat file’s age against 120 seconds.
- **Payment export:** business day and export directory configure a `sqlplus` query against `settled_payment`; successful CSV output is counted, described in a sibling control file, and gzipped. Failed exports are retried up to three times.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, SSH, `curl`, and remote `systemctl` operations.
- `healthcheck.sh` invokes `/usr/local/bin/queue-worker`; PID and heartbeat paths are configurable through `PID_FILE` and `HEARTBEAT_FILE`.
- `nightly_extract.ksh` connects to Oracle as `SYSDBA` through `sqlplus`, queries `settled_payment`, and uses `gzip` and `wc`.

#### Cross-cutting
- The scripts are directly executed shell jobs, not a shared application library; they use Bash, POSIX `sh`, and Ksh93 respectively.
- Each script logs or prints operational status and defines its own command-specific exit statuses.
- Configuration is supplied through command-line options and environment-variable defaults; deployment and export include retries or health checks, while worker lifecycle commands do not.
