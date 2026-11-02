### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:27:07Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host and stops if a host fails its health check.

#### Purpose
- Rolls out one release tarball to environment hosts, one at a time, restarting the service on each host.
- Intended to run directly; sources `lib/common.sh`.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; logs intended deployments without deploying.
- `-p <parallel>` is accepted but ignored; logs that parallel deploys are unsupported.
- `-h` prints usage and exits.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME`.
- Reads `lib/common.sh`, the selected `hosts.staging` or `hosts.production` file, and `build/$TAG.tar.gz`; requires the SSH key to be readable.

#### Output
- Logs timestamped messages to stderr.
- On each remote host, creates `$RELEASE_ROOT/$TAG`, extracts the tarball there, updates `$RELEASE_ROOT/current`, and restarts `SERVICE_NAME` with `sudo systemctl`.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create it.
- Exits `0` after successful completion or for `-h`; exits `2` if `ENVIRONMENT` or `TAG` is missing; `die` exits `1`.

#### Core logic
- Parses options; requires both `ENVIRONMENT` and `TAG`.
- Checks that `rsync`, `ssh`, and `curl` are available and that `SSH_KEY` is readable.
- Loads hosts from the environment-specific file, skipping lines beginning with `#`; fails if none are loaded.
- Iterates through hosts in file order:
  - In dry-run mode, logs the planned deployment and skips deployment and health checks.
  - Otherwise, copies `build/$TAG.tar.gz` to the host’s `/tmp/` with `rsync`, then uses SSH to extract it, update the `current` symlink, and restart the service.
  - Polls `http://$host:8080/health` with `curl`, sleeping two seconds between failures; stops if the health timeout is reached.
  - Logs the host as deployed after `deploy_host` returns successfully.

#### Functions
- `log`: timestamped stderr logging.
- `die`: logs an error and exits.
- `usage`: prints the usage comment.
- `check_prereqs`: verifies required commands and SSH key.
- `load_hosts`: reads hosts for the selected environment.
- `deploy_host`: deploys and health-checks one host.
- `wait_healthy`: polls a host health endpoint.
- `cleanup`: removes the lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`.
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`.
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`.
- `RELEASE_ROOT="/opt/releases"`.
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `ENVIRONMENT=""`.
- `TAG=""`.
- `DRY_RUN=0`.
- `HOSTS=()`.
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`.

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `command`, `curl`, `date`, `grep`, `ln`, `mapfile`, `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Remote host operations: SSH runs `mkdir`, `tar`, `ln`, and `sudo systemctl restart` for `orderd`; health checks use `http://$host:8080/health`.

#### Errors and exit codes
- `1`: `die` on missing prerequisite command, unreadable SSH key, unknown environment, empty host list, unsupported option, missing option value, or health timeout.
- `2`: missing `ENVIRONMENT` or `TAG`.
- `0`: successful run or `-h`.
- `EXIT` trap invokes `cleanup` to remove `/tmp/deploy_release.$$.lock`.
- Health polling retries every two seconds until success or `HEALTH_TIMEOUT=30`; deployment stops on the first failing host.
