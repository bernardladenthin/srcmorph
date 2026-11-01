### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:34:08Z
- G: 
- A: 0.0.0
- X: file
---
> Checks and controls a queue worker, including whether its heartbeat is fresh.

#### Purpose
- Provides init-style start, stop, restart, status, and health operations for the queue worker.
- Run directly with an operation as the first argument.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- First argument `$1`: operation, selected by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and, for health checks, the heartbeat file metadata.

#### Output
- Prints status messages: `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat ...s old`, or `OK`.
- Starts `queue-worker` in the background with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` after a successful `kill`.
- Exit statuses: `0` for successful operations and healthy status; `1` for stale heartbeat; `2` when health check finds no running worker; `3` for stopped status; `64` for an unsupported operation.

#### Core logic
- `start`: if the PID file identifies a running process, print `already running`; otherwise launch the worker with the heartbeat path and record its PID.
- `stop`: if running, send `TERM` and remove the PID file on successful `kill`; otherwise do nothing.
- `restart`: stop, then start.
- `status`: print `running` if active; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if not running; otherwise calculate heartbeat age, warn and exit `1` if age exceeds 120 seconds, or print `OK`.
- For any other operation, print usage to stderr and exit `64`.

#### Functions
- `is_running` — checks PID-file process.
- `heartbeat_age` — calculates heartbeat-file age.
- `start_worker` — launches the worker.
- `stop_worker` — stops the worker and removes its PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `nohup`, `kill`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`.
- Uses `/run/queue-worker.pid` by default for the PID file and `/var/lib/queue-worker/heartbeat` by default for heartbeat data.

#### Errors and exit codes
- `0`: successful operation; `status` finds the worker running; or `health` finds a fresh heartbeat.
- `1`: heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- `2`: `health` finds no running worker.
- `3`: `status` finds no running worker.
- `64`: unsupported operation.
- No traps, retries, or timeouts.
