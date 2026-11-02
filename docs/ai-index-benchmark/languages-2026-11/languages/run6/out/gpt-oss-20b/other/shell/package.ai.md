### other/shell
- H: 1.0
- C: EBEE33D4
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:47:45Z
- G: 
- A: 0.0.0
- X: package
- F: [deploy_release.sh](deploy_release.sh.ai.md)
- F: [healthcheck.sh](healthcheck.sh.ai.md)
- F: [nightly_extract.ksh](nightly_extract.ksh.ai.md)
---
> Automates deployment of release tarballs to multiple hosts and manages a queue‑worker service with health checks.

#### Purpose
- Deploys a specified release tarball to all hosts in a chosen environment.
- Controls a queue‑worker process (start, stop, status, health).

#### Responsibilities
- **Deployment orchestration** – parsing options, validating prerequisites, copying artifacts, extracting, symlinking, restarting services, and health verification.
- **Service lifecycle management** – starting, stopping, restarting, and reporting status of a queue worker.
- **Health monitoring** – checking service availability via HTTP endpoints and heartbeat file age.

#### Key units
- `deploy_release.sh` – Bash script that deploys releases, restarts `orderd`, and verifies `/health`.
- `healthcheck.sh` – POSIX shell wrapper that manages a queue worker and reports health via heartbeat age.
- `nightly_extract.ksh` – (placeholder for nightly extraction logic).

#### Data flow
1. **Deployment**  
   `deploy_release.sh` reads `-e <environment>` and `-t <tag>`, loads host list, rsyncs `build/$TAG.tar.gz` to each host, SSH‑executes extraction and symlink updates, restarts `orderd`, and polls `http://$host:8080/health` until success or timeout.

2. **Service control**  
   `healthcheck.sh` receives a command (`start|stop|restart|status|health`), manipulates `/run/queue-worker.pid`, launches `/usr/local/bin/queue-worker` with `nohup`, and checks heartbeat file age to determine health.

#### Dependencies
- **External tools**: `rsync`, `ssh`, `curl`, `systemctl`, `nohup`, `kill`, `date`, `stat`.
- **Files**: SSH key (`$SSH_KEY`), host list files (`hosts.staging`, `hosts.production`), release tarballs (`build/$TAG.tar.gz`).
- **Service binaries**: `/usr/local/bin/queue-worker`, systemd unit `orderd`.

#### Cross-cutting
- **Logging**: timestamped messages to stderr (`log` function in `deploy_release.sh`).
- **Error handling**: `die`/`usage` functions with exit codes; health checks return specific codes (`0`, `1`, `2`, `3`, `64`).
- **Configuration via environment variables**: `DEPLOY_USER`, `SSH_KEY`, `RELEASE_ROOT`, `SERVICE_NAME`, `HEALTH_TIMEOUT`, `PID_FILE`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`.
- **Idempotent operations**: symlink updates (`current`) and health polling ensure repeatable deployments.
