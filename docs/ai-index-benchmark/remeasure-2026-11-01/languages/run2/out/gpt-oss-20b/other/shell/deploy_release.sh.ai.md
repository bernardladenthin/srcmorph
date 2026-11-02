### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:40:21Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in an environment.
- Runs locally; not intended as a library.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

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
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `$SERVICE_NAME` (`orderd`) via `systemctl`.
- Verifies health at `http://$host:8080/health`; exits on failure.
- Logs actions to stderr.
- Exit status 0 on success; non‑zero on errors or health timeout.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS` array.
- For each host:
  - `deploy_host`: if dry run, log; else:
    - `rsync` tarball.
    - SSH remote commands: create target dir, extract, symlink, restart service.
    - `wait_healthy`: poll health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list for environment.
- `deploy_host` – deploys to a single host.
- `wait_healthy` – polls health endpoint.
- `cleanup` – removes temporary lock file (unused).

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping `staging` → `hosts.staging`, `production` → `hosts.production`.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- Remote host: runs `bash`, `mkdir`, `tar`, `ln`, `sudo systemctl`.
- Host files: `hosts.staging`, `hosts.production`.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Missing required options cause exit status 2.
- Health check timeout (`HEALTH_TIMEOUT`) triggers `die` with message and exit 1.
