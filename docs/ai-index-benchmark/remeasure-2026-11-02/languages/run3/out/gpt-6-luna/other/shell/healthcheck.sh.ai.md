### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:54:52Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Manages a queue worker process and checks whether its heartbeat is recent enough to indicate healthy operation.

#### Purpose
- Starts, stops, restarts, reports status for, and health-checks the queue worker.
- Run directly with an action argument; not a sourced library.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: action (`start`, `stop`, `restart`, `status`, or `health`).
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and the heartbeat file’s modification time.

#### Output
- Starts `/usr/local/bin/queue-worker` with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Removes `PID_FILE` after a successful stop.
- Prints `already running`, `running`, `stopped`, health results, or usage text.
- Exit statuses: `0` for successful actions and healthy status; `1` for a stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an unsupported action.

#### Core logic
- Checks whether `PID_FILE` exists and its PID responds to `kill -0`.
- `start`: if already running, prints `already running`; otherwise starts the worker with `--heartbeat "$HEARTBEAT_FILE"` under `nohup` and records `$!`.
- `stop`: if running, sends `kill` to the PID and removes `PID_FILE` on success.
- `restart`: stops the worker, then starts it.
- `status`: prints `running` or prints `stopped` and exits `3`.
- `health`: exits `2` with `CRITICAL: not running` if stopped; otherwise calculates heartbeat age and exits `1` with a warning if age exceeds `MAX_HEARTBEAT_AGE`; prints `OK` otherwise.
- Unsupported action: prints usage to standard error and exits `64`.

#### Functions
- `is_running`: checks PID file and process.
- `heartbeat_age`: calculates age from heartbeat modification time.
- `start_worker`: starts worker and records PID.
- `stop_worker`: terminates worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`.
- Worker option: `--heartbeat`; receives the configured heartbeat file path.

#### Errors and exit codes
- `0`: successful action, running status, healthy heartbeat, or worker already running.
- `1`: heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- `2`: health check finds the worker not running.
- `3`: status check finds the worker stopped.
- `64`: unsupported action.
- No traps, retries, or timeouts.
