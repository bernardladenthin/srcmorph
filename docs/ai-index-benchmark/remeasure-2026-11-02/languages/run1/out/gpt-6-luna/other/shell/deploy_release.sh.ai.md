### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:23:47Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploy a tagged release archive to each staging or production host in sequence, restart `orderd`, and stop if a host fails its health check.

#### Purpose
- Deploys one release to every host in the selected environment, one host at a time.
- Run directly as `deploy_release.sh`; it sources `lib/common.sh`.

#### Type
- Bash; strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed with `getopts`.
- `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; parsed with `getopts`.
- `-p <parallel>` is accepted but ignored; parsed with `getopts`.
- `-h` prints usage and exits; parsed with `getopts`.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`.
- Reads the sourced `$SCRIPT_DIR/lib/common.sh`, the selected `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production`, and `build/$TAG.tar.gz`.
- Usage reads the script file at `$0`; prerequisite checks read the SSH key at `$SSH_KEY`.

#### Output
- Copies the release archive to each host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, points `$RELEASE_ROOT/current` at the release, and restarts `orderd`.
- Probes `http://$host:8080/health`; logs timestamps and deployment messages to stderr, and prints usage for `-h` or missing required arguments.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that lock.
- Exit status `0` on successful completion or `-h`; `1` for `die` errors and strict-mode failures; `2` when `-e` or `-t` is missing.

#### Core logic
- Parses options, then requires nonempty `ENVIRONMENT` and `TAG`.
- Checks that `rsync`, `ssh`, and `curl` are available and that `SSH_KEY` is readable.
- Loads hosts from the selected environment file, excluding lines beginning with `#`; fails if none remain.
- Iterates through hosts sequentially. In dry-run mode, logs the intended deployment and skips deployment and health checks.
- Otherwise, copies `build/$TAG.tar.gz` to the host, extracts it remotely, updates the `current` symlink, and restarts `orderd`.
- Polls the host health endpoint every two seconds; fails when the elapsed counter reaches `HEALTH_TIMEOUT`.
- Logs each successful host deployment; the `EXIT` trap runs cleanup on script exit.

#### Functions
- `log`: Timestamped stderr logging.
- `die`: Log an error and exit 1.
- `usage`: Print the usage comment.
- `check_prereqs`: Check tools and SSH key readability.
- `load_hosts`: Read hosts for the selected environment.
- `deploy_host`: Deploy the archive and restart the service on one host.
- `wait_healthy`: Poll a host’s health endpoint.
- `cleanup`: Remove the deployment lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`
- `RELEASE_ROOT="/opt/releases"`
- `SERVICE_NAME="orderd"`
- `HEALTH_TIMEOUT=30`
- `ENVIRONMENT=""`
- `TAG=""`
- `DRY_RUN=0`
- `HOSTS=()`
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `command`, `date`, `grep`, `curl`, `rsync`, `ssh`, `tar`, `mkdir`, `ln`, `sudo`, `systemctl`, `sleep`, `rm`, `sed`.
- Remote host operations use `DEPLOY_USER@$host`, the archive `/tmp/$TAG.tar.gz`, release path `/opt/releases/$TAG`, and service `orderd`.
- Health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- `die` exits 1 for missing tools, unreadable SSH key, unknown environment, empty host list, or a host failing its health check.
- `getopts` errors call `die` for unknown options or options missing a value.
- Missing `ENVIRONMENT` or `TAG` prints usage and exits 2.
- Health checks retry at two-second intervals and fail when `waited` reaches `HEALTH_TIMEOUT` (30 seconds).
- `EXIT` trap calls `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
