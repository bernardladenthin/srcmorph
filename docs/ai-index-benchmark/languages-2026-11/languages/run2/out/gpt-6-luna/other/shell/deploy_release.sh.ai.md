### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:32:45Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host in sequence, restarting the service and stopping when a host fails its health check.

#### Purpose
- Rolls out `build/$TAG.tar.gz` to every host in the selected environment, one host at a time.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash; strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; `-p <parallel>` is accepted but ignored, with a log message; `-h` prints usage and exits.
- `DEPLOY_USER` environment variable; defaults to `deploy`.
- `SSH_KEY` environment variable; defaults to `$HOME/.ssh/deploy_ed25519`.
- Reads `lib/common.sh`, the selected `hosts.staging` or `hosts.production` file under `SCRIPT_DIR`, and `build/$TAG.tar.gz`.
- Host files omit lines beginning with `#`.

#### Output
- Prints timestamped logs to standard error.
- Copies the release tarball to each remote host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, and restarts the remote `orderd` service.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock file.
- Exit status: `0` on success or help; `1` for `die` errors, including prerequisites, invalid environment, empty host list, or failed health check; `2` when required `-e` or `-t` is missing.

#### Core logic
- Parses options; rejects unknown options and options missing values.
- Requires nonempty `ENVIRONMENT` and `TAG`.
- Checks that `rsync`, `ssh`, and `curl` are available and that `SSH_KEY` is readable.
- Loads hosts from the selected environment file, excluding comment lines; fails if none remain.
- For each host, in file order:
  - With `-n`, logs that deployment would occur and skips deployment and health checks.
  - Otherwise, copies the tarball over SSH, extracts it remotely, updates the `current` symlink, and restarts `orderd`.
  - Polls the host health endpoint every two seconds until successful or the timeout is reached.
  - Logs successful deployment; stops at the first failure.
- Runs `cleanup` on exit via the `EXIT` trap.

#### Functions
- `log` — Print timestamped messages.
- `die` — Log an error and exit.
- `usage` — Print the usage comment.
- `check_prereqs` — Check tools and SSH key.
- `load_hosts` — Read the environment host list.
- `deploy_host` — Deploy and restart on one host.
- `wait_healthy` — Poll a host health endpoint.
- `cleanup` — Remove the temporary lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"` — script directory.
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`.
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`.
- `RELEASE_ROOT="/opt/releases"` — exported remote release root.
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `ENVIRONMENT=""`.
- `TAG=""`.
- `DRY_RUN=0`.
- `HOSTS=()` — indexed host list.
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")` — environment-to-file mapping.

#### Dependencies
- Sources `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `command`, `curl`, `date`, `dirname`, `grep`, `ln`, `mapfile`, `mkdir`, `pwd`, `rm`, `rsync`, `sed`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Remote deployment uses `tar`, `mkdir`, `ln`, and `sudo systemctl restart` over SSH; targets the `orderd` service.
- Health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- `die` exits `1`; used for missing tools, unreadable SSH key, unknown environment, empty host list, and health-check timeout.
- Missing `-e` or `-t` prints usage and exits `2`.
- `getopts` unknown-option and missing-value errors call `die` and exit `1`.
- `-h` prints usage and exits `0`.
- `wait_healthy` retries every two seconds; fails when `waited` reaches `HEALTH_TIMEOUT`.
- `trap cleanup EXIT` removes `/tmp/deploy_release.$$.lock` on exit.
