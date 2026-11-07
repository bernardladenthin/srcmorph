### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:58Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Deploy a tagged release to every host in a staging or production environment, restarting and health-checking the service host by host.

#### Purpose
- Deploys `build/$TAG.tar.gz` to each configured environment host, updates the current release, and restarts `orderd`; stops on the first failed health check.
- Runs directly; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`.
- `-t <tag>` sets `TAG`.
- `-n` sets `DRY_RUN=1`; logs intended deployments without deploying.
- `-p <parallel>` logs that parallel deployment is unsupported and ignores the value.
- `-h` prints usage and exits successfully.
- `getopts` parses options; unknown options and missing option values are errors.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`.
- Reads `lib/common.sh`, the selected `$SCRIPT_DIR/hosts.staging` or `$SCRIPT_DIR/hosts.production`, and `build/$TAG.tar.gz` during deployment.
- Requires nonempty `ENVIRONMENT` and `TAG`; extra positional arguments are shifted away and otherwise unused.

#### Output
- Logs timestamped status and error messages to stderr.
- Transfers the release archive to each host’s `/tmp/`; remotely extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd`.
- Sends health-check requests to `http://$host:8080/health`.
- Removes `/tmp/deploy_release.$$.lock` on exit; the script does not create this lock.
- Exits `0` on success or help; exits `2` if required environment or tag is missing; exits `1` for `die` errors. Strict-mode failures can also terminate the script nonzero.

#### Core logic
- Parses options and shifts consumed arguments; validates that `ENVIRONMENT` and `TAG` are set.
- Checks for `rsync`, `ssh`, and `curl`, and verifies that `SSH_KEY` is readable.
- Selects the host-list file from `HOST_FILES`; reads lines not beginning with `#` into `HOSTS` and rejects an empty list.
- For each host, `deploy_host` either logs the intended action in dry-run mode or transfers the archive and runs remote deployment commands.
- Remote commands create the release directory, extract the archive, repoint `current`, and restart the service.
- `wait_healthy` polls the host health endpoint every two seconds; deployment stops if the host remains unhealthy for `HEALTH_TIMEOUT`.
- Logs successful deployment per host; an `EXIT` trap removes the lock path.

#### Functions
- `log` — Timestamp and print to stderr.
- `die` — Log an error and exit `1`.
- `usage` — Print the usage comment.
- `check_prereqs` — Check required commands and SSH key.
- `load_hosts` — Load hosts for the selected environment.
- `deploy_host` — Transfer, install, and restart a release.
- `wait_healthy` — Poll a host’s health endpoint.
- `cleanup` — Remove the temporary lock path.

#### Constants and configuration
- `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"` (readonly)
- `DEPLOY_USER="${DEPLOY_USER:-deploy}"`
- `SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"`
- `RELEASE_ROOT="/opt/releases"` (exported)
- `SERVICE_NAME="orderd"`
- `HEALTH_TIMEOUT=30`
- `ENVIRONMENT=""`
- `TAG=""`
- `DRY_RUN=0`
- `HOSTS=()`
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")`

#### Dependencies
- Sourced file: `$SCRIPT_DIR/lib/common.sh`.
- External commands: `bash`, `command`, `curl`, `date`, `grep`, `mapfile`, `mkdir`, `rm`, `rsync`, `sed`, `shift`, `sleep`, `ssh`, `sudo`, `systemctl`, `tar`.
- Host-list files: `$SCRIPT_DIR/hosts.staging`, `$SCRIPT_DIR/hosts.production`.
- Remote service: `orderd`; health endpoint pattern: `http://$host:8080/health`.

#### Errors and exit codes
- `1`: `die` errors, including missing prerequisite, unreadable SSH key, unknown environment, empty host list, or failed health timeout.
- `2`: `ENVIRONMENT` or `TAG` is empty after option parsing.
- `0`: successful completion or `-h`.
- Unknown option and option missing a value call `die`.
- `set -euo pipefail` may also terminate on command failures or unset-variable use.
- `EXIT` trap calls `cleanup`, which removes `/tmp/deploy_release.$$.lock`.
- Health checks retry every two seconds until success or `HEALTH_TIMEOUT=30`; no retry is performed for deployment commands.
