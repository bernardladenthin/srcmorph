### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:12:50Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Deploys a tagged release of the `orderd` service across staging or production hosts, one host at a time, stopping when a host fails its health check.

#### Purpose
- Roll out `build/<tag>.tar.gz` to every host in the selected environment and restart `orderd`.
- Run directly; sources `lib/common.sh`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `-e <staging|production>` sets `ENVIRONMENT`; parsed with `getopts`.
- `-t <tag>` sets `TAG`; parsed with `getopts`.
- `-n` sets `DRY_RUN=1`; parsed with `getopts`.
- `-p <parallel>` is accepted and logged as unsupported; parsed with `getopts`.
- `-h` prints usage and exits; parsed with `getopts`.
- Environment variables: `DEPLOY_USER` defaults to `deploy`; `SSH_KEY` defaults to `$HOME/.ssh/deploy_ed25519`; `HOME` supplies that default.
- Reads the SSH key at `SSH_KEY`, the selected host list under `SCRIPT_DIR` (`hosts.staging` or `hosts.production`), and `build/$TAG.tar.gz`.
- Requires nonempty `ENVIRONMENT` and `TAG`; does not use remaining positional arguments.

#### Output
- Logs timestamped messages to stderr.
- On each remote host, uploads the tarball to `/tmp/`, extracts it under `/opt/releases/$TAG`, updates `/opt/releases/current`, and restarts `orderd` with `sudo systemctl`.
- Probes `http://<host>:8080/health`; removes `/tmp/deploy_release.$$.lock` on exit. The script does not create this lock file.
- Exits `0` on successful completion or `-h`; exits `1` through `die` for validation, prerequisite, host-list, or health-check failures; exits `2` when required environment or tag is missing. `set -e` may also terminate on an unhandled command failure.

#### Core logic
- Sources `lib/common.sh` relative to the script directory and sets deployment configuration.
- Parses options; prints usage for `-h`, rejects unknown options or missing option values, then shifts parsed arguments.
- Requires environment and tag; checks for `rsync`, `ssh`, and `curl`, and verifies the SSH key is readable.
- Selects the environment’s host file, removes lines beginning with `#`, and fails if no hosts remain.
- For each host, either logs the intended deployment in dry-run mode or uploads the tarball and runs remote extraction, symlink update, and service restart commands.
- After each real deployment, polls the host health endpoint every two seconds, failing at the health timeout; logs each completed host.
- Runs cleanup on exit.

#### Functions
- `log` — Timestamp and print to stderr.
- `die` — Log an error and exit `1`.
- `usage` — Print the usage comment.
- `check_prereqs` — Check tools and SSH key readability.
- `load_hosts` — Read hostnames for the selected environment.
- `deploy_host` — Upload and install a release on one host.
- `wait_healthy` — Poll a host’s health endpoint.
- `cleanup` — Remove the per-process lock path.

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
- `HOSTS=()` (indexed array)
- `HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")` (associative array)

#### Dependencies
- Sourced file: `"$SCRIPT_DIR/lib/common.sh"`.
- Commands: `rsync`, `ssh`, `curl`, `date`, `sed`, `command`, `grep`, `tar`, `mkdir`, `ln`, `sudo`, `systemctl`, `sleep`, `rm`.
- Host-list files: `hosts.staging`, `hosts.production`, selected under `SCRIPT_DIR`.
- Build artifact: `build/$TAG.tar.gz`; remote deployment uses `/tmp/$TAG.tar.gz`.
- Remote health endpoint: `http://$host:8080/health`.

#### Errors and exit codes
- `1`: `die` exits for missing tools, unreadable SSH key, unknown environment, empty host list, or a host not healthy before `HEALTH_TIMEOUT`.
- `2`: required `ENVIRONMENT` or `TAG` is empty.
- `0`: successful deployment or help request.
- `EXIT` trap invokes `cleanup`, removing `/tmp/deploy_release.$$.lock`; no retry beyond health polling, which checks every two seconds for up to 30 seconds.
