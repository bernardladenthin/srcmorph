### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:36Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in a chosen environment.
- Restarts the `orderd` service on each host.
- Stops deployment if a host fails its health check.
- Can run in dry‑run mode.

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
  - `hosts.staging` or `hosts.production` in `$SCRIPT_DIR`
  - `build/<TAG>.tar.gz` (source tarball)

#### Output
- Copies `build/<TAG>.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current` pointing to the new release.
- Restarts `orderd` service via `systemctl`.
- Checks health at `http://<host>:8080/health`; aborts if timeout.
- Logs actions to stderr.
- Exit status 0 on success, 1 on error.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment‑specific file.
- For each host:
  - If dry‑run: log intended action.
  - Else:
    - `rsync` tarball to host.
    - SSH into host, extract tarball, update symlink, restart service.
    - Wait for health endpoint, abort on timeout.
  - Log successful deployment.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS` array.
- `deploy_host` – deploys release to a single host.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – associative array mapping environments to host list files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`.
- Host list files: `hosts.staging`, `hosts.production`.
- Source tarball: `build/<TAG>.tar.gz`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, empty host list, health timeout, or unknown options.
- `usage` prints help and exits with status 0 or 2 when required arguments are missing.
