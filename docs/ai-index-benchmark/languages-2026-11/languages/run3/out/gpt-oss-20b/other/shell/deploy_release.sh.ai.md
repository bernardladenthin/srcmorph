### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:55:48Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a release tarball to all hosts in a specified environment.
- Runs locally; not sourced.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- Options parsed by `getopts`:
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
- Extracts tarball on host, creates symlink `$RELEASE_ROOT/current`.
- Restarts `$SERVICE_NAME` (`orderd`) via `systemctl`.
- Checks health endpoint `http://$host:8080/health` until success or timeout.
- Prints deployment status to stderr.
- Exit status 0 on success; non‑zero on errors or health failure.

#### Core logic
- Parse options → set `ENVIRONMENT`, `TAG`, `DRY_RUN`.
- Verify prerequisites (`rsync`, `ssh`, `curl`, SSH key readable).
- Load host list from environment‑specific file.
- For each host:
  - If `DRY_RUN`: log action, skip.
  - `rsync` tarball to `/tmp/`.
  - SSH into host, create target dir, extract tarball, update symlink, restart service.
  - Poll health endpoint until success or timeout.
  - Log success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS` array.
- `deploy_host` – deploys tarball to a single host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping environments to host list files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `date`, `mkdir`, `tar`, `ln`, `sudo`, `systemctl`.
- Host list files: `hosts.staging`, `hosts.production`.
- SSH key file: `$SSH_KEY`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable key, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Missing required options → prints usage, exits 2.
- `wait_healthy` exits with status 1 if health check fails after `HEALTH_TIMEOUT` seconds.
