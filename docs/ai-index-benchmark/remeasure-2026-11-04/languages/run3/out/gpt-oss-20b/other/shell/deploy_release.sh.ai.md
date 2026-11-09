### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:44Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release to all hosts in an environment.
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
  - `$SCRIPT_DIR/lib/common.sh`.
  - `$SCRIPT_DIR/hosts.staging` or `hosts.production` based on `ENVIRONMENT`.

#### Output
- Logs to stderr.
- Copies `build/$TAG.tar.gz` to each host via `rsync`.
- Extracts tarball on host, updates symlink, restarts `orderd` service.
- Verifies health at `http://$host:8080/health`.
- Exits with status 1 on errors, 0 on success.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- `check_prereqs`: ensure `rsync`, `ssh`, `curl` exist and SSH key readable.
- `load_hosts`: read host list from environment-specific file.
- For each host:
  - `deploy_host`: rsync tarball, SSH into host to extract, symlink, restart service, wait for health.
  - Log deployment success.

#### Functions
- `log`: prints timestamped message to stderr.
- `die`: logs error and exits.
- `usage`: prints usage comment from script header.
- `check_prereqs`: verifies required tools and SSH key.
- `load_hosts`: loads host list into `HOSTS`.
- `deploy_host`: deploys release to a single host.
- `wait_healthy`: polls health endpoint until timeout.
- `cleanup`: removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy`.
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping environments to host list files.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- SSH key file: `$SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production`.
- Remote tarball path: `build/$TAG.tar.gz`.

#### Errors and exit codes
- `die` exits with status 1 on missing tools, unreadable key, empty host list, or health timeout.
- `usage` prints help and exits with status 0.
- Missing required options cause exit status 2 after printing usage.
