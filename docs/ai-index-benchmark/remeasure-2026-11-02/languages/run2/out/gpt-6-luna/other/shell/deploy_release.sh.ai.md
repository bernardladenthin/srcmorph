### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:40:08Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploys a tagged order-service release to each host in a staging or production environment, restarting and health-checking hosts sequentially.

#### Purpose
- Rolls out `build/<tag>.tar.gz` to the selected environment and stops at the first failed deployment or health check.
- Runs directly as a deployment command; sources `lib/common.sh`.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed with `getopts`.
- `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; parsed with `getopts`.
- `-p <parallel>` is accepted but ignored; parsed with `getopts`.
- `-h` prints usage and exits; parsed with `getopts`.
- `DEPLOY_USER` environment variable; defaults to `deploy`.
- `SSH_KEY` environment variable; defaults to `$HOME/.ssh/deploy_ed25519`; reads the key file.
- `HOME` supplies the default SSH-key directory.
- Reads the selected host list: `hosts.staging` or `hosts.production` under `SCRIPT_DIR`.
- Reads `build/$TAG.tar.gz` for deployment.

#### Output
- Logs timestamps and deployment messages to standard error.
- Transfers the release archive to each host’s `/tmp/`; extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, and restarts `SERVICE_NAME`.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this file.
- Exits `0` on successful completion or `-h`; exits `2` when `ENVIRONMENT` or `TAG` is missing; exits `1` through `die` for reported errors. With `set -e`, unhandled command failures also terminate the script.

#### Core logic
- Parses options; rejects unknown options and options missing required values.
- Requires both `ENVIRONMENT` and `TAG`; prints usage and exits `2` otherwise.
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Selects the environment’s host-list file, skips lines beginning with `#`, and fails if the resulting host list is empty.
- Processes hosts sequentially. In dry-run mode, logs the intended deployment and skips transfer, restart, and health check.
- For each deployment, transfers the archive with `rsync`, then runs remote commands over `ssh` to create the release directory, extract the archive, update the `current` symlink, and restart the service.
- Polls the host health endpoint every two seconds, failing when `HEALTH_TIMEOUT` is reached; logs success after each host completes.
- Runs `cleanup` on exit via the `EXIT` trap.

#### Functions
- `log` — timestamp and print messages to standard error.
- `die` — log an error and exit `1`.
- `usage` — print the usage comment from the script.
- `check_prereqs` — check required commands and SSH-key readability.
- `load_hosts` — load hosts for the selected environment.
- `deploy_host` — transfer, install, restart, and health-check one host.
- `wait_healthy` — poll a host’s health endpoint until healthy or timeout.
- `cleanup` — remove the temporary lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"` (readonly)
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`
- `RELEASE_ROOT="/opt/releases"` (exported)
- `SERVICE_NAME="orderd"`
- `HEALTH_TIMEOUT=30`
- `ENVIRONMENT=""`
- `TAG=""`
- `DRY_RUN=0`
- `HOSTS=()` (declared indexed array)
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")` (declared associative array)

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- Commands: `date`, `sed`, `command`, `grep`, `rsync`, `ssh`, `bash` (remote), `mkdir`, `tar`, `ln`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.
- Remote service: `orderd`, restarted with `systemctl`.
- Health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- `die` exits `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or a host failing its health check.
- Missing required `-e` or `-t` values causes usage output and exit `2`.
- `-h` prints usage and exits `0`; unknown options and missing option values call `die`.
- Health checks retry every two seconds and fail once the elapsed counter reaches `HEALTH_TIMEOUT` (30 seconds).
- `EXIT` trap calls `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
