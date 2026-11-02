### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:28:09Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Provides operational scripts for releasing services, monitoring a queue worker, and exporting settled payment data.

#### Purpose
- Automates release deployment and health validation across staging and production hosts.
- Supports queue-worker lifecycle checks and nightly Oracle payment exports.

#### Responsibilities
- **Release deployment:** `deploy_release.sh` selects environment hosts, deploys a tagged tarball sequentially, restarts `orderd`, and halts on failed health checks.
- **Worker operations:** `healthcheck.sh` starts, stops, restarts, and reports status for `queue-worker`; its health command checks heartbeat freshness.
- **Payment extraction:** `nightly_extract.ksh` exports settled payments for a business day to a gzipped CSV and writes a row-count control file, retrying failures.

#### Key units
- `deploy_release.sh` — parses deployment options, loads host lists, deploys releases, and polls host health endpoints.
- `healthcheck.sh` — implements PID-file-based worker lifecycle commands and heartbeat-age health reporting.
- `nightly_extract.ksh` — runs the Oracle `settled_payment` query, creates the CSV/control outputs, and manages retries.

#### Data flow
- Release: environment and tag → host list and `build/$TAG.tar.gz` → per-host transfer, extraction, symlink update, service restart → `http://$host:8080/health` validation.
- Worker: command argument → PID-file/process check or lifecycle action; `health` additionally reads heartbeat-file modification time and returns a status.
- Payment export: business day and export directory → Oracle query → semicolon-delimited CSV → row-count `.ctl` file and compressed `.csv.gz`.

#### Dependencies
- `deploy_release.sh` sources `lib/common.sh`, reads `hosts.staging` or `hosts.production`, and uses `rsync`, SSH, `sudo systemctl`, and `curl`; remote service is `orderd`.
- `healthcheck.sh` operates `/usr/local/bin/queue-worker` with configurable PID and heartbeat files; uses `nohup`, `kill`, and `stat`.
- `nightly_extract.ksh` uses `sqlplus` with `/ AS SYSDBA` to query Oracle table `settled_payment`; requires `gzip`.

#### Cross-cutting
- Scripts are directly executable operational tools, using Bash, POSIX `sh`, and `ksh93` respectively.
- Configuration is provided through command-line options and environment variables, with defaults for deployment credentials, worker files, Oracle SID, and export directory.
- Deployment and extraction scripts emit timestamped stderr logs; deployment stops at the first failed host, while extraction retries up to three times.
