### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:32:39Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each staging or production host, restarts `orderd`, and stops if a host fails its health check.

#### Purpose
- Distributes `build/<tag>.tar.gz` to the configured hosts for one environment.
- Installs each release, switches the `current` symlink, restarts the service, and checks host health before continuing.

#### Type
- Bash deployment script; sources `lib/common.sh`.

#### Input
- Options: `-e` environment, `-t` release tag, `-n` dry run, `-p` parallel count, `-h` help.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`; defaults are `deploy` and `$HOME/.ssh/deploy_ed25519`.
- Reads the selected host file under `SCRIPT_DIR` and the local archive `build/$TAG.tar.gz`.
- Requires `rsync`, `ssh`, `curl`, and a readable SSH key.

#### Output
- Transfers and extracts the release on each host; updates `$RELEASE_ROOT/current`; restarts the service with `systemctl`.
- Logs progress and errors to standard error with UTC timestamps.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create that lock file.

#### Core logic
- Parses options; requires nonempty `ENVIRONMENT` and `TAG`.
- Checks required tools and SSH key readability, then loads hosts from the environment-specific file, excluding lines beginning with `#`.
- Processes hosts sequentially. In dry-run mode, logs the intended deployment without transferring, restarting, or checking health.
- For each real deployment, rsyncs the archive to the host’s `/tmp/`, extracts it under `$RELEASE_ROOT/$TAG`, points `current` at that release, and restarts `SERVICE_NAME`.
- Polls `http://<host>:8080/health` every two seconds; aborts on the first host that remains unhealthy through `HEALTH_TIMEOUT`.
- Exits on errors via `set -euo pipefail`; `-p` is accepted but ignored with a log message.

#### Public API
- `log(...)` — timestamped stderr logging.
- `die(...)` — logs an error and exits.
- `usage()` — prints the usage comment.
- `check_prereqs()` — validates tools and SSH key.
- `load_hosts()` — loads hosts for the selected environment.
- `deploy_host(host)` — deploys one release to one host.
- `wait_healthy(host)` — polls the host health endpoint.
- `cleanup()` — removes the per-process temporary lock path.
- Script entry point — parses options and deploys to all loaded hosts.

#### Constants and configuration
- `SCRIPT_DIR` — directory containing the script; readonly.
- `RELEASE_ROOT` — `/opt/releases`; exported.
- `SERVICE_NAME` — `orderd`.
- `HEALTH_TIMEOUT` — `30` seconds.
- `HOST_FILES` — `staging` → `hosts.staging`; `production` → `hosts.production`.
- `DEPLOY_USER` — environment override or `deploy`.
- `SSH_KEY` — environment override or `$HOME/.ssh/deploy_ed25519`.
- `DRY_RUN` — defaults to `0`; set to `1` by `-n`.
- `ENVIRONMENT`, `TAG` — initially empty; set by `-e` and `-t`.
- `HOSTS` — initially empty host array.
- `-p` — parallel count argument is logged and ignored.

#### Dependencies
- `lib/common.sh`.
- External commands: `date`, `sed`, `command`, `grep`, `mapfile`, `rsync`, `ssh`, `tar`, `mkdir`, `ln`, `sudo`, `systemctl`, `curl`, `sleep`, `rm`.

#### Exceptions / Errors
- Exits with an error for missing tools, unreadable SSH key, unknown environment, empty host list, unknown option, or an option missing its value.
- Exits with status `2` when required environment or tag is absent; `-h` prints usage and exits successfully.
- Exits if any host fails health checks within 30 seconds; shell strict mode also terminates on command failures.
