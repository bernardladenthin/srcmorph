### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:38:37Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host in sequence, restarting the service and stopping if a host fails its health check.

#### Purpose
- Rolls out `build/<tag>.tar.gz` to every host in the selected environment and restarts `orderd`.
- Runs directly as a deployment command; sources a shared shell library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts`:
  - `-e` sets `ENVIRONMENT` (`staging` or `production`).
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts a value and logs that parallel deployment is unsupported; ignored.
  - `-h` prints usage and exits.
- Environment variables read:
  - `DEPLOY_USER`, default `deploy`.
  - `SSH_KEY`, default `$HOME/.ssh/deploy_ed25519`; `HOME` supplies the default path.
- Reads `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production`, selected by `ENVIRONMENT`.
- Reads `build/$TAG.tar.gz` for upload and checks that `SSH_KEY` is readable.
- No positional arguments are used after option parsing.

#### Output
- Prints timestamped logs to standard error; usage text goes to standard output.
- Uploads the release tarball to each host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, and restarts `orderd` via `systemctl`.
- Sends health-check requests to `http://<host>:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this file.
- Exit statuses: `0` on successful deployment or help; `1` for `die` failures; `2` when required environment or tag is missing. Other command failures may propagate under `set -e`.

#### Core logic
- Parses options; requires both `ENVIRONMENT` and `TAG`.
- Checks for `rsync`, `ssh`, and `curl`, and verifies the SSH key is readable.
- Loads non-comment lines from the environment’s host file; fails if no hosts are loaded.
- Iterates hosts sequentially:
  - In dry-run mode, logs the planned deployment and skips deployment and health checks.
  - Otherwise, uploads the tarball with `rsync`, then remotely creates the release directory, extracts the archive, updates the `current` symlink, and restarts the service.
  - Polls the host health endpoint every two seconds; fails when the timeout is reached.
  - Logs successful deployment before moving to the next host.

#### Functions
- `log`: Print timestamped message to stderr.
- `die`: Log error and exit.
- `usage`: Print usage comment.
- `check_prereqs`: Check tools and SSH key.
- `load_hosts`: Read hosts for environment.
- `deploy_host`: Deploy release and check health.
- `wait_healthy`: Poll host health endpoint.
- `cleanup`: Remove deployment lock path.

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
- `HOSTS=()` (array)
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")` (associative array)

#### Dependencies
- Sources `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `cd`, `dirname`, `pwd`, `sed`, `date`, `command`, `grep`, `rsync`, `ssh`, `mkdir`, `tar`, `ln`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.
- Remote host commands: `mkdir`, `tar`, `ln`, `sudo systemctl restart`.
- Uses host lists `hosts.staging` and `hosts.production`; health endpoint path `/health` on port `8080`.

#### Errors and exit codes
- `die` exits `1` for missing tools, unreadable SSH key, unknown environment lookup, empty host list, or unhealthy host after the timeout.
- Missing `-e` or `-t` exits `2`; `-h` exits `0`.
- Unknown options and options missing values call `die` and exit `1`.
- `wait_healthy` retries every two seconds until health succeeds or `HEALTH_TIMEOUT` is reached.
- `EXIT` trap runs `cleanup`, removing `/tmp/deploy_release.$$.lock`.
