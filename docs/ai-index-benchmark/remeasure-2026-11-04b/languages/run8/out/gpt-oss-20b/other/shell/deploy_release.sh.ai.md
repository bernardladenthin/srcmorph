### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:25Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release to all hosts in an environment.
- Runs as a standalone script.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- `set -euo pipefail`

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
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list)
  - `build/$TAG.tar.gz` (release archive)

#### Output
- Copies `build/$TAG.tar.gz` to each host via `rsync`.
- Extracts archive to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current`.
- Restarts `orderd` service via `systemctl`.
- Checks health endpoint `http://$host:8080/health` until success or timeout.
- Logs actions to stderr.
- Exit status 0 on success; non‑zero on failure.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS` array.
- For each host:
  - `deploy_host`:
    - If `DRY_RUN`, log and skip.
    - `rsync` archive to `/tmp/`.
    - SSH into host, create target dir, extract archive, update symlink, restart service.
    - Call `wait_healthy` to poll health endpoint.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list file.
- `deploy_host` – deploys to a single host.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file (unused).

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – `([staging]="hosts.staging" [production]="hosts.production")`.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- Host files: `hosts.staging`, `hosts.production`.
- Archive: `build/$TAG.tar.gz`.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `getopts` errors exit with status 1.
- Missing required options exit with status 2.
- Health check timeout (`HEALTH_TIMEOUT` seconds) triggers `die` with message.
- `trap cleanup EXIT` ensures temporary lock file removal.
