### other/shell
- H: 1.0
- C: 4658B0EC
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:20Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release deployment, queue-worker health management, and daily settled-payment exports for operational services.

#### Purpose
- Provides command-line operations for deploying `orderd`, controlling and checking the queue worker, and exporting settled Oracle payments.
- Supports staged host rollout, heartbeat-based worker monitoring, and business-day payment delivery as compressed CSV with a control file.

#### Responsibilities
- **Release deployment — `deploy_release.sh`:** Selects staging or production hosts, installs a tagged release, restarts `orderd`, and verifies each host’s health before proceeding.
- **Worker lifecycle and health — `healthcheck.sh`:** Starts, stops, restarts, and reports on the queue worker; checks heartbeat freshness.
- **Payment extraction — `nightly_extract.ksh`:** Queries `settled_payment` for a business day, writes and compresses a CSV, and emits a row-count control file.

#### Key units
- `deploy_host`: Uploads and installs a release on one host, restarts the service, and checks health.
- `wait_healthy`: Polls `http://$host:8080/health` until healthy or the timeout is reached.
- `load_hosts`: Loads the selected `hosts.staging` or `hosts.production` list.
- `is_running`: Determines queue-worker liveness from its PID file and process state.
- `start_worker` / `stop_worker`: Launch the queue worker and record its PID, or signal it and remove the PID file.
- `heartbeat_age`: Computes the age of the worker heartbeat for health reporting.
- `run_export`: Runs the Oracle payment query for the selected business day.
- `write_control`: Writes the export row count and business day to the `.ctl` file.

#### Data flow
- **Deployment:** Environment and tag options select a host list and release tarball; each host receives the archive, extracts it under `/opt/releases`, switches the `current` symlink, restarts `orderd`, and undergoes an HTTP health check.
- **Worker health:** Lifecycle commands use the PID file to identify and manage the worker; `health` additionally checks heartbeat age against the 120-second limit.
- **Payment export:** Business-day and output-directory options determine the Oracle query and destination; successful output is counted, recorded in a neighboring control file, then gzipped.

#### Dependencies
- Deployment uses `lib/common.sh`, `rsync`, `ssh`, `sudo systemctl`, and `curl`; host inventories are `hosts.staging` and `hosts.production`.
- Worker management invokes `/usr/local/bin/queue-worker` and uses PID and heartbeat files, configurable through `PID_FILE` and `HEARTBEAT_FILE`.
- Payment extraction uses `sqlplus` with Oracle `ORACLE_SID` (default `PAYPRD`) and reads the `settled_payment` table; output uses `gzip` and `wc`.

#### Cross-cutting
- These are directly invoked shell scripts: Bash with strict mode for deployment, POSIX `sh` for worker management, and `ksh93` for payment extraction.
- Each script has its own logging, configuration, and error conventions; deployment stops at the first host failure, while payment extraction retries failed exports up to three times.
