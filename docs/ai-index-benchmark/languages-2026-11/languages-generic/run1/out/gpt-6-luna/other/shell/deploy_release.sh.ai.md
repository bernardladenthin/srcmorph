### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:27:56Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host, restarts the `orderd` service, and stops if a host fails its health check.

#### Purpose
- Roll out `build/<tag>.tar.gz` to environment hosts sequentially.
- Restart `orderd` and wait for each host’s HTTP health endpoint before continuing.

#### Type
- Bash deployment script.

#### Input
- Options: `-e` environment (`staging` or `production`), `-t` release tag, `-n` dry run, `-p` ignored parallelism value, `-h` help.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`; `HOME` supplies the default SSH-key directory.
- Reads host list from the environment’s file under `SCRIPT_DIR`.
- Deploys archive `build/$TAG.tar.gz`.

#### Output
- Transfers and extracts each release under `$RELEASE_ROOT/$TAG` on each host.
- Updates `$RELEASE_ROOT/current`, restarts the service, and checks `http://$host:8080/health`.
- Logs timestamped messages to standard error.
- Removes `/tmp/deploy_release.$$.lock` on exit.

#### Core logic
- Enables `set -euo pipefail`; sources `lib/common.sh`.
- Validates required tools and readability of the SSH key.
- Selects `hosts.staging` or `hosts.production`; excludes lines beginning with `#` and fails if no hosts remain.
- For each host, either logs the intended deployment in dry-run mode or:
  - Transfers the archive with `rsync` over SSH.
  - Extracts it into the tag-specific release directory.
  - Points `current` to that release and restarts the service with `systemctl`.
  - Polls the health endpoint every two seconds, failing at the timeout.
- Processes hosts sequentially; a deployment or health-check failure stops the script.

#### Public API
- `log(message)` → writes timestamped message to standard error.
- `die(message)` → logs an error and exits with status 1.
- `usage()` → prints the usage comment.
- `check_prereqs()` → validates required tools and SSH key.
- `load_hosts()` → loads hosts for the selected environment.
- `deploy_host(host)` → deploys and checks one host, or logs in dry-run mode.
- `wait_healthy(host)` → polls the host health endpoint until healthy or timed out.
- `cleanup()` → removes the process-specific temporary lock path.
- Script entry point → parses options, validates inputs, checks prerequisites, loads hosts, then deploys each host.

#### Constants and configuration
- `SCRIPT_DIR` — directory containing this script.
- `DEPLOY_USER` — environment override; default `deploy`.
- `SSH_KEY` — environment override; default `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` — exported as `/opt/releases`.
- `SERVICE_NAME` — `orderd`.
- `HEALTH_TIMEOUT` — `30` seconds.
- `ENVIRONMENT`, `TAG` — initialized empty; set by `-e` and `-t`.
- `DRY_RUN` — initialized to `0`; set to `1` by `-n`.
- `HOSTS` — host list array, initially empty.
- `HOST_FILES` — `staging` → `hosts.staging`; `production` → `hosts.production`.
- Health endpoint — `http://$host:8080/health`.
- `-p` — logs that parallel deploys are unsupported and ignores the supplied value.

#### Dependencies
- Sources `lib/common.sh`.
- External commands: `bash`, `date`, `sed`, `command`, `grep`, `rsync`, `ssh`, `tar`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.
- Uses Bash built-ins and features: `getopts`, `mapfile`, `declare`, `printf`, `cd`, `pwd`, `dirname`, `shift`, `trap`.

#### Exceptions / Errors
- Exits if `rsync`, `ssh`, or `curl` is unavailable, or if the SSH key is unreadable.
- Exits for an unknown environment, an empty host list, missing required `-e`/`-t`, unknown option, or option missing a value.
- Exits if a host is unhealthy after 30 seconds.
- `set -euo pipefail` causes failures in commands, unset-variable use, and pipeline components to terminate the script.
