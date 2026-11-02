### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:10:45Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host sequentially and stops when a host fails its health check.

#### Purpose
- Rolls out `build/<tag>.tar.gz`, restarts `orderd`, and checks each host before continuing.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; `-t <tag>` sets `TAG`.
- `-n` sets `DRY_RUN=1`; logs intended deployments without deploying.
- `-p <parallel>` logs that parallel deployments are unsupported and ignores the value.
- `-h` prints usage and exits.
- Options parsed with `getopts`; extra positional arguments are shifted away.
- Environment: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; `HOME` supplies the default key path.
- Reads `lib/common.sh`, the selected host list (`hosts.staging` or `hosts.production`), and `build/$TAG.tar.gz` during deployment.
- Requires the SSH key file to be readable.

#### Output
- Sends the release archive to each host’s `/tmp/` via `rsync`.
- On each remote host, creates `/opt/releases/$TAG`, extracts the archive, updates `/opt/releases/current`, and restarts `orderd` with `systemctl`.
- Prints timestamped deployment and error messages to standard error; prints usage for help or missing required options.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock file.
- Exits `0` after all hosts deploy, or on `-h`; exits `2` when `-e` or `-t` is missing. `die` exits `1`; strict mode also exits on unhandled command failures.

#### Core logic
- Parse options; reject unknown options and options missing required values.
- Require both `ENVIRONMENT` and `TAG`; check required tools and SSH key readability.
- Select the environment’s host file, omit lines beginning with `#`, and fail if no hosts remain.
- For each host in order:
  - In dry-run mode, log the intended deployment and return.
  - Otherwise, copy `build/$TAG.tar.gz` to the host and run remote commands to create the release directory, extract the tarball, update the `current` symlink, and restart `orderd`.
  - Poll `http://$host:8080/health` every two seconds until healthy; stop on timeout.
  - Log successful deployment before moving to the next host.

#### Functions
- `log` — timestamp and print a message.
- `die` — log an error and exit `1`.
- `usage` — print the usage comment.
- `check_prereqs` — verify tools and SSH key readability.
- `load_hosts` — load hosts for the selected environment.
- `deploy_host` — deploy a release and check host health.
- `wait_healthy` — poll a host health endpoint.
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
- Commands: `bash`, `cd`, `dirname`, `pwd`, `sed`, `date`, `printf`, `command`, `grep`, `mapfile`, `rsync`, `ssh`, `curl`, `sleep`, `rm`.
- Remote commands: `mkdir`, `tar`, `ln`, `sudo`, `systemctl`.
- Host lists: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Health endpoint: `http://$host:8080/health`; service: `orderd`.

#### Errors and exit codes
- `1`: `die` for missing tools, unreadable SSH key, unknown environment, empty host list, unhealthy host after timeout, unknown option, or option missing a value.
- `2`: required `ENVIRONMENT` or `TAG` is empty.
- `0`: all deployments finish, or help is requested.
- `wait_healthy` retries every two seconds; fails when accumulated wait reaches `HEALTH_TIMEOUT` (30 seconds).
- `EXIT` trap runs `cleanup`, removing `/tmp/deploy_release.$$.lock`.
