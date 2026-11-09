### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:42Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release to all hosts in an environment.
- Runs sequentially; stops on first health failure.
- Can perform a dry‑run.

#### Type
- Bash (`#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- Options parsed with `getopts`:
  - `-e <environment>` → `ENVIRONMENT`
  - `-t <tag>` → `TAG`
  - `-n` → `DRY_RUN=1`
  - `-p <parallel>` → ignored (logged)
  - `-h` → prints usage
- Environment variables:
  - `DEPLOY_USER` (default `deploy`)
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
- Files read:
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list)
  - `build/$TAG.tar.gz` (release archive)

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts archive to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `orderd` service with `systemctl`.
- Checks health at `http://$host:8080/health`; exits on failure.
- Logs actions to stderr.
- Creates and removes `/tmp/deploy_release.$$.lock` on exit.

#### Core logic
- Parse options → set `ENVIRONMENT`, `TAG`, `DRY_RUN`.
- Verify prerequisites (`rsync`, `ssh`, `curl`, SSH key).
- Load host list from environment‑specific file.
- For each host:
  - If dry‑run: log intended action.
  - Else: `rsync` archive, SSH into host to extract, symlink, restart service.
  - Wait for health endpoint; fail if timeout.
  - Log successful deployment.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host file into `HOSTS` array.
- `deploy_host` – performs rsync, extraction, restart, health check.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – script directory.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – associative array mapping `staging` → `hosts.staging`, `production` → `hosts.production`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `systemctl`, `tar`, `mkdir`, `ln`, `rm`.
- Host files: `hosts.staging`, `hosts.production`.
- Release archive: `build/<TAG>.tar.gz`.

#### Errors and exit codes
- `die` exits with status `1` for missing tools, unreadable SSH key, empty host list, or health timeout.
- `getopts` errors exit with status `1` or `2` for missing arguments or unknown options.
- `wait_healthy` exits with `1` if health check fails after `HEALTH_TIMEOUT` seconds.
