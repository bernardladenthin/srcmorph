### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:33:31Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates release rollout, queue-worker health management, and daily settled-payment exports for production services.

#### Purpose
- Provides shell-based operational tooling for deploying `orderd`, managing a queue worker, and exporting settled payment records.
- Covers release distribution and health validation, worker lifecycle and heartbeat checks, and Oracle batch extraction with count controls.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects staging or production hosts, installs a tagged archive, switches the `current` release, restarts `orderd`, and stops on failed health checks; supports dry runs.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status for `queue-worker`; its health command checks process presence and heartbeat age.
- **Payment export:** `nightly_extract.ksh` queries Oracle `settled_payment` for one business day, emits a compressed semicolon-delimited CSV, and writes a row-count control file; retries failed exports.

#### Key units
- `deploy_release.sh` — deploys `build/<tag>.tar.gz` to hosts configured for an environment and polls each host’s `/health` endpoint.
- `healthcheck.sh` — dispatches `start`, `stop`, `restart`, `status`, and `health` commands using a PID file and heartbeat file.
- `nightly_extract.ksh` — exports daily payment fields and produces the matching `.ctl` file.
- `settled_payment` — Oracle table queried by the export, using `payment_id`, `account_no`, `amount`, `currency`, and `business_day`.

#### Data flow
- **Deployment:** environment and tag options → environment-specific host list and local archive → remote extraction and `current` symlink update → `orderd` restart → per-host health polling.
- **Worker health:** command-line action → PID-file process check; for `health`, heartbeat timestamp age determines the result.
- **Payment export:** business-day and directory options → Oracle query → CSV line count and business-day control file → gzip-compressed CSV.

#### Dependencies
- Deployment uses `lib/common.sh`, SSH and `rsync`, plus remote `tar`, `systemctl`, and `curl`; hosts come from `hosts.staging` or `hosts.production`.
- Worker management invokes `/usr/local/bin/queue-worker` and relies on POSIX utilities including `kill`, `nohup`, `date`, and `stat`.
- Payment export uses `sqlplus`, `gzip`, and Oracle’s `settled_payment` table; `ORACLE_SID` defaults to `PAYPRD`.

#### Cross-cutting
- Scripts expose command-line options or commands and report progress or failures to standard error; deployment and export messages include timestamps.
- Configuration uses environment overrides and defaults: deployment credentials and SSH key, worker PID and heartbeat paths, and export directory and Oracle SID.
- Failure handling is explicit: deployment uses strict shell mode and aborts on host health failure; worker health uses distinct exit statuses for stopped and stale-heartbeat states; export retries up to three times with 60-second delays.
