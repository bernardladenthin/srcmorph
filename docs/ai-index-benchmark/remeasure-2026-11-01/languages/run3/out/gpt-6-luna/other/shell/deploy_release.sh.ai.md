### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:55:34Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each host in a staging or production environment, restarting the service and stopping if a host fails its health check.

#### Purpose
- Rolls out `build/$TAG.tar.gz` to environment hosts one at a time, restarting `orderd` and checking health after each host.
- Run directly as a deployment command; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts`:
  - `-e` sets `ENVIRONMENT`; expects `staging` or `production`.
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts an argument and logs that parallel deploys are unsupported; ignored.
  - `-h` prints usage and exits.
- Reads environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME` for that default.
- Reads `$SCRIPT_DIR/lib/common.sh`, the selected `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production`, and `build/$TAG.tar.gz`.
- Requires the configured SSH key to be readable.

#### Output
- Logs timestamped status and error messages to stderr.
- Transfers the release archive to each host’s `/tmp/`, extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd`.
- Sends health checks to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock file.
- Exit statuses: `0` on success or `-h`; `1` for `die` failures; `2` when required `-e` or `-t` is missing. Strict-mode failures may also terminate execution.

#### Core logic
- Parses options; prints usage for help or missing required environment and tag.
- Checks for `rsync`, `ssh`, and `curl`, and verifies the SSH key is readable.
- Loads non-comment lines from the selected host file; fails if no hosts are loaded.
- For each host, in order:
  - In dry-run mode, logs the intended deployment and skips deployment and health checks.
  - Otherwise, transfers the archive with `rsync`, then runs remote commands to create the release directory, extract the archive, update the `current` symlink, and restart `orderd`.
  - Polls the host health endpoint every two seconds until healthy or the timeout is reached.
  - Logs successful deployment; stops on the first failure.
- Runs the `EXIT` cleanup trap.

#### Functions
- `log` — Print timestamped messages to stderr.
- `die` — Log an error and exit.
- `usage` — Print usage from the script comment.
- `check_prereqs` — Check required commands and SSH key.
- `load_hosts` — Load hosts for the selected environment.
- `deploy_host` — Transfer, extract, restart, and check one host.
- `wait_healthy` — Poll a host health endpoint until healthy or timed out.
- `cleanup` — Remove the temporary lock path.

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
- External commands: `bash`, `cd`, `command`, `curl`, `date`, `grep`, `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Host files: `hosts.staging`, `hosts.production`.
- Remote operations use `"$DEPLOY_USER@$host"`, `/tmp/$TAG.tar.gz`, and `/opt/releases/$TAG`; restart `orderd` with `systemctl`.
- Health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- `die` exits `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or a host failing health checks.
- Missing `-e` or `-t` prints usage and exits `2`; `-h` prints usage and exits `0`.
- Unknown options and options missing a value call `die`.
- Health checks retry every two seconds; fail when `waited` reaches `HEALTH_TIMEOUT` (30 seconds).
- `trap cleanup EXIT` removes `/tmp/deploy_release.$$.lock`; no other traps or retries are defined.
