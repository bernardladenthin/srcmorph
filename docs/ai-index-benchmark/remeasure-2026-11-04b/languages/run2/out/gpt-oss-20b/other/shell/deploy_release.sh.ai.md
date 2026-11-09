### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:03Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release to all hosts in an environment.
- Runs as a standalone script.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
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
  - `$SCRIPT_DIR/lib/common.sh`
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list)

#### Output
- Logs to stderr.
- Copies `build/$TAG.tar.gz` to each host via `rsync`.
- Extracts tarball on host, updates symlink `$RELEASE_ROOT/current`.
- Restarts `orderd` service via `systemctl`.
- Verifies health at `http://$host:8080/health`.
- Exits with status 1 on errors, 0 on success.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS`.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service.
  - `wait_healthy`: poll health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log`: prints timestamped message to stderr.
- `die`: logs error and exits.
- `usage`: prints usage comment from script header.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list from environment-specific file.
- `deploy_host`: deploys release to a single host.
- `wait_healthy`: waits for service health check.
- `cleanup`: removes temporary lock file (unused in current code).

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
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`, `sudo`.
- External file: `$SCRIPT_DIR/lib/common.sh`.
- Host files: `hosts.staging`, `hosts.production`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, no hosts, health timeout, unknown option, or missing option value.
- `usage` prints help and exits with status 0 or 2 when required arguments missing.
