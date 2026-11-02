### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:54:00Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the queue worker lifecycle and reports whether its process and heartbeat are healthy.

#### Purpose
- Starts, stops, restarts, and checks the status or heartbeat health of the queue worker.
- Run directly with an action argument.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: action parsed by a `case` statement; accepted values are `start`, `stop`, `restart`, `status`, and `health`.
- Environment: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and, for `health`, the heartbeat file.

#### Output
- Starts the worker with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` when `kill` succeeds.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat Ns old`, `OK`, or usage text.
- Exit statuses: `0` for successful actions, running status, or healthy probe; `1` for stale heartbeat; `2` when the health probe finds the worker stopped; `3` when `status` finds it stopped; `64` for an unsupported action. Other command failures may propagate through the executing branch.

#### Core logic
- `start`: checks whether the PID is live; if not, launches the worker under `nohup` with the heartbeat path and records the background PID.
- `stop`: does nothing if the worker is not live; otherwise sends `kill` and removes the PID file on success.
- `restart`: runs stop, then start.
- `status`: prints `running` if the PID is live; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` if the worker is not live; otherwise compares heartbeat age to 120 seconds, exits `1` with a warning if stale, or prints `OK`.
- Unsupported action: prints usage to stderr and exits `64`.

#### Functions
- `is_running`: checks PID file and process liveness.
- `heartbeat_age`: computes heartbeat file age in seconds.
- `start_worker`: launches worker and records PID.
- `stop_worker`: kills worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `age=$(heartbeat_age)`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`; invoked with `--heartbeat` and the heartbeat file path.

#### Errors and exit codes
- `is_running` requires an existing PID file and a successful `kill -0`.
- Missing or unreadable heartbeat metadata falls back to timestamp `0`; its computed age can trigger the stale-heartbeat warning.
- `health`: exit `2` if not running; exit `1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`; otherwise `0`.
- `status`: exit `3` if stopped; otherwise `0`.
- Unsupported action: exit `64`.
- No traps, retries, or timeouts.
