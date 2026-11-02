### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:25:23Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in a chosen environment.
- Runs locally or as a standalone script.

#### Type
- Bash (`#!/usr/bin/env bash`)
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
  - `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production` based on `ENVIRONMENT`

#### Output
- Logs to stderr with timestamps.
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on host, updates `$RELEASE_ROOT/current` symlink.
- Restarts `orderd` service via `systemctl`.
- Verifies health at `http://$host:8080/health`; exits on failure.
- Removes temporary lock file on exit.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list from environment-specific file.
- For each host:
  - `deploy_host`: if dry‑run, log action; otherwise:
    - `rsync` tarball to host.
    - SSH into host, create target dir, extract tarball, update symlink, restart service.
    - `wait_healthy` polls health endpoint until success or timeout.
  - Log deployment success.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list into `HOSTS`.
- `deploy_host` – deploys tarball to a single host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file.

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
- Remote host endpoints: `http://$host:8080/health`.
- Local files: `build/$TAG.tar.gz`, host list files.

#### Errors and exit codes
- `die` exits with status `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or health check failure.
- `usage` exits with status `2` when required options missing.
- `wait_healthy` exits with status `1` if health check fails after `HEALTH_TIMEOUT` seconds.
