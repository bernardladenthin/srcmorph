### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:37Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release to all hosts of an environment.
- Runs as a standalone script.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- Options parsed by `getopts`:
  - `-e <environment>` → `ENVIRONMENT`.
  - `-t <tag>` → `TAG`.
  - `-n` → `DRY_RUN=1`.
  - `-p <parallel>` → ignored, logged.
  - `-h` → prints usage.
- Environment variables:
  - `DEPLOY_USER` (default `deploy`).
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`).
- Files read:
  - `$SCRIPT_DIR/lib/common.sh` (sourced).
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list).

#### Output
- Logs to stderr with timestamps.
- Deploys `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`.
- Restarts `orderd` via `systemctl`.
- Verifies health at `http://$host:8080/health`; exits on failure.
- Exit status 0 on success, 1 on error, 2 on missing arguments.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS`.
- For each host:
  - `deploy_host`:
    - If `DRY_RUN`, log and skip.
    - `rsync` tarball to host.
    - SSH into host, create target dir, extract tarball, symlink `current`, restart service.
    - Call `wait_healthy` to poll health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list into `HOSTS`.
- `deploy_host` – deploys to a single host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file (unused but defined).

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping environments to host list files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- Host list files: `hosts.staging`, `hosts.production`.
- Remote hosts: those listed in the selected host file.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, empty host list, or health check failure.
- Missing required options triggers usage and exit status 2.
- Health check timeout (`HEALTH_TIMEOUT` seconds) causes `die` with status 1.
