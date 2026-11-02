### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:23:57Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Manages the queue worker lifecycle and checks whether its heartbeat is recent enough.

#### Purpose
- Starts, stops, restarts, and reports the status and heartbeat health of the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh`; no strict-mode settings.

#### Input
- `$1`: action, matched by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- Environment: `PID_FILE`, default `/run/queue-worker.pid`; `HEARTBEAT_FILE`, default `/var/lib/queue-worker/heartbeat`.
- Reads the PID from `PID_FILE`; reads the timestamp of `HEARTBEAT_FILE` for health checks.

#### Output
- Starts `WORKER_BIN` in the background with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` after a successful `kill`.
- Prints `already running`, `running`, `stopped`, `OK`, or a heartbeat warning; errors and usage go to stderr where specified.
- `status` exits 3 when stopped; `health` exits 2 when not running and 1 when the heartbeat is older than the limit. Invalid action exits 64. Other paths use the command or function return status.

#### Core logic
- `start`: if the PID file identifies a live process, print `already running`; otherwise launch the worker with `--heartbeat "$HEARTBEAT_FILE"` and save its PID.
- `stop`: if a live process is identified, send it `kill` and remove the PID file on success.
- `restart`: run `stop_worker`, then `start_worker`.
- `status`: print `running` if the PID file identifies a live process; otherwise print `stopped` and exit 3.
- `health`: exit 2 with `CRITICAL: not running` if the worker is not running; otherwise compare heartbeat age to the configured maximum, printing a warning and exiting 1 if exceeded, or printing `OK`.
- For an unrecognized action, print usage to stderr and exit 64.

#### Functions
- `is_running`: checks the PID file and process liveness.
- `heartbeat_age`: calculates seconds since the heartbeat file timestamp.
- `start_worker`: launches the worker and records its PID.
- `stop_worker`: kills the worker and removes its PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `age=$(heartbeat_age)`

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`.
- Uses `/run/queue-worker.pid` and `/var/lib/queue-worker/heartbeat` by default.

#### Errors and exit codes
- `status` exits 3 when the worker is stopped.
- `health` exits 2 when the worker is not running; exits 1 when heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Invalid action exits 64.
- No traps, retries, or timeouts.
