### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:57:51Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploys a tagged release to each staging or production host in sequence, restarts `orderd`, and stops if a host fails its health check.

#### Purpose
- Rolls out `build/$TAG.tar.gz` to every host listed for the selected environment.
- Run directly as a deployment command; it also sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed with `getopts`.
- `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; logs intended deployments without deploying.
- `-p <parallel>` logs that parallel deployments are unsupported and ignores the value.
- `-h` prints usage and exits successfully.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; reads `HOME` for that default.
- Reads `$SCRIPT_DIR/lib/common.sh`, the selected host file (`hosts.staging` or `hosts.production`), and `build/$TAG.tar.gz`; checks that `SSH_KEY` is readable.

#### Output
- Logs timestamped messages to standard error.
- Transfers the release archive to each host’s `/tmp/`; remotely extracts it under `$RELEASE_ROOT/$TAG`, updates `$RELEASE_ROOT/current`, and restarts the service.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock file.
- Exits `0` on success or help; exits `1` for deployment/prerequisite errors; exits `2` if `-e` or `-t` is missing.

#### Core logic
- Parses options, then requires both `ENVIRONMENT` and `TAG`.
- Checks for `rsync`, `ssh`, and `curl`, and verifies the SSH key is readable.
- Selects the environment’s host file, loads non-comment lines into `HOSTS`, and fails if none are loaded.
- Iterates through hosts sequentially:
  - In dry-run mode, logs the intended deployment and skips remote actions.
  - Otherwise, transfers the archive with `rsync`, then uses `ssh` to create the release directory, extract the archive, update the `current` symlink, and restart the service with `sudo systemctl`.
  - Polls the host’s health endpoint every two seconds; stops the rollout if health checks exceed the timeout.
  - Logs successful deployment to each host.

#### Functions
- `log`: Print timestamped messages to standard error.
- `die`: Log an error and exit.
- `usage`: Print the usage comment.
- `check_prereqs`: Verify tools and SSH key.
- `load_hosts`: Load hosts for the environment.
- `deploy_host`: Deploy and check one host.
- `wait_healthy`: Poll a host’s health endpoint.
- `cleanup`: Remove the lock-file path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`.
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`.
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`.
- `RELEASE_ROOT="/opt/releases"`.
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `ENVIRONMENT=""`.
- `TAG=""`.
- `DRY_RUN=0`.
- `HOSTS=()`.
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production"`).

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `basename` is not used; `bash`, `cd`, `command`, `curl`, `date`, `grep`, `ln`, `mapfile`, `mkdir`, `pwd`, `rm`, `rsync`, `sed`, `shift`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Host-list files: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Remote service: `orderd`; health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- Exit `1` if a required tool is missing, the SSH key is unreadable, the host file has no hosts, an environment is unknown, an option is invalid or missing a value, or a host fails its health check.
- Exit `2` if `ENVIRONMENT` or `TAG` is unset after option parsing.
- Exit `0` after successful completion or `-h`.
- `EXIT` trap runs `cleanup` and removes `/tmp/deploy_release.$$.lock`.
- Health checks retry every two seconds, up to `HEALTH_TIMEOUT=30`; there is no separate command timeout.
