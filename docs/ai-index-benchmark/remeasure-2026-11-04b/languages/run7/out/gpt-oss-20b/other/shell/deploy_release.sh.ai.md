### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:28Z
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
  - `hosts.staging` or `hosts.production` in `$SCRIPT_DIR`
  - `build/$TAG.tar.gz`

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current`.
- Restarts `orderd` via `systemctl`.
- Prints deployment status to stderr.
- Exits with `1` on errors, `2` on missing arguments.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment-specific file.
- For each host:
  - If dry‑run, log action and skip.
  - `rsync` tarball to host.
  - SSH into host, extract tarball, update symlink, restart service.
  - Poll `/health` endpoint until success or timeout.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – ensures tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS`.
- `deploy_host` – performs deployment steps for one host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file (unused).

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping environments to host files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- Host files: `hosts.staging`, `hosts.production`.
- Remote host directories: `/tmp/`, `$RELEASE_ROOT/`.

#### Errors and exit codes
- `1` – missing prerequisites, unreadable SSH key, empty host list, deployment failure, health check timeout.
- `2` – missing required options (`-e`, `-t`).
- `die` triggers exit `1` with error message.
