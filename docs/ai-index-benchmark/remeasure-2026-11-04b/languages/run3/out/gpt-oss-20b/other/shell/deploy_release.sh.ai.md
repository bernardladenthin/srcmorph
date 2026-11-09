### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:24Z
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
- `bash`
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
  - `$SCRIPT_DIR/lib/common.sh`
  - `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production` based on `ENVIRONMENT`
  - `build/$TAG.tar.gz`

#### Output
- Copies `build/$TAG.tar.gz` to `/tmp/` on each host.
- Extracts to `$RELEASE_ROOT/$TAG` on each host.
- Creates symlink `$RELEASE_ROOT/current` → `$RELEASE_ROOT/$TAG`.
- Restarts `orderd` via `systemctl`.
- Checks health at `http://$host:8080/health`; exits on failure.
- Logs actions to stderr.
- Exit status 0 on success, 1 on error, 2 on missing arguments.

#### Core logic
- Parse options; validate `ENVIRONMENT` and `TAG`.
- Verify required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment-specific file.
- For each host:
  - If dry‑run, log intended action.
  - Else:
    - `rsync` tarball to host.
    - SSH into host, create target dir, extract tarball, update symlink, restart service.
    - Poll health endpoint until success or timeout.
  - Log deployment completion.

#### Functions
- `log` – prints timestamped message to stderr.
- `die` – logs error and exits.
- `usage` – prints usage from script comment.
- `check_prereqs` – ensures required tools and SSH key exist.
- `load_hosts` – reads host list file into `HOSTS`.
- `deploy_host` – performs deployment steps for a single host.
- `wait_healthy` – polls health endpoint with timeout.
- `cleanup` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR`=`/path/to/other/shell`
- `DEPLOY_USER`=`deploy`
- `SSH_KEY`=`$HOME/.ssh/deploy_ed25519`
- `RELEASE_ROOT`=`/opt/releases`
- `SERVICE_NAME`=`orderd`
- `HEALTH_TIMEOUT`=`30`
- `HOST_FILES`=`[staging]="hosts.staging" [production]="hosts.production"`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`
- External file: `$SCRIPT_DIR/lib/common.sh`
- Host list files: `hosts.staging`, `hosts.production`

#### Errors and exit codes
- `die` exits with status 1 for missing tools, unreadable SSH key, empty host list, or health check failure.
- Missing required options triggers usage and exits with status 2.
- Health check timeout (`HEALTH_TIMEOUT`) causes exit 1 after exceeding limit.
