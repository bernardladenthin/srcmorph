### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:29:27Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts of a chosen environment.
- Runs as a standalone script (not sourced).

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
  - `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production` (based on `ENVIRONMENT`)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `$SERVICE_NAME` (`orderd`) via `systemctl`.
- Checks health endpoint `http://$host:8080/health` until success or timeout.
- Logs actions to stderr.
- Exit status 0 on success; non‑zero on missing prereqs, invalid options, or health failure.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist; SSH key readable.
- `load_hosts`: read host list file, populate `HOSTS`.
- For each `host` in `HOSTS`:
  - `deploy_host`: if dry run, log and skip; otherwise:
    - `rsync` tarball to host.
    - SSH into host, create target dir, extract tarball, update symlink, restart service.
    - Call `wait_healthy` to poll health endpoint.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list into `HOSTS`.
- `deploy_host` – deploys tarball to a single host.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file (unused in current script).

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – associative array mapping environments to host list files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- External script: `$SCRIPT_DIR/lib/common.sh` (sourced but not detailed here).

#### Errors and exit codes
- `die` exits with status 1 for missing prereqs, unreadable SSH key, no hosts, health timeout, or unknown/invalid options.
- Usage errors exit with status 2.
- Health check failure triggers `die` with message `<host> not healthy after 30s`.
