### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:12:18Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in an environment.
- Runs locally; not intended to be sourced.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- Options parsed with `getopts`:
  - `-e <environment>` → `ENVIRONMENT`
  - `-t <tag>` → `TAG`
  - `-n` → `DRY_RUN=1`
  - `-p <parallel>` → ignored, logged
  - `-h` → prints usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
- Files read:
  - `$SCRIPT_DIR/lib/common.sh`
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list)

#### Output
- Copies `build/$TAG.tar.gz` to each host via `rsync`.
- Extracts tarball on host, updates symlink `$RELEASE_ROOT/current`.
- Restarts `$SERVICE_NAME` (`orderd`) via `systemctl`.
- Checks health endpoint `http://$host:8080/health` until success or timeout.
- Prints status messages to stderr.
- Exit status 0 on success; non‑zero on errors.

#### Core logic
- Parse options → set `ENVIRONMENT`, `TAG`, `DRY_RUN`.
- Verify prerequisites (`rsync`, `ssh`, `curl`, SSH key).
- Load host list from environment‑specific file.
- For each host:
  - If `DRY_RUN`, log intended action.
  - Else:
    - `rsync` tarball to `/tmp/`.
    - SSH into host, create target dir, extract tarball, update symlink, restart service.
    - Wait for health endpoint to respond within `HEALTH_TIMEOUT`.
  - Log deployment completion.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS` array.
- `deploy_host` – performs rsync, SSH commands, and health check.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file (unused in current script).

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – `[staging]="hosts.staging" [production]="hosts.production"`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`, `sleep`, `date`.
- External file: `$SCRIPT_DIR/lib/common.sh` (sourced but not detailed here).
- Host list files: `hosts.staging`, `hosts.production`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Missing required options (`-e`, `-t`) cause exit status 2 after printing usage.
- Health check timeout (`HEALTH_TIMEOUT`) triggers `die` with status 1.
