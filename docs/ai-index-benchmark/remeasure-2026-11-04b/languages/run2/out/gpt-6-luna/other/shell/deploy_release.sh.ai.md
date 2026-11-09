### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:17Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a tagged release tarball to each staging or production host in sequence, restarting `orderd` and stopping on the first failed health check.

#### Purpose
- Rolls out one release to every host in the selected environment, one host at a time.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash; strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts ":e:t:np:h"`:
  - `-e` sets `ENVIRONMENT` (`staging` or `production`).
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts a value but only logs that parallel deployment is unsupported.
  - `-h` prints usage and exits.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME`.
- Reads the selected `hosts.staging` or `hosts.production` file under `SCRIPT_DIR`, and `build/$TAG.tar.gz`.

#### Output
- Logs timestamps and deployment messages to standard error.
- On real deployments, uploads the tarball to each host’s `/tmp/`, extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd`.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that lock file.
- Exit statuses: `0` for success or help; `1` for `die` failures; `2` when `-e` or `-t` is missing. With strict mode, failing commands may also terminate with their command status.

#### Core logic
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Selects the host list for `ENVIRONMENT`, excludes lines beginning with `#`, and fails if no hosts remain.
- For each host, either logs the dry-run action or uploads `build/$TAG.tar.gz` using `rsync`.
- Runs remote commands over SSH to create the release directory, extract the tarball, update the `current` symlink, and restart the service with `sudo systemctl`.
- Polls `http://$host:8080/health` every two seconds until healthy; fails at the `HEALTH_TIMEOUT` limit.
- Logs successful deployment per host; stops on the first failure.

#### Functions
- `log`: Timestamp and print a message.
- `die`: Log an error and exit with status 1.
- `usage`: Print the usage comment.
- `check_prereqs`: Check required commands and SSH key.
- `load_hosts`: Load hosts for the selected environment.
- `deploy_host`: Upload, install, restart, and check one host.
- `wait_healthy`: Poll a host’s health endpoint.
- `cleanup`: Remove the named temporary lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`.
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`.
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`.
- `RELEASE_ROOT="/opt/releases"` (exported).
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `ENVIRONMENT=""`.
- `TAG=""`.
- `DRY_RUN=0`.
- `HOSTS=()`.
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`.

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `cd`, `command`, `curl`, `date`, `grep`, `ln`, `mapfile`, `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Hosts: entries in `hosts.staging` and `hosts.production`.
- Endpoint: `http://$host:8080/health`.
- Remote service: `orderd`, restarted through `sudo systemctl`.

#### Errors and exit codes
- `die` exits `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or a host unhealthy after the timeout.
- Missing `-e` or `-t` prints usage and exits `2`; `-h` prints usage and exits `0`.
- Unknown options and options missing values call `die`.
- Health checks retry every two seconds up to `HEALTH_TIMEOUT=30`.
- `EXIT` trap calls `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
