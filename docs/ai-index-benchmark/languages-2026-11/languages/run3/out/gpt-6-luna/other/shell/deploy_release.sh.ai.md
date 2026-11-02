### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:53:50Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host in sequence, restarting `orderd` and stopping if a host fails its health check.

#### Purpose
- Deploys `build/<tag>.tar.gz` to every host listed for the selected environment, one host at a time.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts`:
  - `-e` sets `ENVIRONMENT` (`staging` or `production`).
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts an argument and logs that parallel deployment is unsupported; the argument is held in `OPTARG`.
  - `-h` prints usage and exits.
- Reads `DEPLOY_USER`, defaulting to `deploy`; reads `SSH_KEY`, defaulting to `$HOME/.ssh/deploy_ed25519`.
- Reads the SSH key at `SSH_KEY`, the selected host file under `SCRIPT_DIR`, and `build/$TAG.tar.gz`.

#### Output
- Prints timestamped logs to stderr; `-n` logs intended deployments without deploying.
- Copies the release archive to each host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, restarts `orderd`, and checks `http://<host>:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that file.
- Exits `0` on success or help, `1` for `die` failures, and `2` when `ENVIRONMENT` or `TAG` is missing. Other command failures propagate under `set -e`.

#### Core logic
- Parses options; requires nonempty `ENVIRONMENT` and `TAG`.
- Checks that `rsync`, `ssh`, and `curl` are available and that `SSH_KEY` is readable.
- Selects `hosts.staging` or `hosts.production`; loads lines not beginning with `#` into `HOSTS` and fails if none are present.
- For each host, either logs the dry-run action or copies the archive, then remotely creates the release directory, extracts the archive, updates the `current` symlink, and restarts `orderd`.
- Polls the host health endpoint every two seconds; fails when `HEALTH_TIMEOUT` is reached.
- Logs successful deployment per host; stops on the first failure.

#### Functions
- `log`: Print timestamped stderr messages.
- `die`: Log an error and exit 1.
- `usage`: Print usage from the script comment.
- `check_prereqs`: Validate commands and SSH key.
- `load_hosts`: Read hosts for the selected environment.
- `deploy_host`: Deploy release and invoke health check.
- `wait_healthy`: Poll host health endpoint.
- `cleanup`: Remove the temporary lock path.

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
- Sources `$SCRIPT_DIR/lib/common.sh`.
- Commands: `date`, `sed`, `command`, `grep`, `rsync`, `ssh`, remote `bash`, `mkdir`, `tar`, `ln`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.
- Reads `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production`; deploys `build/$TAG.tar.gz`.
- Uses remote host SSH access as `$DEPLOY_USER`; checks `http://<host>:8080/health` and restarts the `orderd` systemd service.

#### Errors and exit codes
- `die` exits `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or failed health check.
- Missing `ENVIRONMENT` or `TAG` prints usage and exits `2`; `-h` prints usage and exits `0`.
- Unknown options and options missing values call `die`.
- `EXIT` trap runs `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
- Health polling retries every two seconds up to `HEALTH_TIMEOUT=30`; no separate command timeout is configured.
