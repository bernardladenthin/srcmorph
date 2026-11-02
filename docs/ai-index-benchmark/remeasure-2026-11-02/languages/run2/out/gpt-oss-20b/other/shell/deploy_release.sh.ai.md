### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:41:43Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts of a chosen environment.
- Runs locally or as a standalone script.

#### Type
- Bash (`#!/usr/bin/env bash`)
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
  - `hosts.staging` or `hosts.production` in `$SCRIPT_DIR`
  - `build/$TAG.tar.gz` (source tarball)

#### Output
- Logs to stderr with timestamps.
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `orderd` service via `systemctl`.
- Verifies health at `http://$host:8080/health`; exits on failure.
- Removes temporary lock file on exit.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list from environment-specific file.
- For each host:
  - `deploy_host`: rsync tarball, SSH to extract, symlink, restart service, wait for health.
  - Log deployment success.
- Exit on first host that fails health check.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage comment from script header.
- `check_prereqs` – verifies required tools and SSH key.
- `load_hosts` – loads host list into `HOSTS` array.
- `deploy_host` – deploys to a single host, handles dry‑run.
- `wait_healthy` – polls health endpoint until timeout.
- `cleanup` – removes temporary lock file.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT` – `/opt/releases`
- `SERVICE_NAME` – `orderd`
- `HEALTH_TIMEOUT` – `30`
- `HOST_FILES` – `([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`
- External files: `build/$TAG.tar.gz`, `hosts.staging`, `hosts.production`
- System service: `orderd` (restarted via `systemctl`)

#### Errors and exit codes
- `die` exits with status `1` for missing tools, unreadable SSH key, unknown environment, empty host list, or health timeout.
- `usage` exits with status `0` when `-h` is used.
- Missing required options exit with status `2`.
- `wait_healthy` exits with status `1` if health check fails after `HEALTH_TIMEOUT` seconds.
