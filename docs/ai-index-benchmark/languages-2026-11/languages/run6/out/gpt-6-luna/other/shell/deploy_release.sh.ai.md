### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:37:29Z
- G: 
- A: 0.0.0
- X: file
---
> Rolls a tagged release across an environment’s hosts one at a time, restarting and health-checking the service on each host.

#### Purpose
- Deploys a release tarball to staging or production, stopping if a host fails its health check.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash via `#!/usr/bin/env bash`; strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts`:
  - `-e` sets `ENVIRONMENT` (`staging` or `production`).
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts an argument and logs that parallel deploys are unsupported; the argument is ignored.
  - `-h` prints usage and exits successfully.
- Environment variables:
  - `DEPLOY_USER`, default `deploy`.
  - `SSH_KEY`, default `$HOME/.ssh/deploy_ed25519`; reads `HOME` when the default is needed.
- Reads the selected host file under `SCRIPT_DIR` (`hosts.staging` or `hosts.production`), the SSH key, and `build/$TAG.tar.gz`.

#### Output
- Copies the tarball to each host’s `/tmp/`, extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd` with `sudo systemctl`.
- Prints timestamped progress and errors to stderr; probes `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this file.
- Exits `0` after successful deployment or for `-h`; exits `1` on `die` errors; exits `2` when `-e` or `-t` is missing. Strict mode can also terminate on command failures.

#### Core logic
- Parses options; requires nonempty `ENVIRONMENT` and `TAG`.
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Loads non-comment lines from the selected host file into `HOSTS`; fails if the list is empty.
- For each host, either logs the dry-run action or copies `build/$TAG.tar.gz` with `rsync`.
- Runs remote commands over SSH to create the release directory, extract the tarball, update the `current` symlink, and restart `orderd`.
- Polls the host health endpoint every two seconds; fails once the elapsed counter reaches `HEALTH_TIMEOUT`.
- Logs successful deployment per host; the loop stops at the first failure.

#### Functions
- `log`: Timestamp and print to stderr.
- `die`: Log an error and exit 1.
- `usage`: Print the usage comment.
- `check_prereqs`: Check required tools and SSH key readability.
- `load_hosts`: Read hosts for the selected environment.
- `deploy_host`: Deploy and restart a release on one host.
- `wait_healthy`: Poll a host’s health endpoint.
- `cleanup`: Remove the per-process lock path.

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
- Sources `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `cd`, `command`, `curl`, `date`, `dirname`, `grep`, `mkdir`, `mapfile`, `printf`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Host files: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Deployment artifact: `build/$TAG.tar.gz`.
- Remote host operations use `/tmp/$TAG.tar.gz` and `/opt/releases/$TAG`; health endpoint is `http://$host:8080/health`.

#### Errors and exit codes
- `1`: `die` exits for a missing tool, unreadable SSH key, unknown environment, empty host list, unhealthy host, unknown option, or option missing a value.
- `2`: Required `ENVIRONMENT` or `TAG` is empty.
- `0`: All host deployments complete, or `-h` prints usage.
- `EXIT` trap runs `cleanup`, which removes `/tmp/deploy_release.$$.lock`; no retries beyond health polling, which uses a 30-second threshold and two-second sleeps.
