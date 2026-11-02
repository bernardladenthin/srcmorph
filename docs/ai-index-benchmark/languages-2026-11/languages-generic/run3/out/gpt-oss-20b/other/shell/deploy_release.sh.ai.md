### deploy_release.sh
- H: 1.0
- C: A7D83074
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:40:28Z
- G: 
- A: 0.0.0
- X: file
---
> Deploys a release tarball to each host in an environment, restarting the service and verifying health.

#### Purpose
- Distribute a built release to multiple servers.
- Restart the target service after deployment.
- Verify service health before proceeding.

#### Type
- Bash script (`deploy_release.sh`).

#### Input
- Command‑line options: `-e <environment>`, `-t <tag>`, optional `-n` (dry‑run), `-p` (ignored), `-h`.
- Environment variables: `DEPLOY_USER`, `SSH_KEY`.
- Host list files: `hosts.staging`, `hosts.production` in `lib/`.

#### Output
- Logs to stderr.
- Deploys `build/<TAG>.tar.gz` to `/tmp/` on each host.
- Extracts to `/opt/releases/<TAG>`, updates `/opt/releases/current` symlink.
- Restarts `orderd` via `systemctl`.
- Health check via `curl` to `http://<host>:8080/health`.

#### Core logic
- Parse options; validate environment and tag.
- Ensure required tools (`rsync`, `ssh`, `curl`) and SSH key exist.
- Load host list from environment‑specific file.
- For each host:
  - If dry‑run: log intended action.
  - Else: `rsync` tarball, SSH into host, extract, symlink, restart service.
  - Wait until health endpoint responds or timeout (30 s).
- Exit on first host failure.

#### Public API
- `deploy_release.sh` (script entry point).
  - `usage()` – prints usage from comment.
  - `check_prereqs()` – verifies tools and SSH key.
  - `load_hosts()` – reads host list file.
  - `deploy_host(host)` – deploys to a single host.
  - `wait_healthy(host)` – polls health endpoint.
  - `cleanup()` – removes temporary lock file on exit.

#### Constants and configuration
- `SCRIPT_DIR` – directory of script.
- `DEPLOY_USER` – default `deploy`.
- `SSH_KEY` – default `$HOME/.ssh/deploy_ed25519`.
- `RELEASE_ROOT="/opt/releases"`.
- `SERVICE_NAME="orderd"`.
- `HEALTH_TIMEOUT=30`.
- `HOST_FILES` mapping: `staging` → `hosts.staging`, `production` → `hosts.production`.

#### Dependencies
- External commands: `rsync`, `ssh`, `curl`, `sed`, `grep`, `date`, `printf`, `mkdir`, `tar`, `ln`, `systemctl`, `wait`.
- Bash built‑ins: `set`, `shift`, `trap`, `declare`, `local`, `[[`, `((`, `until`, `sleep`.

#### Exceptions / Errors
- `die()` logs error and exits.
- Missing tools or SSH key triggers `die`.
- Empty host list triggers `die`.
- Health check timeout triggers `die`.
- Unknown or missing option arguments trigger `die`.
