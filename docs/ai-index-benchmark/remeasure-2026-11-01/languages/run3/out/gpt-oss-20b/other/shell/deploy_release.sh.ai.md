### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:57:32Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Roll out a release tarball to all hosts in a specified environment.
- Restart the `orderd` service on each host.
- Stop deployment if a host fails its health check.
- Can be run directly from the command line.

#### Type
- `bash`
- `set -euo pipefail`

#### Input
- `-e <environment>` → `ENVIRONMENT` (`staging` or `production`)
- `-t <tag>` → `TAG`
- `-n` → `DRY_RUN=1`
- `-p <parallel>` → ignored (logged)
- `-h` → prints usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
  - `RELEASE_ROOT` (exported `/opt/releases`)
- Files read:
  - `$SCRIPT_DIR/lib/common.sh`
  - `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production` (based on `ENVIRONMENT`)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current` → `$RELEASE_ROOT/$TAG`.
- Restarts `orderd` service via `systemctl`.
- Prints deployment status to stderr.
- Exits with status 1 on errors, 0 on success.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist; SSH key readable.
- `load_hosts`: read host list from environment-specific file.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service.
  - `wait_healthy`: poll `/health` endpoint until success or timeout.
  - Log deployment message.

#### Functions
- `log`: timestamped stderr output.
- `die`: log error and exit.
- `usage`: prints usage comment from script header.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list into `HOSTS`.
- `deploy_host`: deploys release to a single host.
- `wait_healthy`: waits for host health endpoint.
- `cleanup`: removes temporary lock file (unused in script).

#### Constants and configuration
- `SCRIPT_DIR`=`$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)`
- `DEPLOY_USER`=`${DEPLOY_USER:-deploy}`
- `SSH_KEY`=`${SSH_KEY:-$HOME/.ssh/deploy_ed25519}`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `HOST_FILES`=`([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`, `sudo`.
- External file: `$SCRIPT_DIR/lib/common.sh` (sourced but not detailed here).

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` prints usage and exits with status 0.
- `getopts` errors exit with status 1.
- Deployment stops on first host that fails health check (`die` invoked).
