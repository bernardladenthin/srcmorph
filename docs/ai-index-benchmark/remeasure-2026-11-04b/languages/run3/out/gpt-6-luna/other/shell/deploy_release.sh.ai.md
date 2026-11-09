### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:19:43Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a tagged release to each host in a staging or production environment, restarting `orderd` and stopping if a host fails its health check.

#### Purpose
- Roll out `build/$TAG.tar.gz` to environment hosts one at a time.
- Run directly; sources a shared helper file.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed by `getopts`.
- `-t <tag>` sets `TAG`; parsed by `getopts`.
- `-n` sets `DRY_RUN=1`; parsed by `getopts`.
- `-p <parallel>` logs that parallel deploys are unsupported and ignores the value; parsed by `getopts`.
- `-h` prints usage and exits; parsed by `getopts`.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME`.
- Reads the selected host list under `$SCRIPT_DIR` (`hosts.staging` or `hosts.production`), the SSH key, and `build/$TAG.tar.gz`.

#### Output
- Prints timestamped logs to stderr.
- Transfers the release archive to each host’s `/tmp/`, extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd`.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that lock file.
- Exits `0` on success or help, `1` on `die` errors, and `2` when `ENVIRONMENT` or `TAG` is missing.

#### Core logic
- Checks that `rsync`, `ssh`, and `curl` are available and that the SSH key is readable.
- Loads non-comment lines from the selected host file into `HOSTS`; fails if no hosts are loaded.
- For each host, either logs the dry-run action or transfers the archive and runs remote commands to create the release directory, extract the archive, update the `current` symlink, and restart `orderd`.
- Polls the host health endpoint every two seconds until it succeeds or reaches `HEALTH_TIMEOUT`; then logs the host deployment as complete.
- Stops at the first failed host or health check.

#### Functions
- `log`: Timestamp and print a message.
- `die`: Log an error and exit `1`.
- `usage`: Print the usage line from the script.
- `check_prereqs`: Check tools and SSH key access.
- `load_hosts`: Read hosts for the selected environment.
- `deploy_host`: Deploy a release to one host.
- `wait_healthy`: Poll a host health endpoint.
- `cleanup`: Remove the lock-file path.

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
- Sources `$SCRIPT_DIR/lib/common.sh`.
- External commands: `date`, `sed`, `command`, `grep`, `mapfile`, `rsync`, `ssh`, `bash` (remote), `mkdir`, `tar`, `ln`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.
- Uses host-list files `hosts.staging` and `hosts.production`, archive `build/$TAG.tar.gz`, and health endpoint `http://$host:8080/health`.

#### Errors and exit codes
- `1`: Missing tool, unreadable SSH key, unknown environment, empty host list, or host not healthy before timeout.
- `1`: Unknown option or option missing a value.
- `2`: Required `ENVIRONMENT` or `TAG` absent.
- `0`: Help requested or all hosts deployed successfully.
- `EXIT` trap runs `cleanup`, removing `/tmp/deploy_release.$$.lock`.
- Health checks retry every two seconds, with a 30-second timeout.
