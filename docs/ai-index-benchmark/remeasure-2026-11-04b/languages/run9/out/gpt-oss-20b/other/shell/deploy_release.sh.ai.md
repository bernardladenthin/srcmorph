### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:22Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in an environment.
- Runs as a standalone script, not sourced.

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
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list)
  - `build/$TAG.tar.gz` (release archive)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts archive to `$RELEASE_ROOT/$TAG` on host.
- Creates symlink `$RELEASE_ROOT/current` → `$RELEASE_ROOT/$TAG`.
- Restarts `orderd` service with `systemctl`.
- Checks health endpoint `http://$host:8080/health`; exits on failure.
- Logs actions to stderr.
- Exit status 0 on success, 1 on error, 2 on missing arguments.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment-specific file.
- For each host:
  - If `DRY_RUN`, log intended action.
  - Else:
    - `rsync` archive to host.
    - SSH into host, create target dir, extract archive, update symlink, restart service.
    - Poll health endpoint until success or timeout.
  - Log deployment completion.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS` array.
- `deploy_host` – performs rsync, extraction, restart, health check for one host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – associative array mapping `staging` → `hosts.staging`, `production` → `hosts.production`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`
- Host files: `hosts.staging`, `hosts.production`
- Archive: `build/$TAG.tar.gz`

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, empty host list, or health timeout.
- Missing required options triggers usage and exit status 2.
- `wait_healthy` exits with status 1 if health check fails after `HEALTH_TIMEOUT` seconds.
