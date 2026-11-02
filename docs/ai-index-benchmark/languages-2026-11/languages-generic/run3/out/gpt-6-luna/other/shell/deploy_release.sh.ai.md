### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:38:40Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a tagged release to each host in staging or production, restarts `orderd`, and stops if a host fails its health check.

#### Purpose
- Roll out `build/<tag>.tar.gz` to environment hosts sequentially.
- Switch each host’s `current` release, restart the service, and verify health before continuing.

#### Type
- Bash deployment script.

#### Input
- CLI options: `-e` environment (`staging` or `production`), `-t` release tag, `-n` dry run, `-p` parallel count (accepted but ignored), `-h` help.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`.
- Reads `lib/common.sh` and the selected host list (`hosts.staging` or `hosts.production`) under `SCRIPT_DIR`.
- Reads release archive `build/<TAG>.tar.gz`.

#### Output
- Transfers and extracts the release on each host; updates `/opt/releases/current`; restarts `orderd`.
- Sends timestamped logs to stderr.
- Removes `/tmp/deploy_release.$$.lock` on exit; this script does not create the lock.

#### Core logic
- Resolves `SCRIPT_DIR`, sources `lib/common.sh`, and initializes deployment settings.
- Parses options; requires nonempty environment and tag.
- Checks for `rsync`, `ssh`, and `curl`, and verifies the SSH key is readable.
- Loads hosts by removing lines beginning with `#`; fails if the resulting list is empty.
- For each host, in order:
  - In dry-run mode, logs the planned deployment and skips deployment and health checks.
  - Otherwise, rsyncs the archive to `/tmp/`, extracts it into `/opt/releases/<TAG>`, points `/opt/releases/current` to that directory, and restarts `orderd`.
  - Polls `http://<host>:8080/health` every two seconds until successful or the timeout is reached.
  - Logs successful deployment before moving to the next host.

#### Public API
- CLI `deploy_release.sh -e <staging|production> -t <tag> [-n] [-p <parallel>]`: deploy a release sequentially.
- `log()` — write UTC timestamped message.
- `die()` — log error and exit.
- `usage()` — print usage comment.
- `check_prereqs()` — verify tools and SSH key.
- `load_hosts()` — read selected environment hosts.
- `deploy_host(host)` — deploy release and check host health.
- `wait_healthy(host)` — poll host health endpoint.
- `cleanup()` — remove PID-specific lock path.
- Top-level script entry point — parse options and deploy to loaded hosts; no `main` function.

#### Constants and configuration
- `SCRIPT_DIR` — directory containing the script; readonly.
- `DEPLOY_USER` — environment override, default `deploy`.
- `SSH_KEY` — environment override, default `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT` — exported as `/opt/releases`.
- `SERVICE_NAME` — `orderd`.
- `HEALTH_TIMEOUT` — `30` seconds.
- `ENVIRONMENT`, `TAG` — initially empty; set by `-e` and `-t`.
- `DRY_RUN` — initially `0`; set to `1` by `-n`.
- `HOSTS` — initially empty host array.
- `HOST_FILES` — `staging` → `hosts.staging`; `production` → `hosts.production`.
- `-p` — logs that parallel deployment is unsupported; does not affect deployment.

#### Dependencies
- `lib/common.sh`.
- External commands: `date`, `sed`, `command`, `grep`, `rsync`, `ssh`, `curl`, `mkdir`, `tar`, `ln`, `sudo`, `systemctl`, `sleep`, `rm`.
- Bash facilities: `getopts`, `mapfile`, arrays, process substitution, here-document, `trap`.

#### Exceptions / Errors
- Exits with an error if a required tool is missing, the SSH key is unreadable, the environment is unknown, or the selected host file has no hosts.
- Exits with status `2` when environment or tag is missing.
- Exits on unknown options or options requiring a missing value.
- With `set -euo pipefail`, command failures can terminate execution; a host that remains unhealthy for 30 seconds triggers an error and stops the rollout.
