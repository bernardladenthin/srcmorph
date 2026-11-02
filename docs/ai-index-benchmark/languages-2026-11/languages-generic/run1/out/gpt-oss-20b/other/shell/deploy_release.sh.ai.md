### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:30:12Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Automates release deployment across multiple hosts.
- Ensures service restart and health check after each deployment.

#### Type
- Bash script (`deploy_release.sh`).

#### Input
- Command‑line options: `-e <environment>`, `-t <tag>`, optional `-n` (dry run), `-p <parallel>` (ignored), `-h`.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production` in script directory.

#### Output
- Deploys `build/<TAG>.tar.gz` to `/tmp/` on each host.
- Extracts to `/opt/releases/<TAG>`, updates `/opt/releases/current` symlink.
- Restarts `orderd` service via `systemctl`.
- Logs progress to stderr.
- Exits on first host health‑check failure.

#### Core logic
- Validate required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment‑specific file.
- For each host:
  - If dry run, log intended action.
  - Rsync tarball to host.
  - SSH into host, create target dir, extract tarball, update symlink, restart service.
  - Poll `/health` endpoint until success or timeout (30 s).
- Clean up temporary lock file on exit.

#### Public API
- `deploy_release.sh` (script entry point)  
  - Parameters: `-e <env>`, `-t <tag>`, optional `-n`, `-p`, `-h`.  
  - Purpose: orchestrate deployment across hosts.

#### Constants and configuration
- `DEPLOY_USER` (default `deploy`)
- `SSH_KEY` (default `$HOME/.ssh/deploy_ed25519`)
- `RELEASE_ROOT="/opt/releases"`
- `SERVICE_NAME="orderd"`
- `HEALTH_TIMEOUT=30`
- `HOST_FILES` mapping: `staging` → `hosts.staging`, `production` → `hosts.production`

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `date`, `sed`, `grep`, `mapfile`, `wait_healthy` (internal).
- Bash built‑ins: `set`, `printf`, `die`, `log`, `trap`, `getopts`.

#### Exceptions / Errors
- Missing required tools → `die "missing tool: <tool>"`
- Unreadable SSH key → `die "cannot read SSH key ..."`
- Unknown environment → `die "unknown environment: $ENVIRONMENT"`
- No hosts in file → `die "no hosts in $file"`
- Health check timeout → `die "$host not healthy after ${HEALTH_TIMEOUT}s"`
- Invalid command‑line options → `die "unknown option -$OPTARG"` or `die "option -$OPTARG needs a value"`
