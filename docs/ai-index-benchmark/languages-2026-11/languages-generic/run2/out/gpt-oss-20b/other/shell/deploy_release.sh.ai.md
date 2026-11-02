### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:34:23Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Distributes a specified release to all hosts in a chosen environment.
- Restarts the service on each host after deployment.
- Stops deployment if a host fails its health check.

#### Type
- Bash script (`deploy_release.sh`).

#### Input
- Command‑line options: `-e <environment>`, `-t <tag>`, optional `-n` (dry run), `-p <parallel>` (ignored), `-h` (help).
- Environment variables: `DEPLOY_USER`, `SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production` in script directory.
- Release tarball: `build/<tag>.tar.gz`.

#### Output
- Logs to stderr with timestamps.
- Deploys tarball to `/tmp/` on each host.
- Extracts to `/opt/releases/<tag>`, updates `/opt/releases/current` symlink.
- Restarts `orderd` service via `systemctl`.
- Health check via `curl` to `http://<host>:8080/health`.

#### Core logic
- Validate required tools (`rsync`, `ssh`, `curl`) and SSH key.
- Load host list from environment‑specific file.
- For each host:
  - If dry run, log intended action.
  - Rsync tarball to host.
  - SSH into host, create target dir, extract tarball, update symlink, restart service.
  - Poll health endpoint until success or timeout (30 s).
- Exit on first unhealthy host.

#### Public API
- `deploy_release.sh` (script entry point) – orchestrates deployment.
- `deploy_host(host)` – deploys to a single host.
- `wait_healthy(host)` – blocks until host health endpoint responds or times out.
- `check_prereqs()` – verifies required tools and SSH key.
- `load_hosts()` – reads host list file.
- `usage()` – prints usage information.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – default `deploy`.
- `SSH_KEY` – default `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT="/opt/releases"`.
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `HOST_FILES` mapping: `staging` → `hosts.staging`, `production` → `hosts.production`.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `date`, `sed`, `grep`, `mapfile`, `wait_healthy` (internal).
- Bash built‑ins: `set`, `printf`, `die`, `log`, `trap`, `getopts`.

#### Exceptions / Errors
- `die` exits with message on missing tools, unreadable SSH key, empty host list, or health timeout.
- `getopts` handles unknown or missing option arguments.
- Deployment stops immediately on first host that fails health check.
