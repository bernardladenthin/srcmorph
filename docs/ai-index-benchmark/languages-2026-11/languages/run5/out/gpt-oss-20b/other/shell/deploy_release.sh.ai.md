### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:35:38Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a release tarball to every host in a specified environment.
- Restarts the service on each host and checks health.
- Can run in dry‑run mode.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- Options parsed by `getopts`:
  - `-e <environment>` → `ENVIRONMENT`
  - `-t <tag>` → `TAG`
  - `-n` → `DRY_RUN=1`
  - `-p <parallel>` → ignored, logged
  - `-h` → prints usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
  - `RELEASE_ROOT` (exported `/opt/releases`)
- Files read:
  - `"$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}"` (hosts list)
  - `build/$TAG.tar.gz` (tarball to deploy)

#### Output
- Logs to `stderr` with timestamps.
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on each host.
- Creates symlink `$RELEASE_ROOT/current` → `$RELEASE_ROOT/$TAG`.
- Restarts `systemctl restart "$SERVICE_NAME"` on each host.
- Checks health at `http://$host:8080/health`; exits on failure.
- Removes temporary lock file on exit.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS` array.
- For each `host` in `HOSTS`:
  - `deploy_host`:
    - If `DRY_RUN`, log and skip.
    - `rsync` tarball to host.
    - SSH into host, create target dir, extract tarball, update symlink, restart service.
    - Call `wait_healthy` to poll health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to `stderr`.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list for the chosen environment.
- `deploy_host` – deploys tarball to a single host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR`=`$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)`
- `DEPLOY_USER`=`${DEPLOY_USER:-deploy}`
- `SSH_KEY`=`${SSH_KEY:-$HOME/.ssh/deploy_ed25519}`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `ENVIRONMENT`=`""`
- `TAG`=`""`
- `DRY_RUN`=`0`
- `HOSTS`=()
- `HOST_FILES`=([staging]="hosts.staging" [production]="hosts.production")

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- SSH key file: `$SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production` in script directory.
- Tarball: `build/$TAG.tar.gz`.

#### Errors and exit codes
- `die` exits with status `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or health check failure.
- `usage` exits with status `0` when `-h` is supplied.
- Missing required options (`-e` or `-t`) exit with status `2`.
- `wait_healthy` exits with status `1` if health check does not succeed within `HEALTH_TIMEOUT` seconds.
