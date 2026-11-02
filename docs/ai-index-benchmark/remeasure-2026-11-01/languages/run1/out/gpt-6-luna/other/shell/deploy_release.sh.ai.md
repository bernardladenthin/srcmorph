### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:15:37Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each host in staging or production, restarting and health-checking the service host by host.

#### Purpose
- Rolls out `build/$TAG.tar.gz` to each host in the selected environment, stopping on the first failed deployment or health check.
- Directly executed deployment script; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Options parsed with `getopts`:
  - `-e` sets `ENVIRONMENT` (`staging` or `production`).
  - `-t` sets `TAG`.
  - `-n` sets `DRY_RUN=1`.
  - `-p` accepts an argument but only logs that parallel deploys are unsupported and ignores it.
  - `-h` prints usage and exits successfully.
- Reads environment variables `DEPLOY_USER` (default `deploy`), `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`), and `HOME` when expanding the default key path.
- Reads `lib/common.sh`, the selected `hosts.staging` or `hosts.production` file under `SCRIPT_DIR`, and `build/$TAG.tar.gz`.
- Reads the SSH key at `SSH_KEY`; usage text is extracted from the script’s `# Usage:` line.

#### Output
- Copies the release archive to each host’s `/tmp/`, then remotely extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd`.
- Prints timestamped log messages to stderr; prints usage for `-h` or missing required options.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that file.
- Exit status `0` on success or `-h`; `1` for `die` failures; `2` when `-e` or `-t` is missing. With `set -e`, other failing commands also terminate the script.

#### Core logic
- Parses options; rejects missing `ENVIRONMENT` or `TAG`.
- Checks that `rsync`, `ssh`, and `curl` are available and that `SSH_KEY` is readable.
- Selects the host-list file from `HOST_FILES`, removes lines beginning with `#`, and rejects an empty host list.
- For each host in order:
  - In dry-run mode, logs the intended deployment and skips deployment and health checking.
  - Otherwise, copies `build/$TAG.tar.gz` to the host using `rsync`.
  - Runs remote commands over SSH to create the release directory, extract the archive, update the `current` symlink, and restart the service.
  - Polls `http://$host:8080/health` with `curl`, sleeping two seconds between failures; stops if the health timeout is reached.
  - Logs the completed deployment before proceeding to the next host.

#### Functions
- `log`: Timestamp and print to stderr.
- `die`: Log an error and exit.
- `usage`: Print the usage line from the script.
- `check_prereqs`: Check commands and SSH key.
- `load_hosts`: Load hosts for the selected environment.
- `deploy_host`: Deploy a release to one host.
- `wait_healthy`: Poll a host’s health endpoint.
- `cleanup`: Remove the per-process lock path.

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
- `HOSTS=()`
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `command`, `curl`, `date`, `grep`, `mapfile` (Bash builtin), `mkdir`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Remote host operations use SSH as `$DEPLOY_USER@$host`; health checks use `http://$host:8080/health`.
- Remote commands extract the archive and update the release symlink under `/opt/releases`, then restart `orderd` with `systemctl`.

#### Errors and exit codes
- `1`: `die` is called for a missing prerequisite, unreadable SSH key, unknown environment, empty host list, failed health check, or invalid option.
- `2`: required `ENVIRONMENT` or `TAG` is absent.
- `0`: all hosts deploy successfully, or `-h` is used.
- Missing option arguments invoke `die`; `set -e` also exits on unhandled command failures.
- `EXIT` trap runs `cleanup`, removing `/tmp/deploy_release.$$.lock`.
- Health checks retry every two seconds until success or `HEALTH_TIMEOUT` is reached; no other retries or timeouts are defined.
