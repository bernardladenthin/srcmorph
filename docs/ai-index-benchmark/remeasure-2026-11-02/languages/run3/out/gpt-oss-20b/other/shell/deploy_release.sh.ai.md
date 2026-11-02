### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:56:15Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Deploys a specified release tarball to all hosts in a chosen environment.
- Runs as a standalone script (not sourced).

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

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
  - `hosts.staging` or `hosts.production` in `$SCRIPT_DIR` based on `ENVIRONMENT`.
  - `build/$TAG.tar.gz` (source tarball).

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host via `rsync`.
- Extracts tarball to `$RELEASE_ROOT/$TAG` on host, updates `$RELEASE_ROOT/current` symlink.
- Restarts `orderd` service via `systemctl`.
- Checks health endpoint `http://$host:8080/health`; aborts if unhealthy after 30 s.
- Logs actions to stderr.
- Exit status 0 on success; non‑zero on missing prereqs, invalid options, or health failure.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment‑specific file.
- For each host:
  - If dry‑run, log intended action.
  - Else, `rsync` tarball, SSH into host to extract, symlink, restart service.
  - Wait for health endpoint; abort on timeout.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage line from script comment.
- `check_prereqs` – ensures tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS`.
- `deploy_host` – performs rsync, extraction, restart, health check.
- `wait_healthy` – polls health endpoint until success or timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – `deploy` (or env var).
- `SSH_KEY` – `$HOME/.ssh/deploy_ed25519` (or env var).
- `RELEASE_ROOT` – `/opt/releases`.
- `SERVICE_NAME` – `orderd`.
- `HEALTH_TIMEOUT` – `30`.
- `HOST_FILES` – associative array mapping `staging` → `hosts.staging`, `production` → `hosts.production`.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`.
- SSH key file: `$SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production`.
- Tarball: `build/$TAG.tar.gz`.

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, empty host list, or health timeout.
- `usage` exits with status 2 when required options missing.
- `getopts` errors exit with status 1.
- Health check timeout (`HEALTH_TIMEOUT`) triggers `die` with message `<host> not healthy after 30s`.
