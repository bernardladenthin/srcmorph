### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:54:42Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploys a tagged release tarball to each staging or production host, restarts `orderd`, and stops if a host fails its health check.

#### Purpose
- Roll out `build/$TAG.tar.gz` host by host and verify each service restart.
- Run directly; sources `lib/common.sh`.

#### Type
- Bash; strict mode: `set -euo pipefail`.

#### Input
- `-e` sets `ENVIRONMENT` (`staging` or `production`); `-t` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; `-p` accepts a value but only logs that parallel deploys are unsupported; `-h` prints usage.
- `DEPLOY_USER` environment variable defaults to `deploy`.
- `SSH_KEY` environment variable defaults to `$HOME/.ssh/deploy_ed25519`; reads that key file.
- Reads `lib/common.sh`, the selected `hosts.staging` or `hosts.production` file, and `build/$TAG.tar.gz`.
- Reads the script’s `# Usage:` comment for help text.

#### Output
- Transfers the release archive to each host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, updates the `current` symlink, and restarts the service.
- Prints timestamped progress and errors to stderr.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this file.
- Exit status `0` on successful completion, dry run, or help; `2` if required environment or tag is missing; `1` for explicit `die` errors. Other command failures propagate under `set -e`.

#### Core logic
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Selects the host file for `ENVIRONMENT`, loads non-comment lines into `HOSTS`, and fails if none are found.
- Processes hosts sequentially. In dry-run mode, logs the planned deployment without connecting.
- Otherwise transfers the archive with `rsync`, then runs remote commands over `ssh` to create the release directory, extract the archive, update `current`, and restart `SERVICE_NAME`.
- Polls `http://$host:8080/health` with `curl`, sleeping two seconds between failures; stops deployment if the health timeout is reached.
- Logs successful deployment per host; exits on the first deployment or health-check failure.

#### Functions
- `log` — print timestamped messages to stderr.
- `die` — log an error and exit with status `1`.
- `usage` — print usage from the script comment.
- `check_prereqs` — check required tools and SSH key readability.
- `load_hosts` — load hosts for the selected environment.
- `deploy_host` — transfer, install, restart, and verify a release on one host.
- `wait_healthy` — poll a host’s health endpoint until healthy or timed out.
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
- `HOSTS=()` (indexed array)
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")` (associative array)

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `command`, `curl`, `date`, `grep`, `mapfile`, `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Host inputs: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Remote service endpoint: `http://$host:8080/health`; remote service: `orderd`.

#### Errors and exit codes
- `1`: missing required tool, unreadable SSH key, unknown environment, empty host list, or host health timeout; command failures also terminate under `set -e`.
- `2`: `ENVIRONMENT` or `TAG` is empty after option parsing.
- `0`: successful deployment, dry run, or `-h`.
- `EXIT` trap invokes `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
- No retries for deployment commands; health checks repeat at two-second intervals up to `HEALTH_TIMEOUT`.
