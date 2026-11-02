### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:33:56Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each host in staging or production, restarting the service and stopping if a host fails its health check.

#### Purpose
- Rolls out `build/$TAG.tar.gz` host by host and verifies each host before continuing.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `-e` sets `ENVIRONMENT` (`staging` or `production`); `-t` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; logs intended deployments without deploying.
- `-p` accepts an argument but ignores it, logging that parallel deploys are unsupported.
- `-h` prints usage and exits.
- `DEPLOY_USER` environment variable defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME`.
- Reads `$SCRIPT_DIR/lib/common.sh`, the environment host file (`hosts.staging` or `hosts.production`), and `build/$TAG.tar.gz`; requires the SSH key to be readable.
- Remaining positional arguments are not used.

#### Output
- Logs timestamps and deployment messages to stderr.
- Copies the tarball to each remote host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, and restarts the remote service with `systemctl`.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this file.
- Exits `0` for help or successful completion, `2` when environment or tag is missing, and `1` via `die` on script-detected errors.

#### Core logic
- Parses options; requires nonempty `ENVIRONMENT` and `TAG`.
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Selects the host file for `ENVIRONMENT`, loads non-comment lines into `HOSTS`, and fails if the list is empty.
- Iterates over hosts in file order:
  - In dry-run mode, logs the intended deployment and skips deployment and health checks.
  - Otherwise, copies the tarball to the host, extracts it into the tagged release directory, updates the `current` symlink, and restarts `orderd`.
  - Polls the host health endpoint every two seconds; fails once the elapsed counter reaches `HEALTH_TIMEOUT`.
  - Logs successful deployment before moving to the next host.
- Runs `cleanup` on exit via the `EXIT` trap.

#### Functions
- `log`: Print timestamped stderr messages.
- `die`: Log an error and exit 1.
- `usage`: Print the usage comment.
- `check_prereqs`: Check required tools and SSH key.
- `load_hosts`: Load hosts for the selected environment.
- `deploy_host`: Deploy release and check host health.
- `wait_healthy`: Poll host health endpoint.
- `cleanup`: Remove the temporary lock path.

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
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production"`

#### Dependencies
- Sources `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `curl`, `date`, `grep`, `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Remote commands: `mkdir`, `tar`, `ln`, `sudo systemctl restart`.
- Uses host files `hosts.staging` and `hosts.production`; deploys `build/$TAG.tar.gz` to remote hosts over SSH.

#### Errors and exit codes
- `1`: `die` exits for missing tools, unreadable SSH key, unknown environment, empty host list, failed health check, unknown option, or option missing a value.
- `2`: Required `ENVIRONMENT` or `TAG` is empty.
- `0`: Help option or successful completion.
- `set -euo pipefail` can also terminate on command failures, unset-variable access, or pipeline failures.
- `wait_healthy` retries every two seconds; calls `die` when its elapsed counter reaches `HEALTH_TIMEOUT` (30 seconds).
- `EXIT` trap runs `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
