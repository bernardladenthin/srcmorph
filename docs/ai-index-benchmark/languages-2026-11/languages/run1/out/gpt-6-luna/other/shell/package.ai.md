### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:11:35Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Supports operational delivery and monitoring by deploying tagged application releases, managing a queue worker, and exporting settled payment data for a business day.

#### Purpose
- Provides shell tooling for release rollout, worker lifecycle and health checks, and scheduled payment-data extraction.
- Exposes command-line options, environment overrides, and status codes for automation.

#### Responsibilities
- **Release deployment (`deploy_release.sh`)** — selects staging or production hosts, transfers and activates a tagged archive, restarts `orderd`, and gates sequential rollout on host health.
- **Queue-worker operations (`healthcheck.sh`)** — starts, stops, restarts, and reports status for `queue-worker`; checks process presence and heartbeat freshness.
- **Payment export (`nightly_extract.ksh`)** — queries settled Oracle payments for a business day, writes a delimited CSV and row-count control file, then compresses the extract with retries.

#### Key units
- `deploy_host` — deploys an archive to one host and waits for its health endpoint.
- `wait_healthy` — polls `http://$host:8080/health` until healthy or the 30-second timeout.
- `load_hosts` — loads the selected `hosts.staging` or `hosts.production` list.
- `is_running`, `start_worker`, `stop_worker` — check and manage the worker using its PID file.
- `heartbeat_age` — calculates the age of the worker heartbeat file.
- `run_export` — queries Oracle `settled_payment` records for the selected business day.
- `write_control` — records the CSV row count and business day in a sibling `.ctl` file.

#### Data flow
- **Deployment:** parse environment and tag → load hosts → copy `build/<tag>.tar.gz` → extract under `/opt/releases/<tag>` and update `current` → restart `orderd` → check host health before proceeding.
- **Worker health:** inspect PID/process state → read heartbeat-file modification time → report healthy, stale heartbeat, or not running through output and exit status.
- **Payment extraction:** choose business day and export directory → query settled payments into `payments_<day>.csv` → write `.ctl` row-count control → gzip the CSV; retry failed exports up to three times.

#### Dependencies
- Deployment sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, `ssh`, `curl`, and remote `tar`, `ln`, `sudo`, and `systemctl`.
- Worker management invokes `/usr/local/bin/queue-worker` with `--heartbeat`; uses PID and heartbeat files configurable through `PID_FILE` and `HEARTBEAT_FILE`.
- Payment export uses `sqlplus` with `CONNECT / AS SYSDBA`, querying Oracle table `settled_payment` fields `payment_id`, `account_no`, `amount`, and `currency`; uses `gzip` and `wc`.

#### Cross-cutting
- Scripts run as direct command-line tools, with distinct interfaces and exit codes for automation.
- Deployment and extraction emit timestamped messages to standard error; deployment and extraction include explicit failure handling, while the worker wrapper has no traps or retries.
- Configuration comes from command-line options and environment variables, with defaults for deployment credentials, worker paths, Oracle SID, and export location.
