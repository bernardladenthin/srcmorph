### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:17:42Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Roll out a release tarball to all hosts in a specified environment.
- Restart the service on each host and verify health.
- Can be run directly; not intended as a library.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- Options parsed by `getopts`:
  - `-e <environment>` → `ENVIRONMENT`.
  - `-t <tag>` → `TAG`.
  - `-n` → `DRY_RUN=1`.
  - `-p <parallel>` → ignored, logged.
  - `-h` → prints usage from script comment.
- Environment variables:
  - `DEPLOY_USER` (default `deploy`).
  - `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`).
- Files read:
  - `$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]}` (hosts list).
  - `build/$TAG.tar.gz` (tarball to deploy).

#### Output
- Logs to stderr with timestamps.
- Deploys `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current` symlink.
- Restarts `systemctl restart orderd`.
- Verifies health via `curl http://$host:8080/health`.
- Exit status 0 on success; non‑zero on missing prereqs, invalid options, or health timeout.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list file into `HOSTS` array.
- For each `host` in `HOSTS`:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service.
  - `wait_healthy`: poll health endpoint until success or timeout.
  - Log deployment.

#### Functions
- `log`: timestamped stderr output.
- `die`: error log and exit.
- `usage`: prints usage from script comment.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list file.
- `deploy_host`: deploys tarball to a host.
- `wait_healthy`: polls health endpoint.
- `cleanup`: removes temporary lock file (unused in script).

#### Constants and configuration
- `SCRIPT_DIR`=`$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)`
- `DEPLOY_USER`=`${DEPLOY_USER:-deploy}`
- `SSH_KEY`=`${SSH_KEY:-$HOME/.ssh/deploy_ed25519}`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `ENVIRONMENT`=`""`
- `TAG`=`""`
- `DRY_RUN`=`0`
- `HOSTS`=()
- `HOST_FILES`=([staging]="hosts.staging" [production]="hosts.production")

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- SSH key file: `$SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production` in script directory.
- Tarball: `build/$TAG.tar.gz`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, unknown environment, empty host list, health timeout, or unknown/required options.
- Usage errors exit with status 2.
- `wait_healthy` exits with status 1 if health check fails after `HEALTH_TIMEOUT` seconds.
