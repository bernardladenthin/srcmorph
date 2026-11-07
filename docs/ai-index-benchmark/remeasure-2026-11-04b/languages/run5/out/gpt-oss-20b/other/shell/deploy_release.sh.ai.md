### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:31Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploy a specified release to all hosts in an environment.
- Restart `orderd` service and verify health.
- Can run in dry‑run mode.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- `set -euo pipefail`

#### Input
- Options parsed with `getopts`:
  - `-e <environment>` → `ENVIRONMENT`
  - `-t <tag>` → `TAG`
  - `-n` → `DRY_RUN=1`
  - `-p <parallel>` → ignored (logged)
  - `-h` → usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
- Files read:
  - `hosts.staging` or `hosts.production` (based on `ENVIRONMENT`)
  - `build/$TAG.tar.gz` (source tarball)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `orderd` via `systemctl`.
- Prints deployment status to stderr.
- Exits with status 1 on errors, 0 on success.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list from environment‑specific file.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host, extract, symlink, restart service.
  - `wait_healthy`: poll `/health` endpoint until success or timeout.
  - Log deployment.

#### Functions
- `log` – timestamped stderr output.
- `die` – error log and exit.
- `usage` – prints usage from script comment.
- `check_prereqs` – verifies tools and SSH key.
- `load_hosts` – loads host list.
- `deploy_host` – deploys to a single host.
- `wait_healthy` – waits for health endpoint.
- `cleanup` – removes temporary lock file (unused).

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – `staging=hosts.staging`, `production=hosts.production`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`, `sudo`.
- Host files: `hosts.staging`, `hosts.production`.
- Remote service: `http://<host>:8080/health`.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, empty host list, or health timeout.
- `getopts` errors exit with status 1.
- Missing required options exit with status 2.
- `wait_healthy` exits with status 1 if health check fails after `HEALTH_TIMEOUT` seconds.
