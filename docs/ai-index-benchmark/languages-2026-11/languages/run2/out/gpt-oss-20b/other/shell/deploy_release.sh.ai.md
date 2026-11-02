### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:34:34Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a release tarball to all hosts of a specified environment.
- Runs as a standalone script, not sourced.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- Options parsed with `getopts`:
  - `-e <environment>` → `ENVIRONMENT`
  - `-t <tag>` → `TAG`
  - `-n` → `DRY_RUN=1`
  - `-p <parallel>` → ignored, logged
  - `-h` → prints usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
- Files read:
  - `hosts.staging` or `hosts.production` in `$SCRIPT_DIR`
  - `build/$TAG.tar.gz` (source tarball)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current` → `$RELEASE_ROOT/$TAG`.
- Restarts `orderd` service via `systemctl`.
- Checks health endpoint `http://$host:8080/health` until success or timeout.
- Logs actions to stderr.
- Exit status 0 on success; non‑zero on missing prereqs, invalid options, or health failure.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list from environment-specific file.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service.
  - `wait_healthy`: poll health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log`: prints timestamped message to stderr.
- `die`: logs error and exits.
- `usage`: prints usage comment from script header.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list into `HOSTS` array.
- `deploy_host`: deploys tarball to a single host.
- `wait_healthy`: polls health endpoint.
- `cleanup`: removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR`=`$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)`
- `DEPLOY_USER`=`${DEPLOY_USER:-deploy}`
- `SSH_KEY`=`${SSH_KEY:-$HOME/.ssh/deploy_ed25519}`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `HOST_FILES`=`([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- External script: `lib/common.sh` (sourced for shared utilities).
- SSH key file: `$SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Invalid options or missing required options exit with status 2.
