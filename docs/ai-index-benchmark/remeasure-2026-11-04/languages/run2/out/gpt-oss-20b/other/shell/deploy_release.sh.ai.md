### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:41Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in an environment.
- Runs as a standalone script.

#### Type
- `bash`
- `set -euo pipefail`

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
- Files read:
  - `"$SCRIPT_DIR/lib/common.sh"`
  - `"$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}"` (hosts list)

#### Output
- Logs to stderr.
- Copies `build/$TAG.tar.gz` to each host via `rsync`.
- Extracts tarball on host, updates symlink `current`, restarts `orderd` service.
- Verifies health endpoint `http://$host:8080/health`.
- Exits with status 1 on failure, 0 on success.

#### Core logic
- Parse options, validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS`.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service.
  - `wait_healthy`: poll health endpoint until success or timeout.
  - Log deployment.

#### Functions
- `log`: prints timestamped message to stderr.
- `die`: logs error and exits.
- `usage`: prints usage from script comment.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list into `HOSTS`.
- `deploy_host`: deploys tarball to a host.
- `wait_healthy`: waits for health endpoint.
- `cleanup`: removes temporary lock file (unused).

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
- `HOSTS`=`()`
- `HOST_FILES`=`([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- External file: `"$SCRIPT_DIR/lib/common.sh"`.
- Host list files: `hosts.staging`, `hosts.production`.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Missing required options triggers exit status 2.
- `wait_healthy` exits with status 1 if health check fails after `HEALTH_TIMEOUT` seconds.
