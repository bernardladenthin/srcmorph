### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:57Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploys a tagged release to each staging or production host, restarts `orderd`, and stops if a host fails its health check.

#### Purpose
- Roll out `build/<tag>.tar.gz` to environment hosts one at a time.
- Run directly; sources `lib/common.sh`.

#### Type
- Bash; strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed by `getopts`.
- `-t <tag>` sets `TAG`.
- `-n` sets `DRY_RUN=1`; logs planned deployments without deploying.
- `-p <parallel>` logs that parallel deploys are unsupported and ignores the value.
- `-h` prints usage and exits.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; `HOME` supplies the default SSH-key location.
- Reads `lib/common.sh`, the selected `hosts.staging` or `hosts.production` file, and `build/<tag>.tar.gz`.
- Checks that the SSH key is readable.

#### Output
- Logs timestamps and deployment messages to stderr; `-h` prints usage to stdout.
- On remote hosts: copies the release tarball to `/tmp/`, extracts it under `/opt/releases/<tag>`, updates `/opt/releases/current`, and restarts `orderd` with `sudo systemctl`.
- Sends health checks to `http://<host>:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock file.

#### Core logic
- Parse options; require nonempty `ENVIRONMENT` and `TAG`.
- Check for `rsync`, `ssh`, and `curl`, and verify the SSH key is readable.
- Read the selected host file, skipping lines beginning with `#`; fail if no hosts are loaded.
- For each host in order:
  - In dry-run mode, log the planned deployment and return.
  - Copy `build/<tag>.tar.gz` to the host’s `/tmp/` using `rsync` over SSH.
  - Remotely create the release directory, extract the tarball, update the `current` symlink, and restart `orderd`.
  - Poll the host’s health endpoint every two seconds; log successful deployment before proceeding to the next host.

#### Functions
- `log`: Print timestamped messages to stderr.
- `die`: Log an error and exit with status 1.
- `usage`: Print the usage comment.
- `check_prereqs`: Check required commands and SSH key.
- `load_hosts`: Read the selected environment’s host list.
- `deploy_host`: Copy, install, and restart a release on one host.
- `wait_healthy`: Poll a host’s health endpoint until healthy.
- `cleanup`: Remove the PID-specific lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`.
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`.
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`.
- `RELEASE_ROOT="/opt/releases"` (exported).
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `ENVIRONMENT=""`.
- `TAG=""`.
- `DRY_RUN=0`.
- `HOSTS=()`.
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`.

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `date`, `sed`, `command`, `grep`, `rsync`, `ssh`, `curl`, `sleep`, `rm`, `mkdir`, `tar`, `sudo`, `systemctl`.
- Host-list files: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Release artifact: `build/$TAG.tar.gz`.
- Remote service: `orderd`; remote health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- Exit 1 via `die` for missing tools, unreadable SSH key, unknown environment, empty host list, or health-check timeout.
- Exit 2 if `ENVIRONMENT` or `TAG` is missing.
- Exit 0 for `-h`; successful completion otherwise returns 0.
- Unknown options and options missing values call `die`.
- Health checks retry every two seconds, up to `HEALTH_TIMEOUT` (30 seconds); stops deployment at the first failed host.
- `EXIT` trap runs `cleanup`, removing `/tmp/deploy_release.$$.lock`.
