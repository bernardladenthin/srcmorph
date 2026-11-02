### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:39:07Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in a chosen environment.
- Runs as a standalone script, not sourced.

#### Type
- `bash`
- `set -euo pipefail`

#### Input
- `-e <environment>` → `ENVIRONMENT`
- `-t <tag>` → `TAG`
- `-n` (dry run) → `DRY_RUN=1`
- `-p <parallel>` → ignored, logged
- `-h` → prints usage
- `DEPLOY_USER` (env) → defaults to `deploy`
- `SSH_KEY` (env) → defaults to `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` (env) → defaults to `/opt/releases`
- `SERVICE_NAME` → `orderd`
- `HEALTH_TIMEOUT` → `30`
- `HOST_FILES` mapping: `staging` → `hosts.staging`, `production` → `hosts.production`
- Host list read from the file corresponding to `ENVIRONMENT`.

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG`, updates symlink `$RELEASE_ROOT/current`.
- Restarts `systemctl restart orderd` on each host.
- Checks health at `http://$host:8080/health`; exits if timeout.
- Prints deployment status to stderr.
- Creates and removes `/tmp/deploy_release.$$.lock` (unused lock file).

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment-specific file.
- For each host:
  - If dry run: log action.
  - Else: `rsync` tarball, SSH into host to extract, symlink, restart service.
  - Wait for health endpoint; abort on failure.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS`.
- `deploy_host` – deploys tarball to a single host.
- `wait_healthy` – polls health endpoint until timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR`=`$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)`
- `DEPLOY_USER`=`${DEPLOY_USER:-deploy}`
- `SSH_KEY`=`${SSH_KEY:-$HOME/.ssh/deploy_ed25519}`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `HOST_FILES`=[staging]="hosts.staging" [production]="hosts.production"

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `date`, `sed`, `grep`, `mapfile`, `mkdir`, `tar`, `systemctl`.
- SSH key file: `$SSH_KEY`.
- Host list files: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.

#### Errors and exit codes
- `die` exits with status `1` for missing tools, unreadable key, empty host list, or health timeout.
- `usage` prints help and exits with status `0`.
- Missing required options cause exit status `2`.
- `wait_healthy` exits with status `1` if health check fails after `HEALTH_TIMEOUT` seconds.
