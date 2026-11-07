### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:29Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Manages the queue worker lifecycle and checks whether its heartbeat is recent enough to indicate worker health.

#### Purpose
- Provides `start`, `stop`, `restart`, `status`, and `health` operations for the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: operation name: `start`, `stop`, `restart`, `status`, or `health`; parsed by a `case` statement.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file at `PID_FILE` and checks the heartbeat file at `HEARTBEAT_FILE`.

#### Output
- Starts `/usr/local/bin/queue-worker` with `--heartbeat` and redirects its output to `/dev/null`; records its PID in `PID_FILE`.
- Removes `PID_FILE` after a successful `kill` in `stop`.
- Prints lifecycle and health messages, including `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat Ns old`, `OK`, or usage text.
- Exit statuses: `0` for successful operations and healthy status; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when `status` finds it stopped; `64` for an unrecognized operation.

#### Core logic
- `start`: if `is_running` succeeds, prints `already running`; otherwise launches the worker with `nohup` in the background and writes `$!` to `PID_FILE`.
- `stop`: returns successfully if the worker is not running; otherwise sends `kill` to the PID from `PID_FILE` and removes the PID file on successful termination request.
- `restart`: runs `stop_worker`, then `start_worker`.
- `status`: prints `running` if the PID file identifies a live process; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with a critical message if the worker is not running; otherwise calculates heartbeat age, warns and exits `1` if it exceeds `MAX_HEARTBEAT_AGE`, or prints `OK`.

#### Functions
- `is_running`: Checks PID file and process liveness.
- `heartbeat_age`: Computes heartbeat file age in seconds.
- `start_worker`: Launches worker and records its PID.
- `stop_worker`: Stops worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat`.
- Uses `/run/queue-worker.pid` by default for the PID file and `/var/lib/queue-worker/heartbeat` by default for the heartbeat file.

#### Errors and exit codes
- `0`: Successful operation; `status` reports a running worker; `health` reports a fresh heartbeat.
- `1`: `health` finds heartbeat age greater than `MAX_HEARTBEAT_AGE`.
- `2`: `health` finds the worker is not running.
- `3`: `status` finds the worker is stopped.
- `64`: Unrecognized operation; prints usage to standard error.
- No traps, retries, or timeouts.
